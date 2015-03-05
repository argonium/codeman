/**
 * @(#)IContentHandler.java
 *
 * Created on Oct 01, 2009
 *
 * Copyright 2009 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package com.nexagis.codeman.util;

/**
 * Interface for a class to handle a line read from a content file.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface IContentHandler
{
  /**
   * Process a line read from a content file.
   * 
   * @param line the line that was read
   * @return whether to continue reading the file
   */
  boolean processLine(final String line);
}
