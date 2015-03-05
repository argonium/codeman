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
public final class TextEditorAction extends AbstractAction
{
  /** Default serial version ID. */
  private static final long serialVersionUID = 1L;


  /**
   * Default constructor.
   */
  public TextEditorAction()
  {
    super();
  }

  /**
   * @param name
   */
  public TextEditorAction(String name)
  {
    super(name);
    putValue(MNEMONIC_KEY, KeyEvent.VK_E);
    putValue(SHORT_DESCRIPTION, "Open a new text editor");
  }

  /**
   * @param name
   * @param icon
   */
  public TextEditorAction(String name, Icon icon)
  {
    super(name, icon);
  }
  
  
  /** (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    TabViewManager.getInstance().openEditor();
  }
}
