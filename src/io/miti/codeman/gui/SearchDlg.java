package io.miti.codeman.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import io.miti.codeman.domain.SearchResult;
import io.miti.codeman.managers.ZipManager;
import io.miti.codeman.util.SpringUtilities;
import io.miti.codeman.util.Utility;
import io.miti.codeman.util.WildcardFilter;

public class SearchDlg extends JDialog
{
  private static final long serialVersionUID = 1L;
  private static SearchDlg dlg = null;
  
  /** The UI controls. */
  private JTextField tfQuery = null;
  private JTextField tfFile  = null;
  private JTextField tfDir   = null;
  private JTextField tfMax   = null;
  private JCheckBox  cbMax   = null;
  
  /** The search results. */
  private List<SearchResult> results = null;
  
  /**
   * Default constructor.
   */
  private SearchDlg()
  {
    super();
  }
  
  
  /**
   * Private constructor.
   * 
   * @param frame
   * @param string
   * @param b
   */
  private SearchDlg(JFrame frame, String string, boolean b)
  {
    super(frame, string, b);
  }
  
  
  /**
   * Initialize the UI components for the dialog box.
   */
  private void init()
  {
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    panel.setLayout(new BorderLayout());
    
    final JPanel midPanel = new JPanel(new SpringLayout());
    JLabel lblQuery = new JLabel("Query:");
    lblQuery.setDisplayedMnemonic(KeyEvent.VK_Q);
    tfQuery = new JTextField(20);
    lblQuery.setLabelFor(tfQuery);
    
    JLabel lblFile = new JLabel("File:");
    lblFile.setDisplayedMnemonic(KeyEvent.VK_F);
    tfFile = new JTextField(20);
    lblFile.setLabelFor(tfFile);
    
    JLabel lblDir = new JLabel("Directory:");
    lblDir.setDisplayedMnemonic(KeyEvent.VK_D);
    tfDir = new JTextField(20);
    lblDir.setLabelFor(tfDir);
    
    JLabel lblMax = new JLabel("Max Results:");
    lblMax.setDisplayedMnemonic(KeyEvent.VK_M);
    
    JPanel panelMax = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    tfMax = new JTextField(5);
    cbMax = new JCheckBox("Limit results");
    cbMax.setMnemonic(KeyEvent.VK_L);
    cbMax.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final JCheckBox src = (JCheckBox) e.getSource();
        tfMax.setEnabled(src.isSelected());
      }
    });
    cbMax.setSelected(true);
    lblMax.setLabelFor(tfMax);
    tfMax.setText("20");
    panelMax.add(cbMax);
    panelMax.add(Box.createHorizontalStrut(12));
    panelMax.add(tfMax);
    
    midPanel.add(lblQuery);
    midPanel.add(tfQuery);
    midPanel.add(lblFile);
    midPanel.add(tfFile);
    midPanel.add(lblDir);
    midPanel.add(tfDir);
    midPanel.add(lblMax);
    midPanel.add(panelMax);
    
    SpringUtilities.makeCompactGrid(midPanel, 4, 2, 20, 10, 10, 10);
    panel.add(midPanel, BorderLayout.CENTER);
    
    final JPanel southPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    JButton btnSearch = new JButton("Search");
    btnSearch.setMnemonic(KeyEvent.VK_S);
    btnSearch.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        performSearch();
      }
    });
    JButton btnCancel = new JButton("Cancel");
    btnCancel.setMnemonic(KeyEvent.VK_C);
    btnCancel.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        skipSearch();
      }
    });
    southPanel.add(btnSearch);
    southPanel.add(btnCancel);
    southPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
    panel.add(southPanel, BorderLayout.SOUTH);
    
    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);
    
    // setSize(300, 300);
    pack();
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    setLocationRelativeTo(getParent());
  }
  
  
  /**
   * The one instance of the dialog box.
   * 
   * @param frame the parent frame
   * @return the one instance of the dialog box
   */
  public static SearchDlg getInstance(final JFrame frame)
  {
    if (dlg == null)
    {
      dlg = new SearchDlg(frame, "Search", true);
      dlg.init();
    }
    
    dlg.setVisible(true);
    return dlg;
  }
  
  
  /**
   * Get the list of search results.
   * 
   * @return the list of search results
   */
  public List<SearchResult> getSearches()
  {
    return results;
  }
  
  
  /**
   * Clear any cached results.
   */
  private void clearResults()
  {
    if (results != null)
    {
      results.clear();
    }
    
    results = null;
  }
  
  
  /**
   * Hide the dialog box.
   */
  private void skipSearch()
  {
    // Hide the dialog box
    setVisible(false);
  }
  
  
  /**
   * Perform the search.
   */
  private void performSearch()
  {
    // Clear past results (if any)
    clearResults();
    
    // Get the fields from the UI
    String query = tfQuery.getText();
    String files = tfFile.getText();
    String dirs = tfDir.getText();
    boolean useMax = cbMax.isSelected();
    int maxResults = getMaxResultSize(useMax);
    
    if ((query == null) || (query.trim().length() == 0))
    {
      return;
    }
    
    // Call Lucene for the query
    results = ZipManager.getInstance().search(query);
    if ((results == null) || (results.isEmpty()))
    {
      return;
    }
    
    // Parse files into an array, and remove any files whose name doesn't
    // match the wildcard filter
    List<String> fileNames = parseBySpace(files);
    parseByFileName(fileNames);
    
    // Parse dirs into an array (space-delimited), and remove any files
    // who dir doesn't contain one of the strings
    List<String> dirNames = parseBySpace(dirs);
    parseByDirName(dirNames);
    
    // If useMax, then remove any results after #maxResults
    if (useMax && (results.size() > maxResults))
    {
      int last = results.size() - 1;
      while (last >= maxResults)
      {
        results.remove(last--);
      }
    }
    
    // Hide the dialog box
    setVisible(false);
  }
  
  
  /**
   * Parse the results by DIR name.  There's an implicit OR for the
   * directory names to match the search results (the result just
   * has to match one of the search strings for the dir name).
   * 
   * @param dirNames the array of directory name substrings
   */
  private void parseByDirName(final List<String> dirNames)
  {
    if ((dirNames != null) && (dirNames.size() > 0))
    {
      // Iterate through all of the search results
      final int numResults = results.size();
      for (int i = numResults - 1; i >= 0; --i)
      {
        final SearchResult sr = results.get(i);
        final String name = Utility.getDirNameFromZipEntry(sr.getDoc());
        
        // Iterate through all of the file name values, and see if the
        // search result matches one of them
        // final int nameSize = fileNames.size() - 1;
        boolean keepResult = false;
        for (String dname : dirNames)
        {
          if (name.contains(dname))
          {
            keepResult = true;
            break;
          }
        }
        
        if (!keepResult)
        {
          results.remove(i);
        }
      }
    }
  }
  
  
  /**
   * Parse the results by FILE name.  There's an implicit OR for the
   * file names to match the search results (the result just
   * has to match one of the search strings for the file name).
   * 
   * @param fileNames the array of file name substrings
   */
  private void parseByFileName(final List<String> fileNames)
  {
    if ((fileNames != null) && (fileNames.size() > 0))
    {
      // Iterate through all of the search results
      final int numResults = results.size();
      for (int i = numResults - 1; i >= 0; --i)
      {
        final SearchResult sr = results.get(i);
        final String name = Utility.getFileNameFromZipEntry(sr.getDoc());
        
        // Iterate through all of the file name values, and see if the
        // search result matches one of them
        // final int nameSize = fileNames.size() - 1;
        boolean keepResult = false;
        for (String fname : fileNames)
        {
          WildcardFilter filter = new WildcardFilter(fname, true);
          
          // Check if the filename matches the wildcard
          if (filter.accept(name))
          {
            keepResult = true;
            break;
          }
        }
        
        if (!keepResult)
        {
          results.remove(i);
        }
      }
    }
  }
  
  
  /**
   * Get the maximum number of search results to return.
   * 
   * @param useMax whether to use the entered value
   * @return the maximum number of results
   */
  private int getMaxResultSize(boolean useMax)
  {
    if (!useMax)
    {
      return 0;
    }
    
    int max = 0;
    String strMax = tfMax.getText();
    try
    {
      max = Integer.parseInt(strMax);
    }
    catch (NumberFormatException nfe)
    {
      nfe.printStackTrace();
      max = 0;
    }
    
    return max;
  }
  
  
  /**
   * Parse a list of strings by space.
   * 
   * @param str the input string
   * @return the array of strings
   */
  private List<String> parseBySpace(final String str)
  {
    if ((str == null) || (str.trim().length() == 0))
    {
      return null;
    }
    
    List<String> list = new ArrayList<String>(20);
    StringTokenizer st = new StringTokenizer(str.trim(), " ");
    while (st.hasMoreTokens())
    {
      String nextToken = st.nextToken();
      list.add(nextToken);
    }
    
    return list;
  }
}
