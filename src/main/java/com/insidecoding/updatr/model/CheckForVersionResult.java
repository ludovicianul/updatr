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
  private boolean isNewVersion;
  private String existingVersion;

  public String getExistingVersion() {
    return this.existingVersion;
  }

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

  public static final class Builder {
    private String availableVersion;
    private String downloadUrl;
    private String releaseNotesUrl;
    private boolean isNewVersion;
    private String existingVersion;

    public Builder(String existingVersion) {
      this.existingVersion = existingVersion;
    }

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
    this.isNewVersion = builder.isNewVersion;
    this.existingVersion = builder.existingVersion;
  }

}
