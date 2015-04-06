package com.insidecoding.updatr;

/**
 * Result used by the updaTr Service. It holds all the relevant information needed by the Service in order to
 * perform the update.
 * 
 * @author ludovicianul
 *
 */
public final class UpdatrResult {

	private String existingVersion;
	private String newVersion;
	private boolean newVersionAvailable;
	private String downloadUrl;
	private String releaseNotesUrl;
	private String newLibsUrl;
	private Exception checkForVersionException;
	private Exception updateProcessException;
	private boolean autoUpdateSuccessful;

	private UpdatrResult() {

	}

	public static UpdatrResult create() {
		return new UpdatrResult();
	}

	public UpdatrResult withNewLibsUrl(final String nlu) {
		this.newLibsUrl = nlu;
		return this;
	}

	public String newLibsUrl() {
		return this.newLibsUrl;
	}

	public UpdatrResult autoUpdateSuccessful(boolean aus) {
		this.autoUpdateSuccessful = aus;
		return this;
	}

	public boolean autoUpdateSuccessful() {
		return this.autoUpdateSuccessful;
	}

	public UpdatrResult withUpdateProcessException(Exception ev) {
		this.updateProcessException = ev;
		return this;
	}

	public Exception updateProcessException() {
		return this.updateProcessException;
	}

	public UpdatrResult withCheckForVersionException(Exception ev) {
		this.checkForVersionException = ev;
		return this;
	}

	public Exception checkForVersionException() {
		return this.checkForVersionException;
	}

	public UpdatrResult withExistingVersion(String ev) {
		this.existingVersion = ev;
		return this;
	}

	public String existingVersion() {
		return this.existingVersion;
	}

	public UpdatrResult withNewVersion(String nv) {
		this.newVersion = nv;
		return this;
	}

	public String newVersion() {
		return this.newVersion;
	}

	public UpdatrResult newVersionAvailable(boolean nva) {
		this.newVersionAvailable = nva;
		return this;
	}

	public boolean newVersionAvailable() {
		return this.newVersionAvailable;
	}

	public UpdatrResult withDownloadUrl(String du) {
		this.downloadUrl = du;
		return this;
	}

	public String downloadUrl() {
		return this.downloadUrl;
	}

	public UpdatrResult withReleaseNotesUrl(String rnu) {
		this.releaseNotesUrl = rnu;
		return this;
	}

	public String releaseNotesUrl() {
		return this.releaseNotesUrl;
	}

	@Override
	public String toString() {
		return "UpdatrResult [existingVersion=" + existingVersion + ", newVersion=" + newVersion
				+ ", newVersionAvailable=" + newVersionAvailable + ", downloadUrl=" + downloadUrl
				+ ", releaseNotesUrl=" + releaseNotesUrl + ", newLibsUrl=" + newLibsUrl + ", checkForVersionException="
				+ checkForVersionException + ", updateProcessException=" + updateProcessException
				+ ", autoUpdateSuccessful=" + autoUpdateSuccessful + "]";
	}

}
