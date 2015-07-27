package com.insidecoding.updatr;

/**
 * Exception thrown if the supplied updatr URL is not a valid properties file.
 * 
 * @author ludovicianul
 *
 */
public class InvalidUpdatrFormatException extends Exception {

  private static final long serialVersionUID = 424311890446777276L;

  public InvalidUpdatrFormatException() {
    super(
        "The supplied URL does not contain a valid properties file with the following keys: current_version, "
            + "download_url, release_notes, new_libs");
  }
}
