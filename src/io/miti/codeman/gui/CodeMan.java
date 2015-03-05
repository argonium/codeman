/**
 * @(#)SwingShell.java
 * 
 * Created on Dec 18, 2006
 *
 * Copyright 2006 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package com.nexagis.codeman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import com.nexagis.codeman.managers.ListViewManager;
import com.nexagis.codeman.managers.MenuBarManager;
import com.nexagis.codeman.managers.TabViewManager;
import com.nexagis.codeman.managers.ZipManager;
import com.nexagis.codeman.util.Content;
import com.nexagis.codeman.util.Utility;
import com.nexagis.codeman.util.WindowState;

/**
 * This is the main class for the application.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class CodeMan
{
  /** The name of the properties file. */
  public static final String PROPS_FILE_NAME = "cm.prop";
  
  /** The one instance of this class. */
  private static final CodeMan app;
  
  /** The application frame. */
  public JFrame frame = null;
  
  /** The status bar. */
  private JLabel statusBar = null;
  
  /** The window state (position and size). */
  private WindowState windowState = null;
  
  /** The middle panels. */
  private JSplitPane splitPane = null;
  
  static
  {
    app = new CodeMan();
  }
  
  /**
   * Default constructor.
   */
  private CodeMan()
  {
    super();
  }
  
  
  /**
   * Create the application's GUI.
   */
  private void createGUI()
  {
    // Load the properties file
    windowState = WindowState.getInstance();
    
    // Determine whether we're running in Eclipse or as a stand-alone jar
    checkInputFileSource();
    
    // Set up the frame
    setupFrame();
    
    // Initialize the menu bar
    MenuBarManager.getManager().startup();
    frame.setJMenuBar(MenuBarManager.getManager().getMenuBar());
    
    // Create the empty middle window
    initScreen();
    
    // Set up the status bar
    initStatusBar();
    
    // Display the window.
    frame.pack();
    frame.setVisible(true);
    frame.setIconImage(Content.getIcon("appicon2.png").getImage());
    splitPane.setDividerLocation(0.3);
    
    // Check if we need to load a zip file
    ZipManager.getInstance().checkIfZipLoadNeeded();
    
    // Update the display
    ListViewManager.getInstance().updateView();
  }
  
  
  /**
   * Set up the application frame.
   */
  private void setupFrame()
  {
    // Create and set up the window.
    frame = new JFrame(Utility.getAppName());
    
    // Have the frame call exitApp() whenever it closes
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(final WindowEvent e)
      {
        exitApp();
      }
    });
    
    // Set up the size of the frame
    frame.setPreferredSize(windowState.getSize());
    frame.setSize(windowState.getSize());
    
    // Set the position
    if (windowState.shouldCenter())
    {
      frame.setLocationRelativeTo(null);
    }
    else
    {
      frame.setLocation(windowState.getPosition());
    }
  }
  
  
  /**
   * Initialize the main screen (middle window).
   */
  private void initScreen()
  {
    // Build the panels
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                               ListViewManager.getInstance().getPanel(),
                               TabViewManager.getInstance().getTabbedPane());
    frame.getContentPane().add(splitPane, BorderLayout.CENTER);
  }
  
  
  /**
   * Initialize the status bar.
   */
  private void initStatusBar()
  {
    // Instantiate the status bar
    statusBar = new JLabel("Ready");
    
    // Set the color and border
    statusBar.setForeground(Color.black);
    statusBar.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
                              new SoftBevelBorder(SoftBevelBorder.LOWERED)));
    
    // Add to the content pane
    frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
  }
  
  
  public void setStatusBarText(final String msg)
  {
    if (msg == null)
    {
      statusBar.setText("Ready");
    }
    else
    {
      statusBar.setText(msg);
    }
  }
  
  
  /**
   * Exit the application.
   */
  public void exitApp()
  {
    // Store the window state in the properties file
    windowState.update(frame.getBounds());
    windowState.saveToFile(PROPS_FILE_NAME);
    
    // Close the application by disposing of the frame
    frame.dispose();
  }
  
  
  /**
   * Return the one instance of this class.
   * 
   * @return this instance
   */
  public static CodeMan getApp()
  {
    return app;
  }
  
  
  /**
   * Check how the application is run and save information
   * about the input file.
   */
  private void checkInputFileSource()
  {
    final java.net.URL url = getClass().getResource("/cmds.txt");
    if (url != null)
    {
      // We're running in a jar file
      Utility.readFilesAsStream(true);
    }
    else
    {
      // We're not running in a jar file
      Utility.readFilesAsStream(false);
    }
  }
  
  
  /**
   * Entry point to the application.
   * 
   * @param args arguments passed to the application
   */
  public static void main(final String[] args)
  {
    // Make the application Mac-compatible
    Utility.makeMacCompatible();
    
    // Load the properties file data
    WindowState.load(PROPS_FILE_NAME);
    
    // Initialize the look and feel
    // Utility.listLookAndFeels();
    // Utility.initLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    Utility.initLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
    // Utility.initLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
    
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        // Run the application
        app.createGUI();
      }
    });
  }
}
