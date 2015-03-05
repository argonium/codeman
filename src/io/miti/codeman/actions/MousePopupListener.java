package io.miti.codeman.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import io.miti.codeman.managers.TabViewManager;
import io.miti.codeman.managers.ZipManager;
import io.miti.codeman.model.FileListModel;
import io.miti.codeman.util.Utility;

public final class MousePopupListener extends MouseAdapter
{
  private JList tableList = null;
  private JPopupMenu menu = new JPopupMenu();
  private Point point = null;
  
  private static final String eoln = "\r\n";
  
  /**
   * Default constructor.
   */
  public MousePopupListener()
  {
    super();
  }
  
  
  /**
   * Standard constructor for this class.
   * 
   * @param tableData whether we're showing table or column data
   * @param dataList the JList for this popup menu
   */
  public MousePopupListener(final JList dataList)
  {
    tableList = dataList;
    buildPopup();
  }
  
  
  /**
   * Build the popup menu.
   */
  private void buildPopup()
  {
    JMenuItem m1 = new JMenuItem("Open this file");
    m1.addActionListener(new PopupAction(0));
    
    JMenuItem m2 = new JMenuItem("Copy this file");
    m2.addActionListener(new PopupAction(1));
    
    JMenuItem m3 = new JMenuItem("Copy selected files");
    m3.addActionListener(new PopupAction(2));
    
    JMenuItem m4 = new JMenuItem("Copy all files");
    m4.addActionListener(new PopupAction(3));
    
    menu.add(m1);
    menu.add(m2);
    menu.add(m3);
    menu.add(m4);
  }
  
  
  @Override
  public void mouseClicked(final MouseEvent e)
  {
    checkPopup(e);
  }
  
  
  @Override
  public void mousePressed(final MouseEvent e)
  {
    checkPopup(e);
  }
  
  
  @Override
  public void mouseReleased(final MouseEvent e)
  {
    checkPopup(e);
  }
  
  
  /**
   * If the user invoked the popup trigger (right-click), show the popup menu.
   * 
   * @param e the mouse event
   */
  private void checkPopup(final MouseEvent e)
  {
    if (e.isPopupTrigger())
    {
      point = new Point(e.getX(), e.getY());
      updatePopupItems();
      menu.show(tableList, e.getX(), e.getY());
    }
  }
  
  
  /**
   * Enable and disable items in the popup menu as needed.
   */
  private void updatePopupItems()
  {
    // See if the list has any items
    final int len = ((FileListModel) tableList.getModel()).getSize();
    if (len < 1)
    {
      // No items in the list, so disable all menu items
      enableMenuItems(false, new int[] {0, 1, 2, 3});
      return;
    }
    else
    {
      // Enable the "all tables" menu items
      enableMenuItems(true, new int[] {3});
      
      // Check if there's an item near the mouse click
      final int currItem = tableList.locationToIndex(point);
      enableMenuItems((currItem >= 0), new int[] {0, 1});
      
      // Check if any rows are selected
      final int[] sel = tableList.getSelectedIndices();
      enableMenuItems((sel.length > 0), new int[] {2});
    }
  }
  
  
  /**
   * Enable or disable menu items in the popup menu.
   * 
   * @param enable whether to enable the items referenced by the list in ind
   * @param ind the list of indices of popup menu items to enable or disable
   */
  private void enableMenuItems(final boolean enable, final int[] ind)
  {
    final int num = ind.length;
    for (int i = 0; i < num; ++i)
    {
      menu.getComponent(ind[i]).setEnabled(enable);
    }
  }
  
  
  /**
   * The action listener for the items in the popup menu.
   */
  class PopupAction implements ActionListener
  {
    /** The mode for this action - copy one row, selected rows, or all rows. */
    private int mode = 0;
    
    
    /**
     * Constructor.
     * 
     * @param nMode the mode for this instance
     */
    public PopupAction(final int nMode)
    {
      mode = nMode;
    }
    
    
    /**
     * Handle the action.
     * 
     * @param evt the action event
     */
    @Override
    public void actionPerformed(final ActionEvent evt)
    {
      switch (mode)
      {
        case 0:
          openFile(evt);
          break;
          
        case 1:
          copyFileName(evt);
          break;
          
        case 2:
          copySelectedFileNames(evt);
          break;
          
        case 3:
          copyAllFileNames(evt);
          break;
      }
    }
    
    
    /**
     * Open the current file.
     * 
     * @param evt the action event
     */
    private void openFile(final ActionEvent evt)
    {
      final int currItem = tableList.locationToIndex(point);
      if (currItem >= 0)
      {
        String item = (String) ((FileListModel)
            tableList.getModel()).getElementAt(currItem);
        
        String text = ZipManager.getInstance().getFileText(item);
        if (text != null)
        {
          TabViewManager.getInstance().addFileFromZip(item, text);
        }
      }
    }
    
    
    /**
     * Copy the current object.
     * 
     * @param evt the action event
     */
    private void copyFileName(final ActionEvent evt)
    {
      final int currItem = tableList.locationToIndex(point);
      if (currItem >= 0)
      {
        String item = (String) ((FileListModel)
            tableList.getModel()).getElementAt(currItem);
        if (item != null)
        {
          Utility.copyToClipboard(item);
        }
      }
    }
    
    
    /**
     * Copy the current object.
     * 
     * @param evt the action event
     */
    private void copySelectedFileNames(final ActionEvent evt)
    {
      final int[] sel = tableList.getSelectedIndices();
      if ((sel == null) || (sel.length < 1))
      {
        return;
      }
      
      final int len = sel.length;
      StringBuilder sb = new StringBuilder(100);
      String name = (String) ((FileListModel)
              tableList.getModel()).getElementAt(sel[0]);
      sb.append(name);
      for (int i = 1; i < len; ++i)
      {
        name = (String) ((FileListModel)
            tableList.getModel()).getElementAt(sel[i]);
        sb.append(eoln).append(name);
      }
      
      Utility.copyToClipboard(sb.toString());
    }
    
    
    /**
     * Copy the current objects.
     * 
     * @param evt the action event
     */
    private void copyAllFileNames(final ActionEvent evt)
    {
      final int len = ((FileListModel) tableList.getModel()).getSize();
      if (len < 1)
      {
        return;
      }
      
      StringBuilder sb = new StringBuilder(100);
      String name = (String) ((FileListModel)
              tableList.getModel()).getElementAt(0);
      sb.append(name);
      for (int i = 1; i < len; ++i)
      {
        name = (String) ((FileListModel)
            tableList.getModel()).getElementAt(i);
        sb.append(eoln).append(name);
      }
      
      Utility.copyToClipboard(sb.toString());
    }
  }
}
