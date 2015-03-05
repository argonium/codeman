/**
 * 
 */
package com.nexagis.codeman.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.nexagis.codeman.filters.ZipFilter;
import com.nexagis.codeman.gui.CodeMan;
import com.nexagis.codeman.managers.ZipManager;

/**
 * @author mike
 *
 */
public final class InstallZipAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public InstallZipAction()
  {
    super();
  }

  /**
   * @param name
   */
  public InstallZipAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(SHORT_DESCRIPTION, "Install a zip file of source code");
  }

  /**
   * @param name
   * @param icon
   */
  public InstallZipAction(String name, Icon icon)
  {
    super(name, icon);
  }
  
  
  /** (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    // Close any open zip file
    ZipManager.getInstance().closeZip();
    
    // Let the user select a zip file to open
    JFileChooser fc = new JFileChooser(".");
    fc.setDialogTitle("Install Zip");
    
    FileFilter zipFilter = new ZipFilter();
    fc.addChoosableFileFilter(zipFilter);
    fc.setFileFilter(zipFilter);
    
    // Install the selected file
    int rc = fc.showOpenDialog(CodeMan.getApp().frame);
    if (rc == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      // System.out.println("Opening file " + file.getAbsolutePath());
      ZipManager.getInstance().install(file.getAbsolutePath(), true);
    }
  }
}
