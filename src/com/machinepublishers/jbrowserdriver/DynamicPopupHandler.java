/* 
 * jBrowserDriver (TM)
 * Copyright (C) 2014-2015 Machine Publishers, LLC
 * ops@machinepublishers.com | screenslicer.com | machinepublishers.com
 * Cincinnati, Ohio, USA
 *
 * You can redistribute this program and/or modify it under the terms of the GNU Affero General Public
 * License version 3 as published by the Free Software Foundation.
 *
 * "ScreenSlicer", "jBrowserDriver", "Machine Publishers", and "automatic, zero-config web scraping"
 * are trademarks of Machine Publishers, LLC.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License version 3 for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License version 3 along with this
 * program. If not, see http://www.gnu.org/licenses/
 * 
 * For general details about how to investigate and report license violations, please see
 * https://www.gnu.org/licenses/gpl-violation.html and email the author, ops@machinepublishers.com
 */
package com.machinepublishers.jbrowserdriver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.util.Callback;

class DynamicPopupHandler implements Callback<PopupFeatures, WebEngine> {
  private final JBrowserDriver driver;
  private final Object browserContext;
  private static final Method spawn;
  private static final Field contextItemEngine;
  private static final Method unwrap;

  static {
    Method spawnTmp = null;
    Field contextItemEngineTmp = null;
    Method unwrapTmp = null;
    try {
      Class browserContext = DynamicPopupHandler.class.getClassLoader().loadClass(
          "com.machinepublishers.jbrowserdriver.BrowserContext");
      spawnTmp = browserContext.getDeclaredMethod("spawn", JBrowserDriver.class);
      spawnTmp.setAccessible(true);

      Class browserContextItemClass = DynamicPopupHandler.class.getClassLoader().loadClass(
          "com.machinepublishers.jbrowserdriver.BrowserContextItem");
      contextItemEngineTmp = browserContextItemClass.getDeclaredField("engine");
      contextItemEngineTmp.setAccessible(true);

      Class javaFxObjectClass = DynamicPopupHandler.class.getClassLoader().loadClass(
          "com.machinepublishers.jbrowserdriver.JavaFxObject");
      unwrapTmp = javaFxObjectClass.getDeclaredMethod("unwrap");
      unwrapTmp.setAccessible(true);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    spawn = spawnTmp;
    contextItemEngine = contextItemEngineTmp;
    unwrap = unwrapTmp;
  }

  DynamicPopupHandler(final JBrowserDriver driver, final Object browserContext) {
    this.driver = driver;
    this.browserContext = browserContext;
  }

  @Override
  public WebEngine call(PopupFeatures features) {
    try {
      return (WebEngine) unwrap.invoke(
          ((AtomicReference) contextItemEngine.get(spawn.invoke(browserContext, driver))).get());
    } catch (Throwable t) {
      t.printStackTrace();
      return null;
    }
  }
}