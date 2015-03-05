package io.miti.codeman.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;

import io.miti.codeman.gui.CodeMan;
import io.miti.codeman.managers.ZipManager;

public final class OpenZipAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public OpenZipAction()
  {
    super();
  }

  /**
   * @param name
   */
  public OpenZipAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_O);
    putValue(SHORT_DESCRIPTION, "Open a directory of indexed data");
  }

  /**
   * @param name
   * @param icon
   */
  public OpenZipAction(String name, Icon icon)
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
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    // Install the selected file
    int rc = fc.showOpenDialog(CodeMan.getApp().frame);
    if (rc == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      // System.out.println("Opening file " + file.getAbsolutePath());
      ZipManager.getInstance().openDirectory(file.getAbsolutePath());
    }
  }
}
