/**
 * 
 */
package com.nexagis.codeman.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.nexagis.codeman.gui.CodeMan;

/**
 * @author mike
 *
 */
public final class ExitAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;


  /**
   * 
   */
  public ExitAction()
  {
    super();
  }

  /**
   * @param name
   */
  public ExitAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_E);
    putValue(SHORT_DESCRIPTION, "Exit the application");
  }

  /**
   * @param name
   * @param icon
   */
  public ExitAction(String name, Icon icon)
  {
    super(name, icon);
  }
  
  
  /** (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    CodeMan.getApp().exitApp();
  }
}
