/**
 * The handler for commands entered in the console by the user.
 */

package io.miti.codeman.console;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import io.miti.codeman.domain.Endpoint;
import io.miti.codeman.domain.SearchResult;
import io.miti.codeman.gui.CodeMan;
import io.miti.codeman.gui.SearchDlg;
import io.miti.codeman.interpret.ConfigArg;
import io.miti.codeman.interpret.ConfigParser;
import io.miti.codeman.interpret.LineParser;
import io.miti.codeman.managers.EndpointsManager;
import io.miti.codeman.managers.TabViewManager;
import io.miti.codeman.managers.ZipManager;
import io.miti.codeman.util.Content;
import io.miti.codeman.util.IContentHandler;
import io.miti.codeman.util.ListFormatter;
import io.miti.codeman.util.Logger;

/**
 * Processes commands entered by the user.
 */
public final class CmdLineInterpreter implements Interpreter, IContentHandler
{
  /** An iterator over the strings in the user's command. */
  private List<String> cmds = null;
  
  /** Whether the status bar was updated after command execution.  */
  private boolean barUpdated = false;
  
  /** Declare the date formatter used to format the time for the prompt. */
  private static final SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss] > ");
  
  /** The results of the last search, list endpoints or list files. */
  private List<String> resultFilenames = new ArrayList<String>(10);
  
  /**
   * Default constructor.
   */
  public CmdLineInterpreter()
  {
    super();
  }
  
  
  /**
   * Write the banner to the text area.
   * 
   * @param text the text area control
   */
  @Override
  public void writeBanner(final JTextArea text)
  {
    JConsole.addText("Enter 'help' to see available commands.\n");
  }
  
  
  /**
   * Write the prompt to the text area.
   * 
   * @param text the text area control
   */
  @Override
  public void writePrompt(final JTextArea text)
  {
    String prompt = sdf.format(new Date());
    JConsole.addText(prompt);
  }
  
  
  /**
   * Process a command entered by the user.
   * 
   * @param text the text area control
   * @param cmd the text command
   */
  @Override
  public void processCommand(final JTextArea text, final String cmd)
  {
    // Parse the input command
    cmds = new LineParser().parseIntoPhrases(cmd);
    
    // If no command, return
    if (cmds.size() < 1)
    {
      CodeMan.getApp().setStatusBarText(null);
      JConsole.addText("\n");
      return;
    }
    
    // Search cmds.txt for a match
    final boolean result = Content.processFileLines(this,
                             Content.COMMANDS_DATA);
    if (!result)
    {
      JConsole.addText("\nUnknown command\n");
    }
    else
    {
      CodeMan.getApp().setStatusBarText(null);
      
      // This should only happen if a command didn't set some text
      if (!barUpdated)
      {
        writeSeparateResults(null, "OK");
        JConsole.addText("\n");
      }
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
  
  
  /**
   * Show the version number.
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean showVersion(final Object[] args)
  {
    JConsole.addText("\nCode Manager, version 1.5\n");
    return true;
  }
  
  
  /**
   * Show the help.
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean help(final Object[] args)
  {
    // Get the list of command help
    List<String> cmds = Content.getContent(Content.COMMANDS_HELP);
    
    // Sort the list and print the contents
    Collections.sort(cmds);
    final int size = cmds.size();
    StringBuilder sb = new StringBuilder(300);
    sb.append("\nThe following commands are supported:");
    for (int i = 0; i < size; ++i)
    {
      sb.append("\n" + cmds.get(i));
    }
    sb.append("\n");
    
    // Show the text
    JConsole.addText(sb.toString());
    
    return true;
  }
  
  
  /**
   * Clear the screen.
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean clear(final Object[] args)
  {
    JConsole.setText("");
    return true;
  }
  
  
  /**
   * List the commands starting with some prefix.
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean partialHelp(final Object[] args)
  {
    // TODO Support multiple terms to search by
    
    // Get the list of all commands from the cmds.txt file
    List<String> cmds = Content.getContent(Content.COMMANDS_HELP);
    
    // Save the current size
    int size = cmds.size();
    
    // Iterate over the list (in reverse) and remove any commands that
    // start with any string other than the search criteria
    final String criteria = ((String) args[1]).toUpperCase();
    for (int i = (size - 1); i >= 0; --i)
    {
      final String temp = cmds.get(i).toUpperCase();
      if (!temp.startsWith(criteria))
      {
        cmds.remove(i);
      }
    }
    
    // Update the list size
    size = cmds.size();
    
    if (size == 0)
    {
      JConsole.addText("\nNo commands match the search criteria\n");
      return true;
    }
    
    // Sort the list and build into a list
    Collections.sort(cmds);
    StringBuilder sb = new StringBuilder(100);
    for (int i = 0; i < size; ++i)
    {
      sb.append("\n" + cmds.get(i));
    }
    sb.append("\n");
    
    // Show the text
    JConsole.addText(sb.toString());
    
    return true;
  }
  
  
  /**
   * Set the debug mode for the application (for logging SQL queries).
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean setDebug(final Object[] args)
  {
    // Get the new debug status (on or off)
    final String mode = (String) args[1];
    
    String result = null;
    if (mode.equals("on"))
    {
      Logger.initialize(2, "stdout", true);
      result = "\nDebug mode enabled\n";
    }
    else if (mode.equals("off"))
    {
      Logger.updateLogLevel(0);
      result = "\nDebug mode disabled\n";
    }
    else
    {
      result = "\nUnknown debug mode.  Please use 'on' or 'off'.\n";
    }
    JConsole.addText(result);
    
    return true;
  }
  
  
  public boolean listEndpoints(final Object[] args)
  {
    return queryEndpoints(null);
  }
  
  
  public boolean searchEndpoints(final Object[] args)
  {
    return queryEndpoints(((String) args[2]));
  }
  
  
  public boolean queryEndpoints(final String query)
  {
    // Delete any entries in the saved result list
    resultFilenames.clear();
    
    // Check the directory
    File dir = ZipManager.getInstance().getOutputDirectory();
    if (dir == null)
    {
      JConsole.addText("\nThe output directory is not set. Is a file open?\n");
      return true;
    }
    final File file = new File(dir, "endpoints.txt");
    if (!file.exists())
    {
      JConsole.addText("\nThe endpoints.txt file was not found. Please update the indexes.\n");
      return true;
    }
    
    // Get the list of endpoints
    List<Endpoint> list = EndpointsManager.getInstance().getList(file, query);
    if ((list == null) || (list.size() == 0))
    {
      JConsole.addText("\nNo endpoints were found.\n");
      return true;
    }
    
    // Sort the list
    Collections.sort(list);
    
    // Copy the filenames
    for (Endpoint pt : list)
    {
      // Save to the list of filenames
      resultFilenames.add(pt.getFilename());
    }
    
    // Build the table
    String table = new ListFormatter().getTable(list,
        new String[]{"#", "displayName", "operation", "endpoint"},
        new String[]{"#", "File", "Operation", "Endpoint"});
    JConsole.addText("\n" + table);
    
    return true;
  }
  
  
  public boolean listFiles(final Object[] args)
  {
    return queryFiles(null);
  }
  
  
  public boolean searchFiles(final Object[] args)
  {
    return queryFiles(((String) args[2]));
  }
  
  
  public boolean queryFiles(final String query)
  {
    // Delete any entries in the saved result list
    resultFilenames.clear();
    
    // Check the directory
    File dir = ZipManager.getInstance().getOutputDirectory();
    if (dir == null)
    {
      JConsole.addText("\nThe output directory is not set. Is a file open?\n");
      return true;
    }
    
    // Get the list of files
    // List<Endpoint> list = EndpointsManager.getInstance().getList(file, query);
    List<String> list = ZipManager.getInstance().getFileList(query);
    if ((list == null) || (list.size() == 0))
    {
      JConsole.addText("\nNo files were found.\n");
      return true;
    }
    
    // Copy the filenames
    for (String str : list)
    {
      resultFilenames.add(str);
    }
    
    // Build the table
    String table = new ListFormatter().getTable(list,
        new String[]{"#", null},
        new String[]{"#", "Filename"});
    JConsole.addText("\n" + table);
    
    return true;
  }
  
  
  /**
   * Convert a date from a number into a formatted date.
   * 
   * @param args the array of arguments
   * @return whether the status bar text was modified
   */
  public boolean showDate(final Object[] args)
  {
    // Get the number passed to the method
    final Long date = (Long) args[1];
    
    // Format the date
    final SimpleDateFormat sdf =
                new SimpleDateFormat("hh:mm:ss aa 'on' MMMM dd, yyyy");
    String dateStr = sdf.format(new Date(date.longValue()));
    JConsole.addText("\nThe date is " + dateStr + "\n");
    return true;
  }
  
  
  public boolean search(final Object[] args)
  {
    // Delete any entries in the saved result list
    resultFilenames.clear();
    
    // Search the Lucene index
    final String query = (String) args[1];
    List<SearchResult> results = ZipManager.getInstance().search(query);
    if ((results == null) || (results.size() < 1))
    {
      JConsole.addText("\nNo search results were found\n");
    }
    else
    {
      printSearchResults(results);
    }
    
    return true;
  }
  
  
  private void printSearchResults(List<SearchResult> results)
  {
    // Copy the filenames
    for (SearchResult sr : results)
    {
      resultFilenames.add(sr.getDoc());
    }
    
    String table = new ListFormatter().getTable(results,
        new String[]{"#", "formattedScore", "doc"},
        new String[]{"#", "Score", "File"});
    JConsole.addText("\n" + table);
  }
  
  
  public boolean openFile(final Object[] args)
  {
    // Save the file name to open
    final String name = (String) args[2];
    openFileByName(name);
    
    return true;
  }
  
  
  public boolean catFile(final Object[] args)
  {
    // Save the file name to open
    final String name = (String) args[2];
    catFileByName(name);
    
    return true;
  }
  
  
  private void catFileByName(final String name)
  {
    // If the file exists, list its contents
    String text = ZipManager.getInstance().getFileText(name);
    if (text == null)
    {
      JConsole.addText("\nThe file was not found\n");
    }
    else
    {
      JConsole.addText("\n");
      JConsole.addText(text);
      JConsole.addText("\n");
    }
  }
  
  
  private void openFileByName(final String name)
  {
    // If the file exists, list its contents
    String text = ZipManager.getInstance().getFileText(name);
    if (text == null)
    {
      JConsole.addText("\nThe file was not found\n");
    }
    else
    {
      JConsole.addText("\nOpening file " + name + "\n");
      TabViewManager.getInstance().addFileFromZip(name, text);
    }
  }
  
  
  public boolean catResultNum(final Object[] args)
  {
    int resNum = ((Integer) args[2]).intValue();
    openResultByNumber(resNum, false);
    return true;
  }
  
  
  public boolean openResultNum(final Object[] args)
  {
    int resNum = ((Integer) args[2]).intValue();
    openResultByNumber(resNum, true);
    return true;
  }
  
  
  private void openResultByNumber(final int resNum, final boolean openTab)
  {
    // Check the boundary
    final int numFiles = resultFilenames.size();
    if (numFiles < 1)
    {
      JConsole.addText("\nError: There are no files in the result set to open\n");
    }
    else if (resNum < 1)
    {
      JConsole.addText("\nError: The file number must be at least 1\n");
    }
    else if (resNum > numFiles)
    {
      JConsole.addText("\nError: The file number must be no larger than " + numFiles + "\n");
    }
    else
    {
      final String name = resultFilenames.get(resNum - 1);
      
      if (openTab)
      {
        openFileByName(name);
      }
      else
      {
        catFileByName(name);
      }
    }
  }
  
  
  public boolean queryResultNum(final Object[] args)
  {
    // Print info on the specified result number
    int resNum = ((Integer) args[2]).intValue();
    
    // Check the boundary
    final int numFiles = resultFilenames.size();
    if (numFiles < 1)
    {
      JConsole.addText("\nError: There are no files in the result set to open\n");
    }
    else if (resNum < 1)
    {
      JConsole.addText("\nError: The file number must be at least 1\n");
    }
    else if (resNum > numFiles)
    {
      JConsole.addText("\nError: The file number must be no larger than " + numFiles + "\n");
    }
    else
    {
      final String name = resultFilenames.get(resNum - 1);
      String msg = String.format("\nResult #%d: %s\n", resNum, name);
      JConsole.addText(msg);
    }
    
    return true;
  }
  
  
  public boolean zipInfo(final Object[] args)
  {
    // Check the directory
    File dir = ZipManager.getInstance().getOutputDirectory();
    if (dir == null)
    {
      JConsole.addText("\nThe output directory is not set. Is a file open?\n");
      return true;
    }
    
    final String msg = ZipManager.getInstance().toString();
    JConsole.addText(msg);
    
    return true;
  }
  
  
  public boolean searchex(final Object[] args)
  {
    // Show the Search dialog
    List<SearchResult> results = SearchDlg.getInstance(CodeMan.getApp().frame).getSearches();
    if (results == null)
    {
      JConsole.addText("\nSearch canceled\n");
      return true;
    }
    
    // Delete any entries in the saved result list
    resultFilenames.clear();
    
    if (results.size() == 0)
    {
      JConsole.addText("\nNo results found\n");
      return true;
    }
    
    // Print the results
    printSearchResults(results);
    
    return true;
  }
}
