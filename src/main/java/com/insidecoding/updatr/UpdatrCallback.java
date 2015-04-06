package com.insidecoding.updatr;

/**
 * Implement this in order to provide implementation on what you want to do with the information returned by
 * the updaTr. You may for example:
 * <ul>
 * <li>update the running script</li>
 * <li>do some custom logic with the downloaded file</li>
 * </ul>
 * 
 * 
 * 
 * @author ludovicianul
 * 
 */
public interface UpdatrCallback {

	/**
	 * Specific details on how to process the results.
	 * 
	 * @param upResult
	 *            the UpdatrResult returned by the updaTr service.
	 */
	void processResult(UpdatrResult upResult);
}
