package io.miti.codeman.managers;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import io.miti.codeman.actions.AboutAppAction;
import io.miti.codeman.actions.ConsoleViewAction;
import io.miti.codeman.actions.ExitAction;
import io.miti.codeman.actions.IndexZipAction;
import io.miti.codeman.actions.InstallZipAction;
import io.miti.codeman.actions.OpenZipAction;
import io.miti.codeman.actions.TextEditorAction;

public final class MenuBarManager
{
  private static final MenuBarManager mbm;
  
  private JMenuBar menuBar = null;
  
  private JMenuItem itemIndex = null;
  
  static
  {
    mbm = new MenuBarManager();
  }
  
  
  public static MenuBarManager getManager()
  {
    return mbm;
  }
  
  
  public JMenuBar getMenuBar()
  {
    return menuBar;
  }
  
  
  /**
   * Initialize the Menu Bar and associate actions.
   */
  public void startup()
  {
    menuBar = new JMenuBar();
    
    // Build the File menu
    buildFileMenu(menuBar);
    
    // Build the View menu
    buildViewMenu(menuBar);
    
    // Help menu item
    JMenu menuHelp = new JMenu("Help");
    menuHelp.setMnemonic(KeyEvent.VK_H);
    menuBar.add(menuHelp);
    
    // Add the About menu item
    JMenuItem itemAbout = new JMenuItem(new AboutAppAction("About"));
    menuHelp.add(itemAbout);
  }
  
  
  private void buildViewMenu(final JMenuBar menuBar)
  {
    // View menu
    JMenu menuView = new JMenu("View");
    menuView.setMnemonic(KeyEvent.VK_V);
    menuBar.add(menuView);
    
    // Console
    JMenuItem itemCon = new JMenuItem(new ConsoleViewAction("Console"));
    menuView.add(itemCon);
    
    // Text editor (blank)
    JMenuItem itemEd = new JMenuItem(new TextEditorAction("Text Editor"));
    menuView.add(itemEd);
  }
  
  
  /**
   * Build the File menu bar item.
   * 
   * @param menuBar the application menu bar
   */
  private void buildFileMenu(final JMenuBar menuBar)
  {
    // File Menu
    JMenu menuFile = new JMenu("File");
    menuFile.setMnemonic(KeyEvent.VK_F);
    menuBar.add(menuFile);
    
    // New
    JMenuItem itemNew = new JMenuItem(new InstallZipAction("Install Zip"));
    menuFile.add(itemNew);
    
    JMenuItem itemOpen = new JMenuItem(new OpenZipAction("Open Zip"));
    menuFile.add(itemOpen);
    
    menuFile.addSeparator();
    
    itemIndex = new JMenuItem(new IndexZipAction("Index Zip"));
    itemIndex.setEnabled(false);
    menuFile.add(itemIndex);
    
    menuFile.addSeparator();
    
    // Close the application
    JMenuItem itemExit = new JMenuItem(new ExitAction("Exit"));
    menuFile.add(itemExit);
  }
  
  
  public void fileOpened(final boolean isOpen)
  {
    itemIndex.setEnabled(isOpen);
  }
}
