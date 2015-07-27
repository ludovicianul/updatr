package com.insidecoding.updatr.model;

/**
 * Holds the information returned while checking if a new version is available.
 * 
 * @author ludovicianul
 *
 */
public final class CheckForVersionResult {
  private String availableVersion;
  private String downloadUrl;
  private String releaseNotesUrl;
  private String newLibsUrl;
  private boolean isNewVersion;

  public boolean isNewVersion() {
    return isNewVersion;
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

  public String getNewLibsUrl() {
    return newLibsUrl;
  }

  public static final class Builder {
    private String availableVersion;
    private String downloadUrl;
    private String releaseNotesUrl;
    private String newLibsUrl;
    private boolean isNewVersion;

    public Builder withAvailableVersion(String avVersion) {
      this.availableVersion = avVersion;
      return this;
    }

    public Builder withDownloadUrl(String downloadUrl) {
      this.downloadUrl = downloadUrl;
      return this;
    }

    public Builder withReleaseNotesUrl(String relNotesUrl) {
      this.releaseNotesUrl = relNotesUrl;
      return this;
    }

    public Builder withNewLibsUrl(String newLibs) {
      this.newLibsUrl = newLibs;
      return this;
    }

    public Builder withIsNewVersion(boolean is) {
      this.isNewVersion = is;
      return this;
    }

    public CheckForVersionResult build() {
      return new CheckForVersionResult(this);
    }
  }

  private CheckForVersionResult(Builder builder) {
    this.availableVersion = builder.availableVersion;
    this.downloadUrl = builder.downloadUrl;
    this.releaseNotesUrl = builder.releaseNotesUrl;
    this.newLibsUrl = builder.newLibsUrl;
    this.isNewVersion = builder.isNewVersion;
  }

}
