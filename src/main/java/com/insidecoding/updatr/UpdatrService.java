package com.insidecoding.updatr;

import java.io.IOException;

import com.insidecoding.updatr.model.CheckForVersionResult;
import com.insidecoding.updatr.model.UpdatrSession;

/**
 * This service exposes 2 methods: one for checking if there is an update available and another used
 * to make the update itself.
 * 
 * @author ludovicianul
 * 
 */

public interface UpdatrService {

  /**
   * Checks for a new version on the URL specified as parameter. A {@code key=value} properties file
   * in the following format must exist at the specified URL. The following keys are expected:
   * {@code current_version}, {@code download_url}, {@code release_notes}, {@code new_libs}.
   * 
   * @param updatrUrl
   *          the URL where the properties file is located
   * @param existingVersion
   *          the existing version number in a X.X.X.X format
   * 
   * @return details on the new version
   * @throws IOException
   *           if the supplied URL is not acc1essible or there is an error during processing it
   * @throws InvalidUpdatrFormatException
   *           if the supplied URL is not a valid properties file with the expected keys
   */
  CheckForVersionResult checkForNewVersion(String updatrUrl, String existingVersion)
      throws IOException, InvalidUpdatrFormatException;

  /**
   * Performs the actual update. This will:
   * <ul>
   * <li>download the new app version</li>
   * <li>display the release notes for the new version if available</li>
   * <li>update the running script (if specified) with the new version</li>
   * </ul>
   * 
   * @param session
   *          updatr data including the ones returned by the checkForNeVersion method
   * @param callback
   *          the callback to be invoked if custom processing is required
   * @return true if the update was successful or false otherwise
   * @throws IOException
   *           if something goes wrong while downloading and saving the new version
   * @throws UpdatrProcessingException
   *           if something else goes wrong during the update process
   */
  boolean performUpdate(UpdatrSession session, UpdatrCallback callback) throws IOException,
      UpdatrProcessingException;
}
