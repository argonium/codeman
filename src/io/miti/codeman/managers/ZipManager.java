package io.miti.codeman.managers;

import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import io.miti.codeman.domain.SearchResult;
import io.miti.codeman.gui.CodeMan;
import io.miti.codeman.util.Logger;
import io.miti.codeman.util.Utility;
import io.miti.codeman.util.WindowState;
import io.miti.codeman.util.ZipIndexer;

public final class ZipManager
{
  /** The one instance of this class. */
  private static final ZipManager inst;
  
  /** The output directory for the indexed data (Lucene). */
  private static final String INDEX_DIR = "indexes";
  
  /** The name of the properties file stored in each opened directory. */
  private static final String DIR_PROPS_NAME = "codezip.txt";
  
  /** The filename. */
  private String zipfile = null;
  
  /** The output directory. */
  private File outDir = null;
  
  /** The index directory. */
  private File indexDir = null;
  
  /** The list of files in the zip. */
  private List<String> files = null;
  
  static
  {
    inst = new ZipManager();
  }
  
  /** Default constructor. */
  private ZipManager()
  {
    super();
  }
  
  
  /**
   * Return the one instance of this class.
   * 
   * @return the one instance of this class
   */
  public static ZipManager getInstance()
  {
    return inst;
  }
  
  
  public void checkIfZipLoadNeeded()
  {
    // Get the directory name from the zip file, if any
    String zipDir = WindowState.getInstance().getZipDir();
    if ((zipDir == null) || (zipDir.trim().length() < 1))
    {
      // Nothing to do
      return;
    }
    
    // Check if the directory exists
    final File openDir = new File(zipDir);
    if (!openDir.exists() || !openDir.isDirectory())
    {
      JOptionPane.showMessageDialog(CodeMan.getApp().frame,
          "The directory " + zipDir + " does not exist",
          "Error", JOptionPane.ERROR_MESSAGE);
      WindowState.getInstance().setZipDir("");
      outDir = null;
      return;
    }
    
    openDirectory(zipDir);
  }
  
  
  public void openDirectory(final String dirName)
  {
    // Check if this is a valid directory
    final boolean isValidDir = checkForIndex(dirName);
    if (!isValidDir)
    {
      JOptionPane.showMessageDialog(CodeMan.getApp().frame,
          "Error: Not a valid indexed directory", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    // Set the member variables
    outDir = new File(dirName);
    zipfile = getZipFileName();
    indexDir = new File(outDir, INDEX_DIR);
    saveListOfFiles();
    
    // Save the name of the opened directory
    WindowState.getInstance().setZipDir(dirName);
    
    // Check if the indexes or endpoints are out of date
    final boolean oldIndexes = indexIsOld();
    if (oldIndexes)
    {
      int rc = JOptionPane.showConfirmDialog(CodeMan.getApp().frame,
                    "The indexes are outdated.  Refresh?",
                    "Zip indexes out of date",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
      if (rc == JOptionPane.YES_OPTION)
      {
        updateZipIndexEndpoints();
      }
    }
    
    // Set the frame title
    final String name = getNameForFrame();
    CodeMan.getApp().frame.setTitle(name);
    
    // The file was opened, so update the menu bar items
    MenuBarManager.getManager().fileOpened(true);
    ListViewManager.getInstance().updateView();
  }
  
  
  /**
   * Determine whether the index and endpoints data is out of date.
   * 
   * @return whether the zip file needs to be reindexed
   */
  private boolean indexIsOld()
  {
    // Get the date of the zip file
    final File zipFile = new File(zipfile);
    final long zipDate = zipFile.lastModified();
    
    // Get the date of the endpoints file
    final File endFile = EndpointsManager.getOutputFile(outDir);
    final long endDate = endFile.lastModified();
    
    return (zipDate > endDate);
  }
  
  
  private boolean checkForIndex(final String dirName)
  {
    final File dir = new File(dirName);
    if (!dir.exists() || !dir.isDirectory())
    {
      return false;
    }
    else if (!(new File(dir, DIR_PROPS_NAME)).exists())
    {
      return false;
    }
    else if (!(new File(dir, INDEX_DIR)).exists())
    {
      return false;
    }
    
    return true;
  }
  
  
  /**
   * Install a new zip file.
   * 
   * @param filename the input filename
   * @param indexFiles whether the index the contents of the zip file
   */
  public void install(final String filename, final boolean indexFiles)
  {
    // Verify the file name is not null
    if (filename == null)
    {
      Logger.error("Error: Attempting to install a null zip file");
      return;
    }
    
    // Verify the file exists and is not a directory
    File file = new File(filename);
    if (!file.exists() || file.isDirectory())
    {
      Logger.error("Error: Zip file " + filename + " either doesn't exist or is a directory");
      return;
    }
    
    // Save the name
    zipfile = filename;
    Logger.debug("Initializing zip file " + zipfile);
    
    // Create the directory
    createOutputDirectory();
    
    // Save the zip file name and the list of files
    saveZipFileName(zipfile);
    saveListOfFiles();
    
    if (indexFiles)
    {
      updateZipIndexEndpoints();
    }
    
    // The file was opened, so update the menu bar items
    MenuBarManager.getManager().fileOpened(true);
    ListViewManager.getInstance().updateView();
  }
  
  
  public void closeZip()
  {
    zipfile = null;
    outDir = null;
    indexDir = null;
    if (files != null)
    {
      files.clear();
      files = null;
    }
    
    // Update the view since a file was closed
    ListViewManager.getInstance().updateView();
  }
  
  
  public boolean isFileOpen()
  {
    return (zipfile != null);
  }
  
  
  private void saveListOfFiles()
  {
    // Load data into files
    File file = new File(zipfile);
    if (!file.exists())
    {
      Logger.debug("The zip file " + zipfile + " was not found.  Skipping saving the file names.");
      return;
    }
    
    // Allocate the list to save the array to
    files = new ArrayList<String>(20);
    
    // Iterate over the contents of the zip file
    try
    {
      ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
      ZipEntry entry = null;
      while ((entry = zis.getNextEntry()) != null)
      {
        // If it's a directory, skip it
        if (entry.isDirectory())
        {
          continue;
        }
        
        // Get the contents of the file
        final String name = entry.getName();
        files.add(name);
      }
      
      zis.close();
    }
    catch (ZipException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  
  public void updateZipIndexEndpoints()
  {
    // Show a wait cursor
    JFrame frame = CodeMan.getApp().frame;
    Cursor cursor = frame.getCursor();
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    // Index the file
    indexZip();
    
    // Generate the endpoints file
    generateEndpoints();
    
    // Restore the cursor
    frame.setCursor(cursor);
    
    // Let the user know the indexing is complete
    JOptionPane.showMessageDialog(frame,
        "The file has been indexed to " + outDir.getName(),
        "Indexing Complete", JOptionPane.INFORMATION_MESSAGE);
    
    // Put the zip name in the frame title
    final String name = getNameForFrame();
    frame.setTitle(name);
  }


  private String getNameForFrame()
  {
    File infile = new File(zipfile);
    final String name = infile.getName();
    return (Utility.getAppName() + ": " + name);
  }
  
  
  /**
   * Save the name of the opened zip file to the output directory.
   * 
   * @param zipname the name of the opened zip file
   */
  private void saveZipFileName(final String zipname)
  {
    // Save the zip file name to a file in outDir
    Properties prop = new Properties();
    prop.put("zip.name", zipname);
    
    // Create the output file name
    File file = new File(outDir, DIR_PROPS_NAME);
    Utility.storeProperties(file.getAbsolutePath(), prop);
  }
  
  
  /**
   * Get the name of the zip file from the properties file in the directory.
   * 
   * @return the name of the zip file
   */
  private String getZipFileName()
  {
    // Get the properties object
    File file = new File(outDir, DIR_PROPS_NAME);
    Properties prop = Utility.getProperties(file.getAbsolutePath());
    
    // Check the properties
    if (prop == null)
    {
      return null;
    }
    
    String str = prop.getProperty("zip.name");
    return str;
  }
  
  
  /**
   * Generate the endpoints file.
   */
  private void generateEndpoints()
  {
    // TODO We need the output file for checking if indexes are up to date, but
    // the parser doesn't work very well, so just create an empty file for now
    // instead of parsing the files
    // EndpointsManager.getInstance().generate(zipfile, outDir);
    EndpointsManager.getInstance().writeEndpoints(null, EndpointsManager.getOutputFile(outDir));
  }
  
  
  /**
   * Index the contents of the zip file.
   */
  private void indexZip()
  {
    // Index the contents of the zip file
    ZipIndexer zipper = new ZipIndexer();
    Logger.info("Indexing zip file " + zipfile);
    zipper.indexZipFile(zipfile, indexDir);
    Logger.info("Finished indexing the zip file");
  }
  
  
  /**
   * Initialize the output directory names (data and indexes).
   */
  private void createOutputDirectory()
  {
    // Get the root of the file name
    File infile = new File(zipfile);
    final String name = infile.getName();
    String root = name.substring(0, name.indexOf('.'));
    
    // Determine the name of the output directory
    int num = 1;
    boolean match = false;
    while (!match && (num < 100))
    {
      File file = new File(generateNumberedFileName(root, num));
      if (!file.exists())
      {
        match = true;
      }
      else
      {
        ++num;
      }
    }
    
    // Create the output directory
    final String outDirName = generateNumberedFileName(root, num);
    WindowState.getInstance().setZipDir(outDirName);
    outDir = new File(outDirName);
    outDir.mkdirs();
    
    // Create the directory for Lucene
    indexDir = new File(outDir, INDEX_DIR);
    indexDir.mkdirs();
  }
  
  
  /**
   * Generate a directory name so we can check if it exists.
   * 
   * @param root the root of the directory name
   * @param num a number to append to the root
   * @return the generated directory name
   */
  private static String generateNumberedFileName(final String root, final int num)
  {
    return root + "_" + num;
  }
  
  
  public File getOutputDirectory()
  {
    return outDir;
  }
  
  
  /**
   * Initialize the zip manager from settings in the properties file.
   * 
   * @param filename the input file name
   * @param dataDir the data directory
   * @return 
   */
//  public void install(final String filename, final String dataDir)
//  {
//    // Verify the input file name is not null
//    if (filename == null)
//    {
//      Logger.error("Error: Attempting to install a null zip file");
//      return;
//    }
//    
//    // Verify the input file exists and is not a directory
//    File file = new File(filename);
//    if (!file.exists() || file.isDirectory())
//    {
//      Logger.error("Error: Zip file " + filename + " either doesn't exist or is a directory");
//      return;
//    }
//    
//    // Verify the index directory exists
//    File dir = new File(dataDir);
//    if (!dir.exists() || !dir.isDirectory())
//    {
//      Logger.error("Error: Zip directory " + dataDir + " either doesn't exist or is not a directory");
//      return;
//    }
//    
//    // Save the names
//    zipfile = filename;
//    outDir = dir;
//    indexDir = new File(outDir, INDEX_DIR);
//    Logger.debug("Initializing zip file " + zipfile);
//    Logger.debug("Output directory is " + dataDir);
//    Logger.debug("Index directory is " + indexDir.getAbsolutePath());
//  }
  
  
  public Iterator<String> getFileIterator()
  {
    if (files == null)
    {
      return null;
    }
    
    return files.iterator();
  }
  
  
  public String getFileAt(final int index)
  {
    return files.get(index);
  }
  
  
  /**
   * Get the list of endpoints.
   * 
   * @param inFile the file to get the endpoints from
   * @return
   */
  public List<String> getFileList(final String query)
  {
    // Check if anything was loaded
    if (files == null)
    {
      return null;
    }
    
    return copyList(query);
  }
  
  
  /**
   * Copy the list of file names matching on the query.
   * 
   * @param query the query string (null to copy all files)
   * @return the list of matching file names
   */
  private List<String> copyList(final String query)
  {
    final int size = files.size();
    List<String> list = new ArrayList<String>(size);
    for (String fname : files)
    {
      if ((query == null) || (fname.toLowerCase().indexOf(query.toLowerCase()) >= 0))
      {
        list.add(fname);
      }
    }
    
    return list;
  }
  
  
  /**
   * Search the Lucene index for the files with the text.
   * 
   * @param query the query string
   * @return the list of search results
   */
  public List<SearchResult> search(final String query)
  {
    List<SearchResult> results = new ZipIndexer().queryIndex(query, indexDir);
    return results;
  }


  /**
   * Get the contents of the specified file.
   * 
   * @param name the name of the file
   * @return the text from the file
   */
  public String getFileText(String name)
  {
    if (zipfile == null)
    {
      return null;
    }
    
    String text = null;
    try
    {
      ZipFile zip = new ZipFile(zipfile);  
      ZipEntry entry = zip.getEntry(name);
      if (entry != null)
      {
        // Get the text from the file
        InputStream is = zip.getInputStream(entry);
        final java.util.Scanner s = new java.util.Scanner(is);
        s.useDelimiter("\\A");
        text = s.hasNext() ? s.next() : "";
        s.close();
        is.close();
      }
      
      zip.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return text;
  }
  
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("\n");
    
    if (zipfile == null)
    {
      sb.append("Zip file is null\n");
    }
    else
    {
      sb.append("Zip file: " + zipfile + "\n");
      
      // Get the date of the zip file
      final File zipFile = new File(zipfile);
      final long zipDate = zipFile.lastModified();
      sb.append("Zip date is " + Utility.getDateTimeString(new Date(zipDate)) + "\n");
    }
    
    if (outDir == null)
    {
      sb.append("The output directory is null\n");
    }
    else
    {
      // Get the date of the endpoints file
      final File endFile = EndpointsManager.getOutputFile(outDir);
      if (!endFile.exists())
      {
        sb.append("The endpoints file was not found\n");
      }
      else
      {
        final long endDate = endFile.lastModified();
        sb.append("Endpoints date is " + Utility.getDateTimeString(new Date(endDate)) + "\n");
      }
      
      // private File indexDir = null;
      if (indexDir == null)
      {
        sb.append("The index directory is null\n");
      }
      else
      {
        File[] list = indexDir.listFiles();
        if ((list == null) || (list.length == 0))
        {
          sb.append("The list of files in the index is empty\n");
        }
        else
        {
          final long fileDate = list[0].lastModified();
          sb.append("The index was last updated on: " + Utility.getDateTimeString(new Date(fileDate)) + "\n");
        }
      }
      
      if (files == null)
      {
        sb.append("The list of files is empty\n");
      }
      else
      {
        sb.append("The number of files in the zip is " + files.size() + "\n");
      }
    }
    
    return sb.toString();
  }
}
