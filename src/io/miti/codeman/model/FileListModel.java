package com.nexagis.codeman.model;

import javax.swing.AbstractListModel;

import com.nexagis.codeman.gui.CodeMan;

public final class FileListModel extends AbstractListModel
{
  private static final long serialVersionUID = 1L;
  
  private FileSubset subset = null;

  public FileListModel()
  {
    subset = new FileSubset("");
  }
  
  public FileListModel(final String query)
  {
    subset = new FileSubset(query);
  }

  @Override
  public Object getElementAt(final int index)
  {
    return subset.getFile(index);
  }

  @Override
  public int getSize() {
    return subset.getCount();
  }
  
  
  /**
   * Update the key used to determine the subset of albums to display.
   * 
   * @param artist the artist name
   * @param album the album name
   * @return whether the table needs to be redrawn
   */
  public boolean setSubsetKey(final String file, final boolean forceUpdate)
  {
    // Optimize this to check for the same codes
    boolean redrawNeeded = false;
    if (forceUpdate || !subset.isBasedOn(file))
    {
      // System.out.println("Subset changed in FileListModel!  Need to redraw.");
      subset.clear();
      subset = new FileSubset(file);
      // System.out.println("Size is now " + getSize());
      
      // Show the number of rows in the status bar
      showRowCount();
      
      // The subset changed, so we need to redraw the list
      redrawNeeded = true;
    }
    
    // Return whether the table needs to be redrawn
    return redrawNeeded;
  }
  
  
  /**
   * Show the number of rows in the status bar.
   */
  public void showRowCount()
  {
    // Update the status bar to show the number of matches
    StringBuilder sb = new StringBuilder(30);
    sb.append(Integer.toString(subset.getCount())).append(" match");
    if (subset.getCount() != 1)
    {
      sb.append("es");
    }
    CodeMan.getApp().setStatusBarText(sb.toString());
  }
  
  
  public void contentChanged()
  {
    fireContentsChanged(this, 0, getSize());
  }
  
  
  public void clearList()
  {
    subset.clear();
    subset = new FileSubset("sadjfhlakjhdfalsdjfh");
    contentChanged();
  }
}
