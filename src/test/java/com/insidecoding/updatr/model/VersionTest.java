package com.insidecoding.updatr.model;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

  @Test(expected = IllegalArgumentException.class)
  public void nullVersion() throws Exception {
    Version.fromString(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalVersion() throws Exception {
    Version.fromString("version");
  }

  @Test
  public void brokenVersion() throws Exception {
    Version ver = Version.fromString("1.1.1.");

    Assert.assertTrue(ver.toString().equals("1.1.1"));
  }

  @Test
  public void stringsInVersion() throws Exception {
    Version ver = Version.fromString("1.1.1-SNAPSHOT");

    Assert.assertTrue(ver.toString().equals("1.1.1"));

  }

  @Test
  public void sameVersion() throws Exception {
    Version version1 = Version.fromString("1.1");
    Version version2 = Version.fromString("1.1");

    Assert.assertTrue(version1.compareTo(version2) == 0);
  }

  @Test
  public void minVersionDiff() throws Exception {
    Version version1 = Version.fromString("1.1");
    Version version2 = Version.fromString("1.1.1");

    Assert.assertTrue(version1.compareTo(version2) < 0);
  }

  @Test
  public void sameVersionObject() throws Exception {
    Version version1 = Version.fromString("1.1");

    Assert.assertTrue(version1.compareTo(version1) == 0);
  }

  @Test
  public void newVersionAvailable() throws Exception {
    Version existing = Version.fromString("1.1");
    Version newV = Version.fromString("1.2");

    Assert.assertTrue(existing.compareTo(newV) < 0);
  }

  @Test
  public void toStringz() {
    Version version = Version.fromString("1.1.1");

    Assert.assertTrue(version.toString().equals("1.1.1"));
  }

  @Test
  public void minorVersionWithZero() throws Exception {
    Version version1 = Version.fromString("2.11");
    Version version2 = Version.fromString("2.1");

    Assert.assertTrue(version1.compareTo(version2) > 0);
  }

}
