package org.woped.core.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.woped.core.WoPeDConstants;
import org.woped.core.utilities.ILogger;
import org.woped.core.utilities.LoggerManager;

public class ModelElementFactoryTest {

  @Test
  public void createArcModel_validId_createsInstance() throws Exception {

    ArcModel arc1 = ModelElementFactory.createArcModel("arc1", null, null);
    assertNotNull(arc1);
  }

  @Test
  public void createArcModel_noId_returnsNull() {

    ArcModel arc = ModelElementFactory.createArcModel(null, null, null);
    assertNull(arc);
  }

  @Test
  public void createArcModel_noId_logsError() {

    ILogger logger = mock(ILogger.class);
    LoggerManager.register(logger, WoPeDConstants.CORE_LOGGER);

    ModelElementFactory.createArcModel(null, null, null);

    verify(logger).error("ID must be set");
  }

  @Before
  public void setup() {
    LoggerManager.resetForTesting();
  }

  @After
  public void tearDown() {
    LoggerManager.resetForTesting();
  }
}
