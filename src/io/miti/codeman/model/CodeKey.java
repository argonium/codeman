/**
 * 
 */
package com.nexagis.codeman.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The search key data for a code.
 * 
 * @author mike
 * @version 1.0
 */
public final class CodeKey
{
	/**
	 * The list of key data.
	 */
	private List<String> key = new ArrayList<String>(10);
	
	/**
	 * Name of the code the key is based on.
	 */
	private String lastFile = null;
	
	
	/**
	 * Default constructor.
	 */
	public CodeKey()
	{
	  super();
	}

	
	/**
	 * Initialize the key based on a new code.
	 * 
	 * @param code the code
	 */
	public CodeKey(final String code)
	{
		key.clear();
		
		lastFile = code;
		buildKey(code);
	}
	
	
	/**
	 * Build the key using the code of code.
	 * The data is appended to the key.
	 * 
	 * @param code the code to parse
	 */
	private void buildKey(final String code)
	{
		StringTokenizer st = new StringTokenizer(code, " ");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken().trim();
			if (token.length() > 0)
			{
			  key.add(token.toLowerCase());
			}
		}
	}
	
	
	/**
	 * Check for a match of an album on the key.
	 * 
	 * @param album the album to check
	 * @return whether the album is a match
	 */
	public boolean codeMatches(final String file)
	{
		// Check if the album is a match on the key
		if ((key == null) || (key.isEmpty()))
		{
			return true;
		}
		
		boolean rc = true;
		for (String code : key)
		{
			if (!file.toLowerCase().contains(code.toLowerCase()))
			{
				rc = false;
				break;
			}
		}
		
		return rc;
	}
	
	
	/**
	 * Print the key data.
	 */
	public void printKey()
	{
		System.out.println("Key...");
		for (String word : key)
		{
			System.out.println(" ==>" + word);
		}
	}

	
	/**
	 * Return whether the key is empty.
	 * 
	 * @return if the key is empty
	 */
	public boolean isEmpty()
	{
		return key.isEmpty();
	}

	
	/**
	 * Return whether this key is based on the supplied parameters.
	 * 
	 * @param code the code
	 * @return whether this key is based on the parameters
	 */
	public boolean isBasedOn(final String code)
	{
		boolean rc = true;
		if (lastFile == null)
		{
			rc = (code == null);
		}
		else
		{
			rc = (code == null) ? false : code.equalsIgnoreCase(lastFile);
		}
		
		return rc;
	}
}
