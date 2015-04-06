package com.insidecoding.updatr;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Implementation of the ({@code UpdaterService}.
 * 
 * @author ludovicianul
 *
 */
public class UpdatrServiceImpl implements UpdatrService {
	private static final int BUFFER_SIZE = 1024;
	private static final int TIMEOUT = 60000;

	private static final Logger LOG = LoggerFactory.getLogger(UpdatrService.class);

	@Inject
	@Named("updatrUrl")
	private String updatrUrl;

	@Inject
	@Named("existingVersion")
	private String existingVersion;

	@Inject
	@Named("currentVersionKey")
	private String currentVersionKey;

	@Inject
	@Named("downloadUrlKey")
	private String downloadUrlKey;

	@Inject
	@Named("releaseNotesKey")
	private String releaseNotesKey;

	@Inject
	@Named("newLibsKey")
	private String newLibsKey;

	@Inject
	@Named("projectName")
	private String projectName;

	@Inject
	@Named("scriptName")
	private String scriptName;

	private void runPreconditions() {
		Preconditions.checkNotNull(updatrUrl);
		Preconditions.checkNotNull(existingVersion);
		Preconditions.checkNotNull(currentVersionKey);
		Preconditions.checkNotNull(downloadUrlKey);
		Preconditions.checkNotNull(releaseNotesKey);
		Preconditions.checkNotNull(newLibsKey);
	}

	@Override
	public final UpdatrResult checkForNewVersion(UpdatrCallback callback) {
		this.runPreconditions();
		UpdatrResult result = UpdatrResult.create().withExistingVersion(existingVersion);

		try {
			Map<String, String> properties = this.loadPropertiesMap();

			Version newVersion = Version.fromString(properties.get(currentVersionKey));
			Version currentVersion = Version.fromString(existingVersion);

			if (newVersion.compareTo(currentVersion) > 0) {
				result.newVersionAvailable(true).withDownloadUrl(properties.get(downloadUrlKey))
						.withReleaseNotesUrl(properties.get(releaseNotesKey))
						.withNewLibsUrl(properties.get(newLibsKey)).withNewVersion(properties.get(currentVersionKey));
				LOG.info("New version available");
			} else {
				result.newVersionAvailable(false);
				LOG.info("No new version available");
			}
		} catch (Exception e) {
			LOG.warn("Error while checking for new version. ", e);
			result.withCheckForVersionException(e);
		}

		this.processResult(result);

		callback.processResult(result);

		return result;
	}

	private Map<String, String> loadPropertiesMap() throws IOException {
		URL u = new URL(updatrUrl);
		BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
		String inputLine;

		Map<String, String> properties = new HashMap<String, String>();

		String[] lineItems;
		while ((inputLine = in.readLine()) != null) {
			LOG.debug(inputLine);

			lineItems = inputLine.split("=");

			if (lineItems.length > 1) {
				properties.put(lineItems[0], inputLine.substring(inputLine.indexOf("=") + 1));
			}
		}

		in.close();

		return properties;
	}

	private void processResult(UpdatrResult upResult) {
		if (upResult.checkForVersionException() == null && upResult.newVersionAvailable()) {
			try {
				File projectJar = new File(projectName + "-" + upResult.newVersion() + ".jar");

				LOG.info("Downloading the new version...");

				this.loadUrlToFile(upResult.downloadUrl(), projectJar);
				this.downloadNewLibs(upResult);
				this.replaceNewVersionInScript(upResult);
				this.printReleaseNotes(upResult);

				upResult.autoUpdateSuccessful(true);
				LOG.info("!!! The new version will be picked up on the next run! !!!");

			} catch (Exception e) {
				LOG.warn("Exception occured when downloading the new version: " + e.getMessage());
				LOG.debug("Exception: ", e);
				upResult.withUpdateProcessException(e);
			}
		}
	}

	private void printReleaseNotes(UpdatrResult upResult) throws IOException {
		File tempReleaseNotes = File.createTempFile("release_notes", "tmp");
		this.loadUrlToFile(upResult.releaseNotesUrl(), tempReleaseNotes);

		BufferedReader reader = new BufferedReader(new FileReader(tempReleaseNotes));
		String line = null;
		LOG.info("============= Release notes ================");
		while ((line = reader.readLine()) != null) {
			LOG.info(line);
		}
		reader.close();
		LOG.info("============================================");
	}

	private void loadUrlToFile(String url, File outputFile) throws IOException {
		LOG.info("Downloading " + outputFile.getName() + " from url: [" + url + "]");

		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		connection.setReadTimeout(TIMEOUT);

		ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
		FileOutputStream fos = new FileOutputStream(outputFile);

		long expectedSize = connection.getContentLength();
		LOG.info("Expected size: " + expectedSize);
		long transferedSize = 0L;

		while (transferedSize < expectedSize) {
			transferedSize += fos.getChannel().transferFrom(rbc, transferedSize, 1 << 24);
			LOG.info(transferedSize + " bytes received");
		}
		fos.flush();
		fos.close();

		LOG.info("Download " + outputFile.getName() + " done!");
	}

	private void downloadNewLibs(UpdatrResult upResult) throws IOException {
		File tempZip = File.createTempFile("libs", "tmp");
		this.loadUrlToFile(upResult.newLibsUrl(), tempZip);

		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(tempZip), Charset.forName("UTF-8"));
		ZipEntry entry = zipIn.getNextEntry();
		LOG.info("Unziping...");
		while (entry != null) {
			LOG.info(entry.getName());
			String filePath = "lib" + File.separator + entry.getName();
			extractFile(zipIn, filePath);

			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		LOG.info("Unziping done!");
		zipIn.close();
	}

	private void replaceNewVersionInScript(UpdatrResult upResult) throws IOException {
		List<String> fileLines = new ArrayList<String>();

		LOG.info("Replacing the project running script.");
		BufferedReader reader = new BufferedReader(new FileReader(new File(scriptName)));
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.replaceAll(projectName + "-" + upResult.existingVersion(),
					projectName + "-" + upResult.newVersion());
			fileLines.add(line);
		}

		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(projectName)));
		for (String toWrite : fileLines) {
			writer.write(toWrite);
			writer.newLine();
		}

		writer.close();
		LOG.info("Script updated successfuly");
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

}
