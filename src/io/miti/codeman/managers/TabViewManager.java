/**
 * 
 */
package com.nexagis.codeman.managers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import com.nexagis.codeman.actions.TabHeaderPopupMenu;
import com.nexagis.codeman.console.CmdLineInterpreter;
import com.nexagis.codeman.console.JConsole;
import com.nexagis.codeman.gui.CodeMan;
import com.nexagis.codeman.util.StyleTyper;
import com.nexagis.codeman.util.Utility;

/**
 * The manager for the tabbed view.
 */
public final class TabViewManager
{
  /** The one instance of this class. */
  private static final TabViewManager inst;
  
  /** The JTabbedPane variable. */
  private JTabbedPane tp = null;
  
  /** The popup menu. */
  private TabHeaderPopupMenu popup = null;
  
  /** Whether the header has been initialized or not yet. */
  private boolean headerSetup = false;
  
  private JTextField searchField = null;
  
  private JCheckBox regexCB;
  
  private JCheckBox matchCaseCB;
  
  
  static
  {
    inst = new TabViewManager();
  }
  
  
  /**
   * Default constructor.
   */
  private TabViewManager()
  {
    tp = new JTabbedPane();
    checkHeader();
  }
  
  
  public static TabViewManager getInstance()
  {
    return inst;
  }
  
  
  private void checkHeader()
  {
    if (!headerSetup)
    {
      installMouseListenerWrapper();
      popup = new TabHeaderPopupMenu();
      headerSetup = true;
    }
  }
  
  
  public void addConsole()
  {
    // Search if Console is already open
    boolean isConsoleOpen = false;
    final int tabCount = tp.getTabCount();
    for (int i = 0; i < tabCount; ++i)
    {
      final Component tab = tp.getComponentAt(i);
      if (tab != null)
      {
        String name = tab.getName();
        if ((name != null) && (name.equals("console")))
        {
          isConsoleOpen = true;
          tp.setSelectedIndex(i);
          JConsole.requestCursorFocus();
          break;
        }
      }
    }
    
    // The console is not open, so add it
    if (!isConsoleOpen)
    {
      JConsole appPanel = JConsole.getInstance(new CmdLineInterpreter());
      appPanel.setBackground(Color.WHITE);
      appPanel.setName("console");
      addTab("Console", appPanel);
      JConsole.requestCursorFocus();
      
      checkHeader();
    }
  }
  
  
  public void addFileFromZip(final String filename, final String text)
  {
    // Search if the file is already open
    final String tabName = "FZ:" + filename;
    boolean isOpen = false;
    final int tabCount = tp.getTabCount();
    for (int i = 0; i < tabCount; ++i)
    {
      final Component tab = tp.getComponentAt(i);
      if (tab != null)
      {
        String name = tab.getName();
        if ((name != null) && (name.equals(tabName)))
        {
          isOpen = true;
          tp.setSelectedIndex(i);
          break;
        }
      }
    }
    
    // The console is not open, so add it
    if (!isOpen)
    {
      final RSyntaxTextArea textArea = new RSyntaxTextArea();
      textArea.setText(text);
      textArea.setSyntaxEditingStyle(StyleTyper.getTextStyle(filename));
      textArea.setCodeFoldingEnabled(true);
      textArea.setAntiAliasingEnabled(true);
      textArea.setCaretPosition(0);
      textArea.setEditable(true);
      RTextScrollPane sp = new RTextScrollPane(textArea);
      sp.setFoldIndicatorEnabled(true);
      
      // Add the search fields
      JToolBar toolBar = new JToolBar();
      JLabel lblSearch = new JLabel("Search: ");
      lblSearch.setDisplayedMnemonic(KeyEvent.VK_S);
      toolBar.add(lblSearch);
      searchField = new JTextField(30);
      toolBar.add(searchField);
      lblSearch.setLabelFor(searchField);
      final JButton nextButton = new JButton("Find Next");
      nextButton.setMnemonic(KeyEvent.VK_N);
      nextButton.setDisplayedMnemonicIndex(5);
      nextButton.setActionCommand("FindNext");
      nextButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          performSearch(e, textArea);
        }
      });
      toolBar.add(nextButton);
      searchField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            nextButton.doClick(0);
         }
      });
      JButton prevButton = new JButton("Find Previous");
      prevButton.setMnemonic(KeyEvent.VK_P);
      prevButton.setActionCommand("FindPrev");
      prevButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          performSearch(e, textArea);
        }
      });
      toolBar.add(prevButton);
      regexCB = new JCheckBox("Regex");
      regexCB.setMnemonic(KeyEvent.VK_R);
      toolBar.add(regexCB);
      matchCaseCB = new JCheckBox("Match Case");
      matchCaseCB.setMnemonic(KeyEvent.VK_M);
      toolBar.add(matchCaseCB);      
      
      // Create the main panel
      JPanel appPanel = new JPanel(new BorderLayout());
      appPanel.add(sp, BorderLayout.CENTER);
      appPanel.add(toolBar, BorderLayout.NORTH);
      appPanel.setBackground(Color.WHITE);
      appPanel.setName(tabName);
      
      final String title = Utility.getFileNameFromZipEntry(filename);
      tp.addTab(title, null, appPanel, filename);
      tp.setSelectedIndex(tp.getTabCount() - 1);
      
      checkHeader();
    }
  }
  
  
  public void addTab(final String title, final JPanel panel)
  {
    tp.addTab(title, panel);
    tp.setSelectedIndex(tp.getTabCount() - 1);
    
    checkHeader();
  }
  
  
  public void removeAllTabs()
  {
    tp.removeAll();
  }
  
  
  public JTabbedPane getTabbedPane()
  {
    return tp;
  }
  
  
  private void installMouseListenerWrapper()
  {
    MouseListener handler = findUIMouseListener();
    if (handler != null)
    {
      tp.removeMouseListener(handler);
      tp.addMouseListener(new MouseListenerWrapper(handler));
    }
    else
    {
      System.err.println("Handler is null in TabViewManager!");
    }
  }
  
  
  public void closeCurrentTab(final Point clickPoint)
  {
    // Get the tab at the specified click point
    int sel = tp.getUI().tabForCoordinate(tp, clickPoint.x, clickPoint.y);
    
    if (sel >= 0)
    {
      tp.remove(sel);
    }
  }
  
  
  public void closeAllTabs()
  {
    tp.removeAll();
  }
  
  
  public void closeOtherTabs(final Point clickPoint)
  {
    // Get the tab at the specified click point
    int sel = tp.getUI().tabForCoordinate(tp, clickPoint.x, clickPoint.y);
    
    // If the tab index is non-negative, close all other tabs
    if (sel >= 0)
    {
      final int count = tp.getTabCount();
      for (int i = (count - 1); i >= 0; --i)
      {
        if (i != sel)
        {
          tp.remove(i);
        }
      }
    }
  }
  
  
  public void openEditor()
  {
    RSyntaxTextArea textArea = new RSyntaxTextArea();
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
    textArea.setCodeFoldingEnabled(true);
    textArea.setAntiAliasingEnabled(true);
    textArea.setCaretPosition(0);
    textArea.setEditable(true);
    RTextScrollPane sp = new RTextScrollPane(textArea);
    sp.setFoldIndicatorEnabled(true);
    
    JPanel appPanel = new JPanel(new BorderLayout());
    appPanel.add(sp);
    appPanel.setBackground(Color.WHITE);
    appPanel.setName("TE:Text");
    
    tp.addTab("Text", appPanel);
    tp.setSelectedIndex(tp.getTabCount() - 1);
    textArea.requestFocusInWindow();
    
    checkHeader();
  }
  
  
  /**
   * Open a plain text editor.
   */
  public void openSlimEditor()
  {
    JTextArea taPanel = new JTextArea();
    JScrollPane sp = new JScrollPane(taPanel);
    taPanel.setCaretPosition(0);
    taPanel.setEditable(true);
    JPanel appPanel = new JPanel(new BorderLayout());
    appPanel.add(sp);
    appPanel.setBackground(Color.WHITE);
    appPanel.setName("TE:Text");
    
    tp.addTab("Text", appPanel);
    tp.setSelectedIndex(tp.getTabCount() - 1);
    taPanel.requestFocusInWindow();
    
    checkHeader();
  }
  
  
  private MouseListener findUIMouseListener()
  {
    MouseListener[] listeners = tp.getMouseListeners();
    for (MouseListener l : listeners)
    {
      if (l.getClass().getName().contains("$Handler"))
      {
        return l;
      }
    }
    
    return null;
  }
  
  
  public static class MouseListenerWrapper implements MouseListener
  {
    private MouseListener delegate = null;

    public MouseListenerWrapper(MouseListener delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
      delegate.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
      if (!SwingUtilities.isRightMouseButton(e))
      {
        delegate.mousePressed(e);
        return;
      }
      
      // Show a pop-up menu - close, close others, close all, new editor
      int tabIndex = TabViewManager.inst.tp.getUI().tabForCoordinate(TabViewManager.inst.tp, e.getX(), e.getY());
      if (tabIndex >= 0)
      {
        TabViewManager.inst.popup.setPopupPoint(e.getX(), e.getY());
        TabViewManager.inst.popup.show(e.getComponent(), e.getX(), e.getY());
      }
      
      return;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
      delegate.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
      delegate.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
      delegate.mouseExited(e);
    }
  }
  
  
  public void performSearch(ActionEvent e, RSyntaxTextArea textArea) {

    // "FindNext" => search forward, "FindPrev" => search backward
    String command = e.getActionCommand();
    boolean forward = "FindNext".equals(command);

    // Create an object defining our search parameters.
    SearchContext context = new SearchContext();
    String text = searchField.getText();
    if (text.length() == 0) {
       return;
    }
    context.setSearchFor(text);
    context.setMatchCase(matchCaseCB.isSelected());
    context.setRegularExpression(regexCB.isSelected());
    context.setSearchForward(forward);
    context.setWholeWord(false);

    boolean found = SearchEngine.find(textArea, context);
    if (!found) {
       JOptionPane.showMessageDialog(CodeMan.getApp().frame, "Text not found");
    }
  }  
}
