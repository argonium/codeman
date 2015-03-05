package io.miti.codeman.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class ReqMapParser
{
  /** Default constructor. */
  public ReqMapParser()
  {
    super();
  }
  
  
  /**
   * Parse the tokens in the line.
   * 
   * @param data the request mapping
   * @param rootEndpoint the default endpoint
   * @param rootOperation the default operation
   * @return a map of value and method data
   */
  public Map<String, String> parseLine(final String data,
                                       final String rootEndpoint,
                                       final String rootOperation)
  {
    String key = null;
    Map<String, String> map = new HashMap<String, String>(2);
    
    // Parse on: "...", =, space, comma
    final int len = data.length();
    for (int i = 0; i < len; ++i)
    {
      char ch = data.charAt(i);
      if (ch == '"')
      {
        StringBuilder sb = new StringBuilder(20);
        i = getQuotedString(i, sb, data);
        final String fullEndpoint = generateEndpoint(rootEndpoint, sb.toString());
        map.put((key != null) ? key : "value", fullEndpoint);
      }
      else if (ch == ' ')
      {
        continue;
      }
      else if (ch == ',')
      {
        continue;
      }
      else
      {
        // Get next token
        i = getNextToken(i, data.substring(i), map, rootEndpoint);
        // System.out.println("Token = " + sb.toString());
      }
    }
    
    // Supply any missing info, from the parent
    checkMap(map, "value", rootEndpoint);
    checkMap(map, "method", parseMethod(rootOperation));
    
    // Parse the line:
    //   "blah"
    //   value = "blah"
    //   method = blah
    //   value = "blah", method = blah
    // After processing, if value or method are null, use the root values
    // If both are null, then show an error message and return
    return map;
  }
  
  
  /**
   * Fill in any missing values.
   * 
   * @param map the map to check
   * @param key the key for the value to check
   * @param root the value to supply if it's missing for key
   */
  private void checkMap(final Map<String, String> map,
                        final String key,
                        final String root)
  {
    // Check if we need to fill in a missing value (inherited from the parent)
    final String value = map.get(key);
    if ((value == null) && (root != null))
    {
      map.put(key,  root);
    }
  }
  
  
  /**
   * Get the next token (key = value).
   * 
   * @param index the starting line index
   * @param text the text line to parse
   * @param map the map to store the data in
   * @return the updated line index
   */
  private int getNextToken(final int index,
                           final String text,
                           final Map<String, String> map,
                           final String rootEndpoint)
  {
    int i = index;
    
    // Get the next token or pair of tokens
    int endIndex = text.indexOf(",");
    String data = text.substring(0, (endIndex < 0) ? text.length() : endIndex);
    System.out.println("==>" + data);
    
    // Get the fields before and after the =
    final int equalsIndex = data.indexOf('=');
    final String key = data.substring(0, equalsIndex).trim();
    final String value = data.substring(equalsIndex + 1).trim();
    
    if (key.equals("value"))
    {
      // Verify this is a string
      if ((value.charAt(0) == '"') && (value.charAt(value.length() - 1) == '"'))
      {
        String token = value.substring(1, value.length() - 1);
        final String fullEndpoint = generateEndpoint(rootEndpoint, token);
        map.put(key, fullEndpoint);
      }
      else
      {
        System.err.println("Error getting value from " + value);
      }
    }
    else if (key.equals("method"))
    {
      // Parse out the operation
      final String op = parseMethod(value);
      map.put(key, op);
    }
    else
    {
      throw new UnsupportedOperationException("Illegal token found=>" + key + "<=");
    }
    
    i += data.length();
    return i;
  }
  
  
  private String generateEndpoint(final String root, final String token)
  {
    // There is no root, so return the token
    if ((root == null) || (root.length() < 1))
    {
      return token;
    }
    
    // Concatenate the root and token
    final boolean rootEnds = root.endsWith("/");
    final boolean tokenStarts = token.startsWith("/");
    if (rootEnds && tokenStarts)
    {
      return (root + token.substring(1));
    }
    else if (!rootEnds && !tokenStarts)
    {
      return (root + "/" + token);
    }
    
    return (root + token);
  }
  
  
  private String parseMethod(final String value)
  {
    if (value == null)
    {
      return null;
    }
    
    final String methodToken = "RequestMethod.";
    if (value.startsWith(methodToken))
    {
      return value.substring(methodToken.length());
    }
    
    return value;
  }
  
  
  /**
   * Get a quoted string, as a standalone parameter.
   * 
   * @param index the starting index of the string
   * @param sb holds the parsed string
   * @param data the data line
   * @return the updated line index
   */
  private int getQuotedString(final int index, final StringBuilder sb, final String data)
  {
    int i = index;
    ++i;
    int closeIndex = data.indexOf('"', i);
    String str = data.substring(i, closeIndex);
    sb.append(str);
    i = closeIndex + 1;
    return i;
  }
  
  
  /**
   * Print the map, for debugging.
   * 
   * @param map the map of key/value pairs to print
   */
  private static void printMap(Map<String, String> map)
  {
    System.out.println("Printing map...");
    for (Entry<String, String> entry : map.entrySet())
    {
      System.out.println("  " + entry.getKey() + " ==>" + entry.getValue() + "<==");
    }
  }
  
  
  public static void main(String[] args)
  {
    ReqMapParser rmq = new ReqMapParser();
    printMap(rmq.parseLine("\"blah\"", null, "RequestMethod.POST"));
    printMap(rmq.parseLine("method = RequestMethod.POST, value = \"hoomph\"", null, null));
    printMap(rmq.parseLine("value = \"hoomph\", method = POST", null, null));
    printMap(rmq.parseLine("method = POST", "/api/code-group", null));
  }
}
