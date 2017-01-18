package com.insidecoding.updatr.model;

/**
 * Holds the data used by the UpdatrService when performing the app update.
 * 
 * @author ludovicianul
 *
 */
public final class UpdatrSession {

  private String existingVersion;
  private String availableVersion;
  private String downloadUrl;
  private String releaseNotesUrl;
  private String appName;
  private String scriptName;
  private String[] scriptCommand;

  public String[] getScriptCommand() {
    return this.scriptCommand;
  }

  public String getExistingVersion() {
    return existingVersion;
  }

  public String getAvailableVersion() {
    return availableVersion;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public String getReleaseNotesUrl() {
    return releaseNotesUrl;
  }

  public String getAppName() {
    return appName;
  }

  public String getScriptName() {
    return scriptName;
  }

  public static final class Builder {
    private String existingVersion;
    private String availableVersion;
    private String downloadUrl;
    private String releaseNotesUrl;
    private String appName;
    private String scriptName;
    private String[] scriptCommand;

    public Builder(CheckForVersionResult result) {
      this.availableVersion = result.getAvailableVersion();
      this.downloadUrl = result.getDownloadUrl();
      this.releaseNotesUrl = result.getReleaseNotesUrl();
    }

    public UpdatrSession build() {
      return new UpdatrSession(this);
    }

    public Builder withExistingVersion(String exVersion) {
      this.existingVersion = exVersion;
      return this;
    }

    public Builder withAppName(String name) {
      this.appName = name;
      return this;
    }

    public Builder withScriptName(String name) {
      this.scriptName = name;
      return this;
    }

    public Builder withScriptParams(String[] command) {
      this.scriptCommand = command;
      return this;
    }
  }

  private UpdatrSession(Builder builder) {
    this.existingVersion = builder.existingVersion;
    this.availableVersion = builder.availableVersion;
    this.downloadUrl = builder.downloadUrl;
    this.releaseNotesUrl = builder.releaseNotesUrl;
    this.appName = builder.appName;
    this.scriptName = builder.scriptName;
    this.scriptCommand = builder.scriptCommand;
  }

}
