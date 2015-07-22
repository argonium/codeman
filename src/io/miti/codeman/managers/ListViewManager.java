package io.miti.codeman.managers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import io.miti.codeman.actions.MousePopupListener;
import io.miti.codeman.model.FileListModel;
import io.miti.codeman.util.Content;
import io.miti.codeman.util.StripeRenderer;

public final class ListViewManager
{
  /** The one instance of this class. */
  private static final ListViewManager inst;
  
  /** The panel for this class. */
  private JPanel view = null;
  
  /** The text field. */
  private JTextField tfSearch = null;
  
  /** The list. */
  private JList<String> listFiles = null;
  
  /** The button to clear the search filter. */
  private JButton btnClear = null;
  
  static
  {
    inst = new ListViewManager();
  }
  
  
  /**
   * Default constructor.
   */
  private ListViewManager()
  {
    initializeView();
  }
  
  
  public static ListViewManager getInstance()
  {
    return inst;
  }
  
  
  public JPanel getPanel()
  {
    return view;
  }
  
  
  @SuppressWarnings("unchecked")
  private void initializeView()
  {
    view = new JPanel(new BorderLayout());
    
    // Set up the top panel (search field and button to clear the query)
    JPanel topPanel = new JPanel(new BorderLayout());
    tfSearch = new JTextField();
    tfSearch.setToolTipText("Enter words to match on filename");
    tfSearch.addKeyListener(new KeyListener()
    {
      @Override
      public void keyPressed(KeyEvent arg0){}

      @Override
      public void keyReleased(KeyEvent arg0)
      {
        keyChanged(tfSearch.getText());
      }

      @Override
      public void keyTyped(KeyEvent arg0) {}
    });
    topPanel.add(tfSearch, BorderLayout.CENTER);
    btnClear = new JButton(Content.getIcon("cross.png"));
    btnClear.setToolTipText("Clear the filter");
    btnClear.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent arg)
      {
        tfSearch.setText("");
        keyChanged("");
        tfSearch.requestFocusInWindow();
      }
    });
    topPanel.add(btnClear, BorderLayout.EAST);
    
    // Build the panel in the middle of the view (for listing files)
    listFiles = new JList<String>();
    listFiles.setModel(new FileListModel());
    listFiles.setCellRenderer(new StripeRenderer());
    
    // Add the listener for the popup menu
    listFiles.addMouseListener(new MousePopupListener(listFiles));
    
    // Build the view
    view.add(topPanel, BorderLayout.NORTH);
    view.add(new JScrollPane(listFiles), BorderLayout.CENTER);
  }
  
  
  private void keyChanged(final String query)
  {
    // Execute the search
    if (((FileListModel) listFiles.getModel()).setSubsetKey(query, false))
    {
      // Redraw the list since the data changed
      ((FileListModel) listFiles.getModel()).contentChanged();
    }
  }
  
  
  public void updateView()
  {
    final boolean isOpen = ZipManager.getInstance().isFileOpen();
    if (isOpen)
    {
      btnClear.setEnabled(true);
      tfSearch.setEnabled(true);
      ((FileListModel) listFiles.getModel()).setSubsetKey("", true);
      ((FileListModel) listFiles.getModel()).contentChanged();
    }
    else
    {
      btnClear.setEnabled(false);
      tfSearch.setEnabled(false);
      ((FileListModel) listFiles.getModel()).clearList();
    }
  }
}
