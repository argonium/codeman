package com.nexagis.codeman.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nexagis.codeman.managers.ZipManager;

public final class FileSubset
{
  /** The key for this subset. */
  private CodeKey key = null;
  
  /** The list of indexes into the file list, for this subset. */
  private List<Integer> lookup = new ArrayList<Integer>(50);
  
  /**
   * Default constructor.
   */
  public FileSubset()
  {
    super();
  }
  
  
  /**
   * Constructor taking an artist and album name.
   * 
   * @param artist the artist name
   * @param album the album name
   */
  public FileSubset(final String file)
  {
    buildKey(file);
    loadSubset();
  }
  
  
  /**
   * Build the key using the artist and album name.
   * 
   * @param artist the artist name
   * @param album the album name
   */
  private void buildKey(final String file)
  {
    key = new CodeKey(file);
  }
  
  
  /**
   * Load the subset.
   */
  private void loadSubset()
  {
    int index = 0;
    Iterator<String> iter = ZipManager.getInstance().getFileIterator();
    if (iter == null)
    {
      // System.err.println("The file list iterator is null in FileSubset");
      return;
    }
    
    while (iter.hasNext())
    {
      // Get the next file in the list
      String file = iter.next();
      
      // Check for a match
      if (key.codeMatches(file))
      {
        lookup.add(Integer.valueOf(index));
      }
      
      ++index;
    }
  }
  
  
  /**
   * Return the size of the subset.
   * 
   * @return the size of the subset
   */
  public int getCount()
  {
    return lookup.size();
  }

  
  /**
   * Return the key data.
   * 
   * @return the key data
   */
  public CodeKey getKey()
  {
    return key;
  }
  
  
  /**
   * Return the album at an index.
   * 
   * @param index the index of the album to return
   * @return the album at the index
   */
  public String getFile(final int index)
  {
    return ZipManager.getInstance().getFileAt(lookup.get(index));
  }
  
  
  /**
   * Return whether the key is empty.
   * 
   * @return if the key is empty
   */
  public boolean isKeyEmpty()
  {
    return key.isEmpty();
  }
  
  
  /**
   * Return whether the key is based on the supplied artist and album name.
   * 
   * @param artist the name of the artist
   * @param album the name of the album
   * @return whether the key is based on artist and album
   */
  public boolean isBasedOn(final String file)
  {
    // return file.toLowerCase().contains(key.toLowerCase());
    return key.isBasedOn(file);
  }
  
  
  /**
   * Empty the list.
   */
  public void clear()
  {
    lookup.clear();
    lookup = null;
  }
}
