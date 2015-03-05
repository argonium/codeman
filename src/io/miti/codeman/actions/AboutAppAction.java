/**
 * 
 */
package com.nexagis.codeman.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.nexagis.codeman.gui.CodeMan;

/**
 * @author mike
 *
 */
public final class AboutAppAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;


  /**
   * 
   */
  public AboutAppAction()
  {
    super();
  }

  /**
   * @param name
   */
  public AboutAppAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    putValue(SHORT_DESCRIPTION, "About the application");
  }

  /**
   * @param name
   * @param icon
   */
  public AboutAppAction(String name, Icon icon)
  {
    super(name, icon);
  }
  
  
  /** (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    StringBuilder sb = new StringBuilder(100);
    sb.append("Code Manager v.0.1\nWritten by Mike Wallace");
    JOptionPane.showMessageDialog(CodeMan.getApp().frame, sb.toString(),
                  "About", JOptionPane.INFORMATION_MESSAGE);
  }
}
