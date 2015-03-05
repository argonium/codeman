/**
 * @(#)ConfigParser.java
 * 
 * Created on Nov 10, 2009
 *
 * Copyright 2009 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package com.nexagis.codeman.interpret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parse the line from the commands file.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class ConfigParser
{
  /**
   * Default constructor.
   */
  public ConfigParser()
  {
    super();
  }
  
  
  /**
   * Return whether this line has a command.
   * 
   * @param inputLine the input string
   * @return whether the input string has a command
   */
  public boolean hasCommand(final String inputLine)
  {
    if ((inputLine == null) || (inputLine.length() < 1))
    {
      return false;
    }
    
    if (inputLine.charAt(0) == '#')
    {
      return false;
    }
    
    final int index = inputLine.indexOf(':');
    if (index <= 0)
    {
      return false;
    }
    
    return true;
  }
  
  
  /**
   * Return the name of the method to call.
   * 
   * @param inputLine the input string
   * @return the name of the method to call
   */
  public String getFunctor(final String inputLine)
  {
    final int index = inputLine.indexOf(':');
    return inputLine.substring(0, index).trim();
  }
  
  
  /**
   * Parse the command file line.
   * 
   * @param inputLine the line from the input file
   * @return an iterator of the arguments
   */
  public Iterator<ConfigArg> parse(final String inputLine)
  {
    // Check for a null input argument
    if (inputLine == null)
    {
      return null;
    }
    
    // Check for an empty input argument, or a comment
    final String line = inputLine.trim();
    if ((line.length() < 1) || (line.charAt(0) == '#'))
    {
      return null;
    }
    
    // Parse out the line
    final int index = inputLine.indexOf(':');
    Iterator<String> iter = new LineParser().parseIntoPhrases(
             line.substring(index + 1)).iterator();
    List<ConfigArg> list = new ArrayList<ConfigArg>(10);
    while (iter.hasNext())
    {
      String val = iter.next();
      int valLen = val.length();
      if (valLen == 0)
      {
        continue;
      }
      
      if (val.charAt(0) == '_')
      {
        if ((valLen == 1) || (valLen >= 3))
        {
          list.add(new ConfigArg(val));
        }
        else // if (valLen == 2)
        {
          final char t = val.charAt(1);
          if (t == 's')
          {
            list.add(new ConfigArg(ConfigArg.ConfigType.STRING));
          }
          else if (t == 'f')
          {
            list.add(new ConfigArg(ConfigArg.ConfigType.FLOAT));
          }
          else if (t == 'd')
          {
            list.add(new ConfigArg(ConfigArg.ConfigType.DOUBLE));
          }
          else if (t == 'n')
          {
            list.add(new ConfigArg(ConfigArg.ConfigType.INTEGER));
          }
          else if (t == 'l')
          {
            list.add(new ConfigArg(ConfigArg.ConfigType.LONG));
          }
          else
          {
            list.add(new ConfigArg(val));
          }
        }
      }
      else
      {
        list.add(new ConfigArg(val));
      }
    }
    
    // Return the iterator over the list
    return list.iterator();
  }
}
