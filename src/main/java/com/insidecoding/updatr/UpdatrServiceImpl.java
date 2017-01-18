package com.insidecoding.updatr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insidecoding.updatr.model.CheckForVersionResult;
import com.insidecoding.updatr.model.UpdatrSession;
import com.insidecoding.updatr.model.Version;

/**
 * Implementation of the {@code UpdaterService}.
 * 
 * @author ludovicianul
 *
 */
public final class UpdatrServiceImpl implements UpdatrService {
  private static final int TIMEOUT = 60000;

  private static final String DOWNLOAD_URL = "download_url";
  private static final String CURRENT_VERSION = "current_version";
  private static final String RELEASE_NOTES = "release_notes";

  private static final Logger LOG = LoggerFactory.getLogger(UpdatrService.class);

  @Override
  public CheckForVersionResult checkForNewVersion(final String updatrUrl, final String existingVersion)
      throws IOException, InvalidUpdatrFormatException {
    Map<String, String> properties = this.loadPropertiesMap(updatrUrl);
    Version newVersion = Version.fromString(properties.get(CURRENT_VERSION));
    Version currentVersion = Version.fromString(existingVersion);
    LOG.info("Checking for updates at: " + updatrUrl);
    LOG.info("Comparing current version: " + currentVersion + " with available version: " + newVersion);

    CheckForVersionResult.Builder checkBuilder = new CheckForVersionResult.Builder(existingVersion);

    if (newVersion.compareTo(currentVersion) > 0) {
      checkBuilder.withIsNewVersion(true).withDownloadUrl(properties.get(DOWNLOAD_URL)).withReleaseNotesUrl(properties.get(RELEASE_NOTES))
          .withAvailableVersion(properties.get(CURRENT_VERSION));
      LOG.info("New version available");
    } else {
      checkBuilder.withIsNewVersion(false);
      LOG.info("No new version available");
    }

    return checkBuilder.build();
  }

  @Override
  public boolean performUpdate(final UpdatrSession session, final UpdatrCallback callback) throws IOException, UpdatrProcessingException {
    File projectJar = new File(session.getAppName() + "-" + session.getAvailableVersion() + ".jar");
    LOG.info("Downloading the new version to file: " + projectJar.getPath());

    this.loadUrlToFile(session.getDownloadUrl(), projectJar);
    this.replaceNewVersionInScript(session);
    this.printReleaseNotes(session.getReleaseNotesUrl());

    LOG.info("!!! Re-launching app...!!!");
    this.relaunchApp(session);
    callback.processResult(session);

    return true;
  }

  private void relaunchApp(UpdatrSession session) {
    try {
      ProcessBuilder ps = new ProcessBuilder(session.getScriptCommand());
      ps.redirectErrorStream(true);
      Process pr = ps.start();

      try (BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
        String line;
        while ((line = in.readLine()) != null) {
          LOG.info(line);
        }
        pr.waitFor();
        LOG.info("Finished running with the new version!");
      }
    } catch (Exception e) {
      LOG.warn("Application restart failed. Please re-run the application in order to benefit from the last update!", e);
    }
  }

  private Map<String, String> loadPropertiesMap(final String updatrUrl) throws IOException, InvalidUpdatrFormatException {
    URL url = new URL(updatrUrl);
    Map<String, String> properties = new HashMap<String, String>();
    boolean valid = true;
    try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
      String inputLine;

      String[] lineItems;
      while ((inputLine = in.readLine()) != null) {
        LOG.debug(inputLine);

        lineItems = inputLine.split("=");

        if (lineItems.length > 1) {
          properties.put(lineItems[0].trim(), inputLine.substring(inputLine.indexOf("=") + 1));
        } else {
          LOG.info("Invalid line: " + inputLine);
        }
      }
    }

    if (!valid || properties.isEmpty()) {
      throw new InvalidUpdatrFormatException();
    }

    return properties;
  }

  private void printReleaseNotes(final String releaseNotesUrl) throws IOException {
    File tempReleaseNotes = File.createTempFile("release_notes", "tmp");
    this.loadUrlToFile(releaseNotesUrl, tempReleaseNotes);

    try (BufferedReader reader = new BufferedReader(new FileReader(tempReleaseNotes))) {
      String line = null;
      LOG.info("============= Release notes ================");
      while ((line = reader.readLine()) != null) {
        LOG.info(line);
      }
    }

    LOG.info("============================================");
  }

  private void loadUrlToFile(final String url, final File outputFile) throws IOException {
    LOG.info("Downloading " + outputFile.getName() + " from url: [" + url + "]");

    if (url == null || url.trim().isEmpty()) {
      LOG.info("Empty URL received. Skipping...");
      return;
    }

    URL website = new URL(url);
    URLConnection connection = website.openConnection();
    connection.setReadTimeout(TIMEOUT);

    try (ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(outputFile);) {

      long expectedSize = connection.getContentLength();
      LOG.info("Expected size: " + expectedSize);

      if (expectedSize == -1) {
        throw new IOException("The provided URL [ " + url + "] does not contain a valid file!");
      }

      long transferedSize = 0L;

      while (transferedSize < expectedSize) {
        transferedSize += fos.getChannel().transferFrom(rbc, transferedSize, 1 << 24);
        LOG.info(transferedSize + " bytes received");
      }

    }

    LOG.info("Download " + outputFile.getName() + " done!");
  }

  private void replaceNewVersionInScript(final UpdatrSession session) throws IOException {
    List<String> fileLines = new ArrayList<String>();

    LOG.info("Replacing the app running script.");
    try (BufferedReader reader = new BufferedReader(new FileReader(new File(session.getScriptName())))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        line = line.replaceAll(session.getAppName() + "-" + session.getExistingVersion() + ".[a-zA-Z]*",
            session.getAppName() + "-" + session.getAvailableVersion() + ".jar");

        fileLines.add(line);
      }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(session.getScriptName())))) {
      for (String toWrite : fileLines) {
        writer.write(toWrite);
        writer.newLine();
      }
    }
    LOG.info("Script updated successfuly");
  }
}
