/**
 * @(#)ConfigArg.java
 * 
 * Created on Nov 10, 2009
 *
 * Copyright 2009 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package com.nexagis.codeman.interpret;

/**
 * A class describing each element of a config argument.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class ConfigArg
{
  /**
   * The type of Config argument.
   */
  public enum ConfigType
  {
    /** A predefined string. */
    TOKEN("Token"),
    
    /** An integer. */
    INTEGER("Integer"),
    
    /** A long. */
    LONG("Long"),
    
    /** A float. */
    FLOAT("Float"),
    
    /** A double. */
    DOUBLE("Double"),
    
    /** A String. */
    STRING("String");
    
    /**
     * The description string.
     */
    private String desc;
    
    /**
     * Constructor.
     * 
     * @param sDesc the description string
     */
    private ConfigType(final String sDesc)
    {
      desc = sDesc;
    }
    
    
    /**
     * Return the description of this instance.
     * 
     * @return the description of this instance
     */
    @Override
    public String toString()
    {
      return desc;
    }
  };
  
  
  /**
   * The argument value.
   */
  private String value = null;
  
  /**
   * The type of the argument.
   */
  private ConfigType type = ConfigType.TOKEN;
  
  /**
   * The allowed length of the field.  Only used for integers.
   */
  private int len = 0;
  
  /**
   * Default constructor.
   */
  public ConfigArg()
  {
    super();
  }
  
  
  /**
   * Constructor.
   * 
   * @param sValue the argument value
   */
  public ConfigArg(final String sValue)
  {
    value = sValue;
  }
  
  
  /**
   * Constructor.
   * 
   * @param pType the type of argument
   */
  public ConfigArg(final ConfigType pType)
  {
    type = pType;
  }
  
  
  /**
   * Constructor.
   * 
   * @param pType the type of argument
   * @param nLen the argument length
   */
  public ConfigArg(final ConfigType pType,
                    final int nLen)
  {
    type = pType;
    len = nLen;
  }
  
  
  /**
   * Return the value.
   * 
   * @return the value
   */
  public String getValue()
  {
    return value;
  }
  
  
  /**
   * Return the field type.
   * 
   * @return the type
   */
  public ConfigType getType()
  {
    return type;
  }
  
  
  /**
   * Return the length.
   * 
   * @return the length
   */
  public int getLength()
  {
    return len;
  }
  
  
  /**
   * Return whether the value is a token.
   * 
   * @return whether the value is a token
   */
  public boolean isToken()
  {
    return (type == ConfigType.TOKEN);
  }
  
  
  /**
   * Return whether the value is an integer.
   * 
   * @return whether the value is an integer
   */
  public boolean isInteger()
  {
    return (type == ConfigType.INTEGER);
  }
  
  
  /**
   * Return whether the value is a long.
   * 
   * @return whether the value is a long
   */
  public boolean isLong()
  {
    return (type == ConfigType.LONG);
  }
  
  
  /**
   * Return whether the value is a float.
   * 
   * @return whether the value is a float
   */
  public boolean isFloat()
  {
    return (type == ConfigType.FLOAT);
  }
  
  
  /**
   * Return whether the value is a double.
   * 
   * @return whether the value is a double
   */
  public boolean isDouble()
  {
    return (type == ConfigType.DOUBLE);
  }
  
  
  /**
   * Return whether the value is a string.
   * 
   * @return whether the value is a string
   */
  public boolean isString()
  {
    return (type == ConfigType.STRING);
  }
  
  
  /**
   * Return the description of this instance.
   * 
   * @return the description of this instance
   */
  @Override
  public String toString()
  {
    // Build the output string and return it
    StringBuilder sb = new StringBuilder(100);
    sb.append(type.toString()).append(", ")
      .append(Integer.toString(len)).append(": ")
      .append(value);
    
    return sb.toString();
  }
}
