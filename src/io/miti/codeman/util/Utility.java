package io.miti.codeman.util;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Utility methods.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Utility
{
  /**
   * Whether to read input files as a stream.
   */
  private static boolean readAsStream = false;
  
  /**
   * The line separator for this OS.
   */
  private static String lineSep = null;
  
  /**
   * Default constructor.
   */
  private Utility()
  {
    super();
  }
  
  
  /**
   * Return the application name.
   * 
   * @return the application name
   */
  public static String getAppName()
  {
    return "Code Manager";
  }
  
  
  /**
   * Return the line separator for this OS.
   * 
   * @return the line separator for this OS
   */
  public static String getLineSeparator()
  {
    // See if it's been initialized
    if (lineSep == null)
    {
      lineSep = System.getProperty("line.separator");
    }
    
    return lineSep;
  }
  
  
  /**
   * Whether to read content files as a stream.  This
   * is used when running the program as a standalone
   * jar file.
   * 
   * @param useStream whether to read files via a stream
   */
  public static void readFilesAsStream(final boolean useStream)
  {
    readAsStream = useStream;
  }
  
  
  /**
   * Whether to read content files as a stream.
   * 
   * @return whether to read content files as a stream
   */
  public static boolean readFilesAsStream()
  {
    return readAsStream;
  }
  
  
  /**
   * Sleep for the specified number of milliseconds.
   * 
   * @param time the number of milliseconds to sleep
   */
  public static void sleep(final long time)
  {
    try
    {
      Thread.sleep(time);
    }
    catch (InterruptedException e)
    {
      Logger.error(e);
    }
  }
  
  
  /**
   * Return whether this number is effectively zero.
   * 
   * @param value the input value
   * @return whether this is effectively zero
   */
  public static boolean isZero(final double value)
  {
    return ((Math.abs(value) < 0.00000001));
  }
  
  
  /**
   * Return a colon-separated string as two integers, in a Point.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @return a point containing the two integers
   */
  public static Point getStringAsPoint(final String sInput,
                                       final int defaultValue)
  {
    // Declare the return value
    Point pt = new Point(defaultValue, defaultValue);
    
    // Check the input
    if (sInput == null)
    {
      return pt;
    }
    
    // Find the first colon
    final int colonIndex = sInput.indexOf(':');
    if (colonIndex < 0)
    {
      // There is no colon, so save the whole string
      // as the x value and return
      pt.x = Utility.getStringAsInteger(sInput, defaultValue, defaultValue);
      return pt;
    }
    
    // Get the two values.  Everything before the colon
    // is returned in x, and everything after the colon
    // is returned in y.
    pt.x = Utility.getStringAsInteger(sInput.substring(0, colonIndex),
                                      defaultValue, defaultValue);
    pt.y = Utility.getStringAsInteger(sInput.substring(colonIndex + 1),
                                      defaultValue, defaultValue);
    
    // Return the value
    return pt;
  }
  
  
  /**
   * Convert a string into an integer.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as an integer
   */
  public static int getStringAsInteger(final String sInput,
                                       final int defaultValue,
                                       final int emptyValue)
  {
    // This is the variable that gets returned
    int value = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return emptyValue;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return emptyValue;
    }
    
    // Convert the number
    try
    {
      value = Integer.parseInt(inStr);
    }
    catch (NumberFormatException nfe)
    {
      value = defaultValue;
    }
    
    // Return the value
    return value;
  }
  
  
  /**
   * Convert a string into a floating point number.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as a float
   */
  public static float getStringAsFloat(final String sInput,
                                       final float defaultValue,
                                       final float emptyValue)
  {
    // This is the variable that gets returned
    float fValue = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return emptyValue;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return emptyValue;
    }
    
    // Convert the number
    try
    {
      fValue = Float.parseFloat(inStr);
    }
    catch (NumberFormatException nfe)
    {
      fValue = defaultValue;
    }
    
    // Return the value
    return fValue;
  }
  
  
  /**
   * Convert a string into a double.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as a double
   */
  public static double getStringAsDouble(final String sInput,
                                         final double defaultValue,
                                         final double emptyValue)
  {
    // This is the variable that gets returned
    double value = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return emptyValue;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return emptyValue;
    }
    
    // Convert the number
    try
    {
      value = Double.parseDouble(inStr);
    }
    catch (NumberFormatException nfe)
    {
      value = defaultValue;
    }
    
    // Return the value
    return value;
  }
  
  
  /**
   * Returns the input value as a string with the default number of
   * digits in the mantissa.
   * 
   * @param value the input value
   * @return the score as a String
   */
  public static String toString(final double value)
  {
    return Utility.toString(value, 5);
  }
  
  
  /**
   * Returns the input value as a string with a specified number of
   * digits in the mantissa.
   * 
   * @param value the input value
   * @param nMantissaDigits the number of digits to the right of the decimal
   * @return the score as a String
   */
  public static String toString(final double value, final int nMantissaDigits)
  {
    // Set reasonable bounds for the precision
    final int nPrecision = Math.min(10, Math.max(0, nMantissaDigits));
    
    // Construct the StringBuffer to hold the formatting string.
    // This is necessary since the format string is variable due
    // to the nMantissaDigits value.  The "-" means the string
    // is left-justified.
    StringBuilder buf = new StringBuilder(10);
    buf.append("%-5.").append(Integer.toString(nPrecision))
       .append("f");
    
    // Construct the string and return it to the caller.
    // Trim it since the output string may have trailing
    // spaces.
    final String result = String.format(buf.toString(), value).trim();
    
    // Save the length
    final int len = result.length();
    if (len < 3)
    {
      return result;
    }
    
    // Check the position of any decimals
    final int decPos = result.lastIndexOf('.');
    if ((decPos < 0) || (decPos >= (len - 2)))
    {
      return result;
    }
    
    // Trim any trailing zeros, except one just after the decimal
    int index = len - 1;
    buf = new StringBuilder(result);
    while ((index > (decPos + 1)) && (result.charAt(index) == '0'))
    {
      // Delete the character
      buf.deleteCharAt(index);
      
      // Decrement the index
      --index;
    }
    
    return buf.toString();
  }
  
  
  /**
   * Return whether the string is null or has no length.
   * 
   * @param msg the input string
   * @return whether the string is null or has no length
   */
  public static boolean isStringEmpty(final String msg)
  {
    return ((msg == null) || (msg.length() == 0));
  }
  
  
  /**
   * Make the application compatible with Apple Macs.
   */
  public static void makeMacCompatible()
  {
    // Set the system properties that a Mac uses
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.showGrowBox", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                       getAppName());
  }
  
  
  public static void listLookAndFeels()
  {
    LookAndFeelInfo[] lafs = javax.swing.UIManager.getInstalledLookAndFeels();
    for (LookAndFeelInfo laf : lafs)
    {
      // TODO
      System.out.println(String.format("%s: %s", laf.getName(), laf.getClassName()));
    }
  }
  
  
  public static void initLookAndFeel()
  {
    initLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
  }
  
  
  /**
   * Initialize the application's Look And Feel with the default
   * for this OS.
   */
  public static void initLookAndFeel(final String clName)
  {
    // Use the default look and feel
    try
    {
      javax.swing.UIManager.setLookAndFeel(clName);
    }
    catch (Exception e)
    {
      System.out.println("Exception setting L&F: " + e.getMessage());
      Logger.error("Exception: " + e.getMessage());
    }
  }
  
  
  /**
   * Verify the user has the minimum version of the JVM
   * needed to run the application.
   * 
   * @return whether the JVM is the minimum supported version
   */
  public static boolean hasRequiredJVMVersion()
  {
    // The value that gets returned
    boolean status = true;
    
    // Check the version number
    status = SystemInfo.isJava5orHigher();
    if (!status)
    {
      // This will hold the error string
      StringBuilder sb = new StringBuilder(100);
      
      sb.append("This application requires Java 1.5 (5.0) or later")
        .append(".\nYour installed version of Java is ")
        .append(SystemInfo.getCurrentJavaVersionAsString())
        .append('.');
      
      // Show an error message to the user
      JOptionPane.showMessageDialog(null, sb.toString(),
                         "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Return the status code
    return status;
  }
  
  
  /**
   * Center the application on the screen.
   * 
   * @param comp the component to center on the screen
   */
  public static void centerOnScreen(final java.awt.Component comp)
  {
    // Get the size of the screen
    Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    
    // Determine the new location of the window
    int x = (screenDim.width - comp.getSize().width) / 2;
    int y = (screenDim.height - comp.getSize().height) / 2;
    
    // Move the window
    comp.setLocation(x, y);
  }
  
  
  /**
   * Return an AWT image based on the filename.
   * 
   * @param fileName the image file name
   * @return the AWT image
   */
  public static java.awt.Image loadImageByName(final String fileName)
  {
    // The icon that gets returned
    Image icon = null;
    
    // Load the resource
    URL url = Utility.class.getResource(fileName);
    if (url != null)
    {
      icon = Toolkit.getDefaultToolkit().getImage(url);
    }
    else
    {
      Logger.error("Error: Unable to open the file " + fileName);
      return null;
    }
    
    // Return the AWT image
    return icon;
  }
  
  
  /**
   * Load the buffered image by name.
   * 
   * @param loc the name of the image file
   * @return the image
   */
  public static BufferedImage loadBufferedImageByName(final String loc)
  {
    // The icon that gets returned
    BufferedImage image = null;
    
    // Load the resource
    URL url = Utility.class.getResource(loc);
    if (url != null)
    {
      try
      {
        image = ImageIO.read(url);
      }
      catch (IOException e)
      {
        Logger.error("Exception opening " + loc + ": " + e.getMessage());
        image = null;
      }
    }
    else
    {
      Logger.error("Error: Unable to open the file " + loc);
      return null;
    }
    
    return image;
  }
  
  
  /**
   * Load the icon by name.
   * 
   * @param loc the name of the icon file
   * @return the image
   */
  public static ImageIcon loadIconByName(final String loc)
  {
    // The icon that gets returned
    ImageIcon icon = null;
    
    // Load the resource
    URL url = Utility.class.getResource(loc);
    if (url != null)
    {
      icon = new ImageIcon(url);
    }
    else
    {
      Logger.error("Error: Unable to open the file " + loc);
      return null;
    }
    
    return icon;
  }
  
  
  /**
   * Store the properties object to the filename.
   * 
   * @param filename name of the output file
   * @param props the properties to store
   */
  public static void storeProperties(final String filename,
                                     final Properties props)
  {
    // Write the properties to a file
    FileOutputStream outStream = null;
    try
    {
      // Open the output stream
      outStream = new FileOutputStream(filename);
      
      // Save the properties
      props.store(outStream, "Properties file");
      
      // Close the stream
      outStream.close();
      outStream = null;
    }
    catch (FileNotFoundException fnfe)
    {
      Logger.error("File not found: " + fnfe.getMessage());
    }
    catch (IOException ioe)
    {
      Logger.error("IOException: " + ioe.getMessage());
    }
    finally
    {
      if (outStream != null)
      {
        try
        {
          outStream.close();
        }
        catch (IOException ioe)
        {
          Logger.error("IOException: " + ioe.getMessage());
        }
        
        outStream = null;
      }
    }
  }
  
  
  /**
   * Load the properties object.
   * 
   * @param filename the input file name
   * @return the loaded properties
   */
  public static Properties getProperties(final String filename)
  {
    // The object that gets returned
    Properties props = null;
    
    InputStream propStream = null;
    try
    {
      // Open the input stream as a file
      propStream = new FileInputStream(filename);
      
      // Check for an error
      if (propStream != null)
      {
        // Load the input stream
        props = new Properties();
        props.load(propStream);
        
        // Close the stream
        propStream.close();
        propStream = null;
      }
    }
    catch (IOException ioe)
    {
      props = null;
    }
    finally
    {
      // Make sure we close the stream
      if (propStream != null)
      {
        try
        {
          propStream.close();
        }
        catch (IOException e)
        {
          Logger.error(e.getMessage());
        }
        
        propStream = null;
      }
    }
    
    // Return the properties
    return props;
  }
  
  
  /**
   * Ensure that a particular table cell is visible.
   * 
   * @param table the table
   * @param rowIndex the row index
   * @param vColIndex the column index
   */
  public static void scrollToVisible(final JTable table,
                                     final int rowIndex,
                                     final int vColIndex)
  {
    // Check the parent
    if (!(table.getParent() instanceof JViewport))
    {
      return;
    }
    
    // Get the parent viewport
    JViewport viewport = (JViewport) table.getParent();
    
    // This rectangle is relative to the table where the
    // northwest corner of cell (0,0) is always (0,0).
    final Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    
    // The location of the viewport relative to the table
    final Point pt = viewport.getViewPosition();
    
    // Translate the cell location so that it is relative
    // to the view, assuming the northwest corner of the
    // view is (0,0)
    rect.setLocation(rect.x - pt.x, rect.y - pt.y);
    
    // Scroll the area into view
    viewport.scrollRectToVisible(rect);
  }
  
  
  /**
   * Format the date as a string, using a standard format.
   * 
   * @param date the date to format
   * @return the date as a string
   */
  public static String getDateString(final Date date)
  {
    // Declare our formatter
    java.text.SimpleDateFormat formatter = new
    java.text.SimpleDateFormat("MMMM d, yyyy");
    
    // Return the date/time as a string
    return formatter.format(date);
  }
  
  
  public static String getDateTimeString()
  {
    return getDateTimeString(new Date());
  }
  
  
  /**
   * Format the date and time as a string, using a standard format.
   * 
   * @return the date as a string
   */
  public static String getDateTimeString(final Date date)
  {
    // Declare our formatter
    java.text.SimpleDateFormat formatter = new
      java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    // Return the date/time as a string
    return formatter.format(date);
  }
  
  
  /**
   * Convert a boolean array into a string.  Used for database
   * access and the listRoles and listPrimaries arrays.
   * 
   * @param data the boolean array
   * @return the array as a string
   */
  public static String listToString(final boolean[] data)
  {
    StringBuilder sb = new StringBuilder(data.length);
    for (int i = 0; i < data.length; ++i)
    {
      sb.append((data[i]) ? '1' : '0');
    }
    return sb.toString();
  }
  
  
  /**
   * Convert a string into an array of booleans.
   * 
   * @param str the input string
   * @param listData the output array
   */
  public static void stringToBooleanList(final String str,
                                         final boolean[] listData)
  {
    // Check the input for a null or empty string, or no 1's
    if ((str == null) || (str.length() < 1) || (str.indexOf('1') < 0))
    {
      // Fill the array with false
      java.util.Arrays.fill(listData, false);
      return;
    }
    
    // Iterate over the string
    final int len = str.length();
    for (int i = 0; i < 6; ++i)
    {
      // Check if we're before the end of the string
      if (i < len)
      {
        // We are, so convert the character into a boolean
        listData[i] = (str.charAt(i) == '1');
      }
      else
      {
        // We're past the end of the array, so assume false
        listData[i] = false;
      }
    }
  }
  
  
  /**
   * Parse a string of time (in minutes and seconds) and return
   * the time in seconds.  Acceptable input formats are SS, :SS
   * and MM:SS.
   * 
   * @param timeStr the input time string
   * @return the number of seconds in the time
   */
  public static int getMSTimeInSeconds(final String timeStr)
  {
    // Check the input
    if ((timeStr == null) || (timeStr.trim().length() < 1))
    {
      // The input string is invalid
      return 0;
    }
    
    // Trim the string
    final String time = timeStr.trim();
    
    // Check for a colon
    final int colonIndex = time.indexOf(':');
    if (colonIndex < 0)
    {
      // There is no colon, so just parse the string as the
      // number of seconds
      return getStringAsInteger(time, 0, 0);
    }
    else if (colonIndex == 0)
    {
      // There is a colon at the start, so just parse the rest
      // of the string as the number of seconds
      return getStringAsInteger(time.substring(1), 0, 0);
    }
    
    // There is a colon inside the string, so parse the
    // minutes (before) and seconds (after), and then add
    int mins = 60 * (getStringAsInteger(time.substring(0, colonIndex), 0, 0));
    int secs = getStringAsInteger(time.substring(colonIndex + 1), 0, 0);
    return (mins + secs);
  }
  
  
  /**
   * Parse a string of time (in hours and minutes) and return
   * the time in minutes.  Acceptable input formats are MM, :MM
   * and HH:MM.
   * 
   * @param timeStr the input time string
   * @return the number of minutes in the time
   */
  public static int getHMTimeInMinutes(final String timeStr)
  {
    // Check the input
    if ((timeStr == null) || (timeStr.trim().length() < 1))
    {
      // The input string is invalid
      return 0;
    }
    
    // Trim the string
    final String time = timeStr.trim();
    
    // Check for a colon
    final int colonIndex = time.indexOf(':');
    if (colonIndex < 0)
    {
      // There is no colon, so just parse the string as the
      // number of minutes
      return getStringAsInteger(time, 0, 0);
    }
    else if (colonIndex == 0)
    {
      // There is a colon at the start, so just parse the rest
      // of the string as the number of minutes
      return getStringAsInteger(time.substring(1).trim(), 0, 0);
    }
    
    // There is a colon inside the string, so parse the
    // hours (before) and minutes (after), and then add
    // together after multiplying the hours by 60 (to put
    // in minutes)
    int hrs = 60 * (getStringAsInteger(time.substring(0, colonIndex), 0, 0));
    int mins = getStringAsInteger(time.substring(colonIndex + 1), 0, 0);
    return (hrs + mins);
  }
  
  
  public static String getFileNameFromZipEntry(String filename)
  {
    final int len = filename.length();
    int index = filename.lastIndexOf('/');
    if (index < 0)
    {
      return filename;
    }
    else if (index == (len - 1))
    {
      return "";
    }
    
    // Return everything after the '/'
    return filename.substring(index + 1);
  }
  
  
  public static String getDirNameFromZipEntry(String filename)
  {
    // final int len = filename.length();
    int index = filename.lastIndexOf('/');
    if (index <= 0)
    {
      // There's no directory
      return "";
    }
    
    // Return everything before the slash
    return filename.substring(0, index);
  }
  
  
  public static String shortenToFirstLast(String filename)
  {
    if ((filename == null) || (filename.length() < 2))
    {
      return filename;
    }
    
    final int firstIndex = filename.indexOf('/', 1);
    final int lastIndex = filename.lastIndexOf('/');
    
    if ((firstIndex < 0) || (firstIndex == lastIndex))
    {
      return filename;
    }
    
    String result = filename.substring(0, firstIndex) + filename.substring(lastIndex);
    return result;
  }
  
  
  /**
   * Copy text to the clipboard.
   * 
   * @param msg the string to copy to the clipboard
   */
  public static void copyToClipboard(final String msg)
  {
    StringSelection stringSelection = new StringSelection(msg);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }
}
