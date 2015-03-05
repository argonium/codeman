package com.nexagis.codeman.domain;

import com.nexagis.codeman.util.Utility;

public final class Endpoint implements Comparable<Endpoint>
{
  /** The file with the endpoint. */
  private String filename = null;
  
  /** The filename that gets displayed. */
  private String displayName = null;
  
  /** The HTTP operation - PUT, POST, GET, etc. */
  private String operation = null;
  
  /** The URL endpoint. */
  private String endpoint = null;
  
  
  /** Default constructor. */
  public Endpoint()
  {
    super();
  }
  
  
  /**
   * Constructor.
   * 
   * @param sFilename the input filename
   * @param sOperation the HTTP operation
   * @param sEndpoint the URL endpoint
   */
  public Endpoint(final String sFilename,
                  final String sOperation,
                  final String sEndpoint)
  {
    filename = sFilename;
    displayName = Utility.shortenToFirstLast(filename);
    operation = sOperation;
    endpoint = sEndpoint;
    
    // Ensure the endpoint starts with "/"
    if ((endpoint != null) && (endpoint.length() > 0) && (endpoint.charAt(0) != '/'))
    {
      endpoint = "/" + endpoint;
    }
  }
  
  
  public String getFilename()
  {
    return filename;
  }
  
  
  public void setFilename(final String fname)
  {
    filename = fname;
  }
  
  
  public String getDisplayName()
  {
    return displayName;
  }
  
  
  public String getEndpoint()
  {
    return endpoint;
  }
  
  
  public String getOperation()
  {
    return operation;
  }
  
  
  public boolean isMatch(final String query)
  {
    if (query == null)
    {
      return true;
    }
    
    return (endpoint.toLowerCase().indexOf(query.toLowerCase()) >= 0);
  }
  
  
  /**
   * Return the object as a CSV string.
   * 
   * @return the object as a CSV string
   */
  public String toCSV()
  {
    String msg = String.format("%s,%s,%s", filename, operation, endpoint);
    return msg;
  }


  @Override
  public int compareTo(Endpoint e)
  {
    int rc = displayName.compareTo(e.displayName);
    if (rc == 0)
    {
      rc = endpoint.compareTo(e.endpoint);
      if (rc == 0)
      {
        rc = operation.compareTo(e.operation);
      }
    }
    
    return rc;
  }
}
