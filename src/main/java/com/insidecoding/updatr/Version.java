package com.insidecoding.updatr;

/**
 * Abstracts the representation of a (software) version in format X.X.X.X...
 * 
 * @author ludovicianul
 * 
 */
public final class Version implements Comparable<Version> {

	private Integer[] items;

	private Version(String[] itms) {
		if (itms == null) {
			throw new IllegalArgumentException("must provide non-null items array");
		}

		if (itms.length == 0) {
			throw new IllegalArgumentException("At least one component required");
		}

		this.items = new Integer[itms.length];

		for (int i = 0; i < itms.length; i++) {
			this.items[i] = Integer.valueOf(itms[i]);
			if (this.items[i] < 0) {
				throw new IllegalArgumentException("must only use positive numbers; offending value " + itms[i]);
			}
		}

	}

	/**
	 * It creates a version object from a given String representation.
	 * 
	 * @param versionSpec
	 *            version in format X.X.X.
	 * @return a Version representation of the given String.
	 */
	public static Version fromString(String versionSpec) {

		return new Version(parse(versionSpec));
	}

	@Override
	public int compareTo(Version o) {

		if (this == o) {
			return 0;
		}
		int result = 0;
		int length = Math.min(items.length, o.items.length);
		int idx = 0;

		while (result == 0 && idx < length) {
			int current = items[idx].compareTo(o.items[idx]);

			if (current != 0) {
				result = current;
			}

			idx++;
		}

		if (result == 0) {
			if (idx < items.length) {
				result = items[idx];
			} else if (idx < o.items.length) {
				result = -o.items[idx];
			}
		}

		return result;
	}

	private static String[] parse(String versionStr) {
		if (versionStr == null) {
			return null;
		}

		final String[] items = versionStr.replaceAll("[^\\d.]", "").split("\\.");

		for (int i = 0; i < items.length; i++) {
			items[i] = items[i].trim();
		}

		return items;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(items[0].toString());

		if (items.length >= 1) {
			for (int i = 1; i < items.length; i++) {
				b.append('.').append(items[i]);
			}
		}
		return b.toString();
	}

}
