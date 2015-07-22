package io.miti.codeman.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import io.miti.codeman.domain.Endpoint;
import io.miti.codeman.util.CSVParser;
import io.miti.codeman.util.Logger;
import io.miti.codeman.util.ReqMapParser;

/**
 * Class to generate and manage the list of endpoints for the zip file.
 * 
 * @author mike
 */
public final class EndpointsManager
{
  /** The one instance of this class. */
  private static final EndpointsManager inst;
  
  /** The list of endpoints. */
  private List<Endpoint> listEndpoints = null;
  
  static
  {
    inst = new EndpointsManager();
  }
  
  /** Default constructor. */
  private EndpointsManager()
  {
    super();
  }
  
  
  /**
   * Return the one instance of this class.
   * 
   * @return the one instance of this class
   */
  public static EndpointsManager getInstance()
  {
    return inst;
  }
  
  
  /**
   * Return a reference to the output file.
   * 
   * @param homeDir the directory
   * @return the File reference
   */
  public static File getOutputFile(final File homeDir) {
    return new File(homeDir, "endpoints.txt");
  }
  
  
  /**
   * Generate the list of endpoints and save to a file.
   * 
   * @param zipfile the input zipfile
   * @param file the output file
   */
  public void generate(final String zipfile, final File homeDir)
  {
    // If the cached list is empty, delete it
    if (listEndpoints != null)
    {
      listEndpoints.clear();
      listEndpoints = null;
    }
    
    // Generate the list of endpoints
    try
    {
      // Build a list of the strings in the file
      List<Endpoint> points = new ArrayList<Endpoint>(20);
      
      // Iterate over the contents of the zip file
      ZipInputStream zis = new ZipInputStream(new FileInputStream(zipfile));
      ZipEntry entry = null;
      while ((entry = zis.getNextEntry()) != null)
      {
        // Check if we care about this entry
        if (!shouldProcess(entry))
        {
          continue;
        }
        
        // Save the name
        final String name = entry.getName();
        Logger.debug("Getting endpoints for " + name);
        
        // Get the endpoints for this entry
        BufferedReader br = new BufferedReader(new InputStreamReader(zis));
        List<String> lines = new ArrayList<String>(20);
        String line = br.readLine();
        while (line != null)
        {
          lines.add(line.trim());
          line = br.readLine();
        }
        
        br.close();
        
        // Check the file for endpoints
        addEndpoints(lines, name, points);
      }
      
      // Sort the list
      Collections.sort(points);
      
      // Write any results to file
      final File file = getOutputFile(homeDir);
      writeEndpoints(points, file);
      
      // Close the input stream
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
  
  
  /**
   * Write the endpoints to the output file.
   * 
   * @param points the list of endpoints
   * @param file the output file
   */
  public void writeEndpoints(List<Endpoint> points, File file)
  {
    // Create the file
    BufferedWriter bw = null;
    try
    {
      bw = new BufferedWriter(new FileWriter(file));
      if ((points == null) || (points.isEmpty()))
      {
        // The list is empty, but write something
        bw.write("");
      }
      else
      {
        // Iterate over the list
        for (Endpoint point : points)
        {
          bw.write(point.toCSV() + "\r\n");
        }
      }
      
      bw.flush();
      bw.close();
      bw = null;
    }
    catch (IOException ioe)
    {
      Logger.error(ioe);
    }
  }
  
  
  /**
   * Create the list of endpoints.
   * 
   * @param lines the lines from the zip entry
   * @param filename the input file name (zip entry)
   * @return the list of any endpoints in the zip entry
   */
  private void addEndpoints(final List<String> lines,
                            final String filename,
                            final List<Endpoint> points)
  {
    Logger.debug("Parsing endpoints from " + filename);
    
    // The string to search for
    final String mappingLine = "@RequestMapping";
    
    // Store the endpoint and operation for the class
    String rootEndpoint = null;
    String rootOperation = null;
    
    // Iterate over the file contents
    boolean parentFound = false;
    for (String line : lines)
    {
      // Verify this is a line of interest
      final int startIndex = line.indexOf(mappingLine);
      if (startIndex < 0)
      {
        continue;
      }
      
      // Parse the line
      final int parenStart = line.indexOf('(', startIndex);
      final int parenEnd = line.indexOf(')', startIndex);
      if ((parenStart < 0) || (parenEnd < 0) || (parenStart >= (parenEnd - 1)))
      {
        Logger.error("Error: Invalid line from " + filename + ": " + line);
        continue;
      }
      
      // Retrieve the data in the request mapping
      final String data = line.substring(parenStart + 1, parenEnd);
      Logger.debug("  Endpoint=>" + data);
      
      // Parse the line
      Map<String, String> map = new ReqMapParser().parseLine(data, rootEndpoint, rootOperation);
      if ((map == null) || map.isEmpty())
      {
        Logger.error("No results from line=>" + data);
        continue;
      }
      
      // If this is the first time for this file, save the defaults
      if (!parentFound)
      {
        parentFound = true;
        rootEndpoint = map.get("value");
        rootOperation = map.get("method");
        
        // If either is null, we don't have a complete endpoint
        if ((rootEndpoint == null) || (rootOperation == null))
        {
          continue;
        }
      }
      
      // Add the info to results
      points.add(createEndpoint(map, filename));
    }
  }
  
  
  /**
   * Create an endpoint from the supplied data.
   * 
   * @param map the map of value and method data
   * @param filename the input filename
   * @return an endpoint for this dataset
   */
  private Endpoint createEndpoint(Map<String, String> map, final String filename)
  {
    Endpoint point = new Endpoint(filename, map.get("method"), map.get("value"));
    return point;
  }
  
  
  /**
   * Whether to process this zip entry.
   * 
   * @param entry the zip entry
   * @return whether this process this entry
   */
  private boolean shouldProcess(final ZipEntry entry)
  {
    // If it's a directory, skip it
    if (entry.isDirectory())
    {
      return false;
    }
    
    final String name = entry.getName();
    final boolean isJavaController = name.endsWith("Controller.java");
    return isJavaController;
  }
  
  
  /**
   * Get the list of endpoints.
   * 
   * @param inFile the file to get the endpoints from
   * @return
   */
  public List<Endpoint> getList(final File inFile)
  {
    return getList(inFile, null);
  }
  
  
  /**
   * Get the list of endpoints.
   * 
   * @param inFile the file to get the endpoints from
   * @return
   */
  public List<Endpoint> getList(final File inFile, final String query)
  {
    // Check if the list is loaded
    if (listEndpoints == null)
    {
      loadEndpointsFromFile(inFile);
    }
    
    // Check if anything was loaded
    if (listEndpoints == null)
    {
      return null;
    }
    
    return copyList(query);
  }
  
  
  private List<Endpoint> copyList(final String query)
  {
    // Copy listEndpoints
    final int size = listEndpoints.size();
    List<Endpoint> list = new ArrayList<Endpoint>(size);
    for (Endpoint pt : listEndpoints)
    {
      if (pt.isMatch(query))
      {
        list.add(pt);
      }
    }
    
    return list;
  }


  private void loadEndpointsFromFile(final File inFile)
  {
    // Load the list
    listEndpoints = new ArrayList<Endpoint>(50);
    BufferedReader br = null;
    try
    {
      br = new BufferedReader(new FileReader(inFile));
      String line = br.readLine();
      while (line != null)
      {
        if (line.length() > 0)
        {
          listEndpoints.add(createEndpointFromLine(line));
        }
        
        line = br.readLine();
      }
      
      br.close();
      br = null;
      
      Collections.sort(listEndpoints);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  
  private Endpoint createEndpointFromLine(final String line)
  {
    CSVParser parser = new CSVParser();
    Iterator<String> fields = parser.parse(line);
    final String filename = fields.next();
    final String oper = fields.next();
    final String api = fields.next();
    
    Endpoint endpoint = new Endpoint(filename, oper, api);
    return endpoint;
  }
}
