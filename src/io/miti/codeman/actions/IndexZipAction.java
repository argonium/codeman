package io.miti.codeman.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import io.miti.codeman.managers.ZipManager;

/**
 * @author mike
 *
 */
public final class IndexZipAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public IndexZipAction()
  {
    super();
  }

  /**
   * @param name
   */
  public IndexZipAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(SHORT_DESCRIPTION, "Update the index for a zip file");
  }

  /**
   * @param name
   * @param icon
   */
  public IndexZipAction(String name, Icon icon)
  {
    super(name, icon);
  }
  
  
  /** (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    ZipManager.getInstance().updateZipIndexEndpoints();
  }
}
