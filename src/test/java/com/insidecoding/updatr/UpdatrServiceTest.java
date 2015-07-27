package com.insidecoding.updatr;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.insidecoding.updatr.model.CheckForVersionResult;

public class UpdatrServiceTest {
  private UpdatrService updatr;

  @Before
  public void setUp() {
    updatr = new UpdatrServiceImpl();
  }

  @Test
  public void newVersionAvailable() throws Exception {
    URL url = this.getClass().getResource("/updatr_is_new.txt");

    CheckForVersionResult result = updatr.checkForNewVersion(url.toString(), "2.01");

    Assert.assertTrue(result.isNewVersion());
  }

  @Test(expected = InvalidUpdatrFormatException.class)
  public void emptyFile() throws Exception {
    URL url = this.getClass().getResource("/updatr_empty.txt");

    updatr.checkForNewVersion(url.toString(), "2.01");
  }
}
