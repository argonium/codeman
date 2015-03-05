package io.miti.codeman.interpret;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import io.miti.codeman.gui.CodeMan;
import io.miti.codeman.util.Content;
import io.miti.codeman.util.IContentHandler;

/**
 * The command interpreter.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Interpreter implements IContentHandler
{
  /**
   * An iterator over the strings in the user's command.
   */
  private List<String> cmds = null;
  
  /**
   * Whether the status bar was updated after command execution.
   */
  private boolean barUpdated = false;
  
  
  /**
   * Default constructor.
   */
  public Interpreter()
  {
    super();
  }
  
  
  /**
   * Constructor taking the user's command.
   * 
   * @param cmd the input command
   */
  public Interpreter(final String cmd)
  {
    super();
    process(cmd);
  }
  
  
  /**
   * Interpret the command.
   * 
   * @param cmd the user's command
   */
  public void process(final String cmd)
  {
    // Parse the input command
    cmds = new LineParser().parseIntoPhrases(cmd);
    
    // If no command, return
    if (cmds.size() < 1)
    {
      CodeMan.getApp().setStatusBarText(null);
      return;
    }
    
    // Search cmds.txt for a match
    boolean result = Content.processFileLines(this, Content.COMMANDS_DATA);
    if (!result)
    {
      writeResults("Error in command");
    }
    else
    {
      CodeMan.getApp().setStatusBarText(null);
      
      // This should only happen if a command didn't set some text
      if (!barUpdated)
      {
        writeSeparateResults(null, "OK");
      }
    }
  }
  
  
  /**
   * Parse the line from the input file.
   * 
   * @param line the line from the file
   * @return whether to continue parsing the file
   */
  @Override
  public boolean processLine(final String line)
  {
    // Parse the line from the command file
    ConfigParser parser = new ConfigParser();
    if (!parser.hasCommand(line))
    {
      return true;
    }
    
    // Get the functor
    String functor = parser.getFunctor(line);
    if ((functor == null) || (functor.length() < 0))
    {
      return true;
    }
    
    // Get the iterator over the config arguments
    Iterator<ConfigArg> args = parser.parse(line);
    
    // See if this command matches the line from the input file
    boolean keepReading = true;
    Object[] functorData = matchData(args);
    if (functorData.length > 0)
    {
      keepReading = false;
      
      // Call the method via reflection
      dispatch(functor, functorData);
    }
    
    return keepReading;
  }
  
  
  /**
   * Check for a match between the command and the Config data.
   * 
   * @param args the list of config parameters from the file
   * @return the parsed data, if a match
   */
  private Object[] matchData(final Iterator<ConfigArg> args)
  {
    Iterator<String> cmdsIter = cmds.iterator();
    
    // If they match, return an array of the parameters in args
    List<Object> data = new ArrayList<Object>(20);
    boolean ok = true;
    while (ok && args.hasNext())
    {
      final ConfigArg config = args.next();
      
      // Check if more commands are available
      if (!cmdsIter.hasNext())
      {
        // We ran out of commands, so this doesn't match
        ok = false;
        break;
      }
      final String cmd = cmdsIter.next();
      
      // Check if the data matches up
      if (config.isToken())
      {
        ok = (config.getValue().equalsIgnoreCase(cmd));
        if (ok)
        {
          data.add(cmd);
        }
      }
      else if (config.isLong())
      {
        long value = getLong(cmd);
        ok = (value != Long.MIN_VALUE);
        if (ok)
        {
          data.add(Long.valueOf(value));
        }
      }
      else if (config.isInteger())
      {
        int value = getInteger(cmd);
        ok = (value != Integer.MIN_VALUE);
        if (ok)
        {
          data.add(Integer.valueOf(value));
        }
      }
      else if (config.isString())
      {
        ok = true;
        data.add(cmd);
      }
      else if (config.isFloat())
      {
        float value = getFloat(cmd);
        ok = (value != Float.NaN);
        if (ok)
        {
          data.add(Float.valueOf(value));
        }
      }
      else if (config.isDouble())
      {
        double value = getDouble(cmd);
        ok = (value != Double.NaN);
        if (ok)
        {
          data.add(Double.valueOf(value));
        }
      }
    }
    
    // See if there are more cmds
    if (cmdsIter.hasNext())
    {
      // There are, so the two lists don't match up
      ok = false;
    }
    
    // Check for an error
    if (!ok)
    {
      return (new Object[0]);
    }
    
    return data.toArray();
  }
  
  
  /**
   * Dispatch to the target method.
   * 
   * @param functor the method to call
   * @param functorData the data for the method
   */
  public void dispatch(final String functor,
                        final Object[] functorData)
  {
    try
    {
      // Call the method through reflection
      Class<? extends Interpreter> cls = this.getClass();
      Method method = cls.getMethod(functor, new Class[] {Object[].class});
      Object result = method.invoke(this, new Object[] {functorData});
      barUpdated = (Boolean) result;
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
    catch (NoSuchMethodException e)
    {
      e.printStackTrace();
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
  }
  
  
  /**
   * Write out different map and log window messages.
   * 
   * @param msgMap the message for the map's status window
   * @param msgLog the msg for the log window
   */
  public static void writeSeparateResults(final String msgMap,
                                            final String msgLog)
  {
    showResults(msgMap, msgLog);
  }
  
  
  /**
   * Write map and log window messages.
   * 
   * @param msgCommon the msg for both windows
   */
  public static void writeResults(final String msgCommon)
  {
    showResults(msgCommon, msgCommon);
  }
  
  
  /**
   * Write the result to the status bar and log window.
   * 
   * @param msgMap the string for the map window (status bar)
   * @param msgLog the string for the log window
   */
  private static synchronized void showResults(final String msgMap,
                                                   final String msgLog)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        CodeMan.getApp().setStatusBarText(msgMap);
      }
    });
  }
  
  
  /**
   * Return the Float in the String.
   * 
   * @param obj the string to parse
   * @return the float value, else Float.NaN
   */
  private float getFloat(final String obj)
  {
    float value = Float.NaN;
    try
    {
      value = Float.parseFloat(obj);
    }
    catch (NumberFormatException nfe)
    {
      value = Float.NaN;
    }
    
    return value;
  }
  
  
  /**
   * Return the Double in the String.
   * 
   * @param obj the string to parse
   * @return the double value, else Double.NaN
   */
  private double getDouble(final String obj)
  {
    double value = Double.NaN;
    try
    {
      value = Double.parseDouble(obj);
    }
    catch (NumberFormatException nfe)
    {
      value = Double.NaN;
    }
    
    return value;
  }
  
  
  /**
   * Return the Integer in the String.
   * 
   * @param obj the string to parse
   * @return the integer value, else Integer.MIN_VALUE
   */
  private int getInteger(final String obj)
  {
    int value = Integer.MIN_VALUE;
    try
    {
      value = Integer.parseInt(obj);
    }
    catch (NumberFormatException nfe)
    {
      value = Integer.MIN_VALUE;
    }
    
    return value;
  }
  
  
  /**
   * Return the Long in the String.
   * 
   * @param obj the string to parse
   * @return the long value, else Long.MIN_VALUE
   */
  private long getLong(final String obj)
  {
    long value = Long.MIN_VALUE;
    try
    {
      value = Long.parseLong(obj);
    }
    catch (NumberFormatException nfe)
    {
      value = Long.MIN_VALUE;
    }
    
    return value;
  }
  
  
  /**
   * Quit the app.
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean quit(final Object[] args)
  {
    CodeMan.getApp().exitApp();
    return false;
  }
}
