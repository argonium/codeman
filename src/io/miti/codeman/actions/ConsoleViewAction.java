/**
 * 
 */
package com.nexagis.codeman.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.nexagis.codeman.managers.TabViewManager;

/**
 * @author mike
 *
 */
public final class ConsoleViewAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;


  /**
   * Default constructor.
   */
  public ConsoleViewAction()
  {
    super();
  }

  /**
   * @param name
   */
  public ConsoleViewAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    putValue(SHORT_DESCRIPTION, "Open the console");
  }

  /**
   * @param name
   * @param icon
   */
  public ConsoleViewAction(String name, Icon icon)
  {
    super(name, icon);
  }
  
  
  /** (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    TabViewManager.getInstance().addConsole();
  }
}
