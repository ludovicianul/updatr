package com.insidecoding.updatr;

/**
 * This class assumes that the following Strings are binded by your IoC container:
 * <ul>
 * <li>@Named("updatrUrl")</li>
 * <li>@Named("existingVersion")</li>
 * <li>@Named("currentVersionKey")</li>
 * <li>@Named("downloadUrlKey")</li>
 * <li>@Named("releaseNotesKey")</li>
 * <li>@Named("newLibsKey")</li>
 * <li>@Named("projectName")</li>
 * <li>@Named("scriptName")</li>
 * </ul>
 * 
 * @author ludovicianul
 * 
 */

public interface UpdatrService {

	/**
	 * Checks for a new version based on the above mentioned injected parameters.
	 * 
	 * @param callback
	 *            the callback called after checking for the new version.
	 * @return new version checking details.
	 */
	UpdatrResult checkForNewVersion(UpdatrCallback callback);
}
