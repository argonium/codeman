package io.miti.codeman.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import io.miti.codeman.managers.TabViewManager;

public class TabHeaderPopupMenu extends JPopupMenu
{
  private static final long serialVersionUID = 1L;
  
  private Point clickPoint = null;

  public TabHeaderPopupMenu()
  {
    initialize();
  }

  public TabHeaderPopupMenu(String label)
  {
    super(label);
  }
  
  
  private void initialize()
  {
    JMenuItem closeTab = new JMenuItem("Close");
    closeTab.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent arg)
      {
        closeCurrentTab(arg);
      }
    });
    add(closeTab);
    
    JMenuItem closeAllTabs = new JMenuItem("Close All");
    closeAllTabs.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent arg)
      {
        closeAllTabs(arg);
      }
    });
    add(closeAllTabs);
    
    JMenuItem closeOtherTabs = new JMenuItem("Close Others");
    closeOtherTabs.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent arg)
      {
        closeOtherTabs(arg);
      }
    });
    add(closeOtherTabs);
    
    addSeparator();
    
    JMenuItem openEditor = new JMenuItem("Open Editor");
    openEditor.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent arg)
      {
        openEditor(arg);
      }
    });
    add(openEditor);
  }
  
  
  private void closeCurrentTab(final ActionEvent arg)
  {
    TabViewManager.getInstance().closeCurrentTab(clickPoint);
  }
  
  
  private void closeAllTabs(final ActionEvent arg)
  {
    TabViewManager.getInstance().closeAllTabs();
  }
  
  
  private void closeOtherTabs(final ActionEvent arg)
  {
    TabViewManager.getInstance().closeOtherTabs(clickPoint);
  }
  
  
  private void openEditor(final ActionEvent arg)
  {
    TabViewManager.getInstance().openEditor();
  }

  public void setPopupPoint(int x, int y)
  {
    clickPoint = new Point(x, y);
  }
}
