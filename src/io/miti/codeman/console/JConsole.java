package io.miti.codeman.console;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

/**
 * This class creates a Console control that lets a user type in
 * a command, get a response, and then repeat.  It mimics the
 * behavior of a DOS console window, but with mouse support.
 * Previously entered text (commands and responses) cannot be
 * edited or deleted in the console window.  A popup menu supports
 * copying selected text to the clipboard.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class JConsole extends JPanel implements KeyListener,
    MouseListener, ActionListener
{
  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The one instance of this panel.
   */
  private static JConsole panel = null;
  
  /**
   * The text control.
   */
  private JTextArea text = new JTextArea();
  
  /**
   * The history of commands.
   */
  private List<String> cmds = new ArrayList<String>(20);
  
  /**
   * The popup menu.
   */
  private JPopupMenu menu;
  
  /**
   * The index (into the text control) of the first character of
   * the current command.
   */
  private int lineStart = -1;
  
  /**
   * The index of the command into the history.
   */
  private int cmdIndex = -1;
  
  /**
   * The interpreter for the control.  It has methods for printing a banner
   * at startup, printing the prompt after a command is processed, and
   * processing a command.
   */
  private Interpreter interpreter = null;
  
  
  /**
   * Default constructor.
   */
  private JConsole()
  {
    setup();
  }
  
  
  /**
   * Constructor taking an interpreter.
   * 
   * @param pInterpreter the interpreter for the console
   */
  private JConsole(final Interpreter pInterpreter)
  {
    interpreter = pInterpreter;
    setup();
  }
  
  
  /**
   * Return an instance of this panel.
   *
   * @param pInterpreter the interpreter to use
   * @return an instance of this panel
   */
  public static JConsole getInstance(final Interpreter pInterpreter)
  {
    if (panel == null)
    {
      panel = new JConsole(pInterpreter);
      
      panel.setLayout(new java.awt.BorderLayout());
      
      // Put the text area in a scroll pane
      JScrollPane scroll = new JScrollPane(panel.text);
      scroll.getVerticalScrollBar().setUnitIncrement(40);
      scroll.getHorizontalScrollBar().setUnitIncrement(40);
      scroll.setViewportView(panel.text);
      panel.add("Center", scroll);
      
      // Show the banner
      panel.writeBanner();
    }

    // Return the panel
    return panel;
  }
  
  
  /**
   * Return the text area.
   * 
   * @return the text area of the console
   */
  public static JTextArea getTextArea()
  {
    return panel.text;
  }
  
  
  /**
   * Set the text in the text area.
   * 
   * @param msg the new text in the text area
   */
  public static void setText(final String msg)
  {
//    try
//    {
//      javax.swing.SwingUtilities.invokeAndWait(new Runnable()
//      {
//        @Override
//        public void run()
//        {
          panel.text.setText(msg);
//        }
//        
//      });
//    }
//    catch (InterruptedException e)
//    {
//      e.printStackTrace();
//    }
//    catch (InvocationTargetException e)
//    {
//      e.printStackTrace();
//    }
    
//    javax.swing.SwingUtilities.invokeAndWait(new Runnable()
//    {
//      public void run()
//      {
//        panel.text.setText(msg);
//      }
//    });
  }
  
  
  /**
   * Append to the text in the text area.
   * 
   * @param msg the text to append to the text area
   */
  public static void addText(final String msg)
  {
//    javax.swing.SwingUtilities.invokeLater(new Runnable()
//    {
//      @Override
//      public void run()
//      {
        panel.text.append(msg);
//      }
//      
//    });

//    javax.swing.SwingUtilities.invokeLater(new Runnable()
//    {
//      public void run()
//      {
//        panel.text.append(msg);
//      }
//    });
  }
  
  
  /**
   * Kill this panel instance.
   */
  public static void dropInstance()
  {
    panel = null;
  }
  
  
  /**
   * Set up the text area.
   */
  private void setup()
  {
    // Set up the font and text area
    Font font = new Font("Monospaced", Font.PLAIN, 12);
    text.setText("");
    text.setFont(font);
    text.setMargin(new Insets(7, 5, 7, 5));
    text.addKeyListener(this);
    // setViewportView(text);
    
    // Set up the popup menu
    menu = new JPopupMenu("JConsole Menu");
    menu.add(new JMenuItem("Copy")).addActionListener(this);
    
    // Make the text control respond to mouse-clicks
    text.addMouseListener(this);
  }
  
  
  /**
   * Request the cursor focus in the text area.
   */
  public static void requestCursorFocus()
  {
    if ((panel != null) && (panel.text != null))
    {
      panel.text.requestFocusInWindow();
    }
  }
  
  
  /**
   * Call the interpreter to write the banner and prompt.
   */
  private void writeBanner()
  {
    interpreter.writeBanner(text);
    interpreter.writePrompt(text);
    lineStart = text.getCaretPosition();
  }
  
  
  /**
   * Handle a pressed key.
   * 
   * @param e the key event
   */
  @Override
  public void keyPressed(final KeyEvent e)
  {
    handleKey(e);
  }
  
  
  /**
   * Handle a typed key.
   * 
   * @param e the key event
   */
  @Override
  public void keyTyped(final KeyEvent e)
  {
    handleKey(e);
  }
  
  
  /**
   * Handle keys pressed in the text area.
   * 
   * @param e the key event
   */
  private void handleKey(final KeyEvent e)
  {
    // Check if the control key was pressed with the key
    final boolean ctrlMask = (e.getModifiers() &
                               java.awt.event.ActionEvent.CTRL_MASK) != 0;
    // final boolean shiftMask = (e.getModifiers() &
    //                            java.awt.event.ActionEvent.SHIFT_MASK) != 0;
    
    // Check if there is a text selection
    final int selStart = text.getSelectionStart();
    final int selEnd = text.getSelectionEnd();
    final boolean textSelected = ((selStart >= 0) && (selStart < selEnd));
    
    // Store the key character, key code and caret position
    final char key = e.getKeyChar();
    final int code = e.getKeyCode();
    final int caret = text.getCaretPosition();
    
    // Check the key
    if (key == KeyEvent.VK_BACK_SPACE)
    {
      if (text.getCaretPosition() <= lineStart)
      {
        e.consume();
      }
      
      return;
    }
    else if (key == KeyEvent.VK_ENTER)
    {
      // Handle the Enter key
      if (e.getKeyCode() == 0)
      {
        process(getCommand());
        interpreter.writePrompt(text);
        lineStart = text.getDocument().getLength();
        text.setCaretPosition(lineStart);
      }
      else
      {
        e.consume();
      }
      cmdIndex = cmds.size();
      return;
    }
    else if (key == KeyEvent.VK_DELETE)
    {
      // Handle the Delete key
      if (textSelected)
      {
        if (selStart < lineStart)
        {
          e.consume();
        }
      }
      else if (caret < lineStart)
      {
        e.consume();
      }
      return;
    }
    else if (code == KeyEvent.VK_HOME)
    {
      // On Home, move the cursor to the start of the command
      text.setCaretPosition(lineStart);
      e.consume();
      return;
    }
    else if (code == KeyEvent.VK_END)
    {
      // On End, move the cursor to the end of the document
      text.setCaretPosition(text.getDocument().getLength());
      e.consume();
      return;
    }
    else if (key == KeyEvent.VK_TAB)
    {
      // Handle the Tab key
      e.consume();
      return;
    }
    else if ((code == KeyEvent.VK_PAGE_DOWN) || (code == KeyEvent.VK_PAGE_UP))
    {
      return;
    }
    else if ((code == KeyEvent.VK_C) && (ctrlMask))
    {
      // Handle ctrl-c (copy)
      return;
    }
    else if ((code == KeyEvent.VK_X) && (ctrlMask))
    {
      // Handle ctrl-x (cut)
      if ((textSelected) && (selStart < lineStart))
      {
        e.consume();
      }
      return;
    }
    else if ((code == KeyEvent.VK_V) && (ctrlMask))
    {
      // Handle ctrl-v (paste)
      if (textSelected)
      {
        if (selStart < lineStart)
        {
          e.consume();
        }
      }
      else
      {
        if (caret < lineStart)
        {
          e.consume();
        }
      }
      return;
    }
    else if ((key >= 32) && (key <= 126))
    {
      if (selStart < lineStart)
      {
        e.consume();
      }
      return;
    }
    else if (code == KeyEvent.VK_LEFT)
    {
      // Left arrow - if at the start of the line, consume
      if (caret == lineStart)
      {
        e.consume();
      }
      return;
    }
    else if (code == KeyEvent.VK_RIGHT)
    {
      // Right arrow - handle normally
      return;
    }
    else if (code == KeyEvent.VK_UP)
    {
      arrowUp(e, caret);
      return;
    }
    else if (code == KeyEvent.VK_DOWN)
    {
      arrowDown(e, caret);
      return;
    }
  }
  
  
  /**
   * Handle the up arrow key.
   * 
   * @param e the key event
   * @param caret the position of the caret
   */
  private void arrowUp(final KeyEvent e, final int caret)
  {
    // Up arrow - history
    if (caret >= lineStart)
    {
      // If there's no previous command, return
      if ((cmds.size() == 0) || (cmdIndex <= 0) || (cmdIndex > cmds.size()))
      {
        e.consume();
        return;
      }
      else if (e.getID() == java.awt.Event.KEY_RELEASE)
      {
        // Only handle key_press
        e.consume();
        return;
      }
      
      // Delete the current command and append the previous one in the history
      deleteText();
      --cmdIndex;
      addText(cmds.get(cmdIndex));
      
      // Consume the keystroke
      e.consume();
    }
    
    // If caret is before lineStart, then treat the key normally
  }
  
  
  /**
   * Handle the down arrow key.
   * 
   * @param e the key event
   * @param caret the position of the caret
   */
  private void arrowDown(final KeyEvent e, final int caret)
  {
    // Down arrow - history
    if (caret >= lineStart)
    {
      // If there's no next command, return
      if ((cmds.size() == 0) || (cmdIndex >= (cmds.size() - 1)))
      {
        e.consume();
        return;
      }
      else if (e.getID() == java.awt.Event.KEY_RELEASE)
      {
        // Only handle key_press
        e.consume();
        return;
      }
      
      // Delete the current command and append the next one in the history
      deleteText();
      ++cmdIndex;
      addText(cmds.get(cmdIndex));
      
      // Consume the keystroke
      e.consume();
    }
    
    // If caret is before lineStart, then treat the key normally
  }
  
  
  /**
   * Delete the text in the command area.
   */
  private void deleteText()
  {
    // Delete all text in the document, starting with lineStart
    try
    {
      text.getDocument().remove(lineStart, text.getDocument().getLength() - lineStart);
    }
    catch (BadLocationException e1)
    {
      e1.printStackTrace();
    }
  }


  /**
   * Get the command from the console.
   * 
   * @return the text command
   */
  private String getCommand()
  {
    // Get the command string, from lineStart to the end of the document
    String str = null;
    try
    {
      str = text.getText(lineStart, text.getDocument().getLength() - lineStart);
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
    return str;
  }
  
  
  /**
   * Pass the command to the interpreter for handling it.
   * 
   * @param str the command
   */
  private void process(final String str)
  {
    // Only process the command if it's not null and has some length
    if ((str != null) && (str.trim().length() > 0))
    {
      // Save the command to the history and then process it
      if ((cmdIndex >= 0) && (cmdIndex < cmds.size()) &&
          (str.equals(cmds.get(cmdIndex))))
      {
        ++cmdIndex;
      }
      else
      {
        cmds.add(str);
        cmdIndex = cmds.size();
      }
      
      // Process the command
      interpreter.processCommand(text, str.trim());
    }
    else
    {
      addText("\n");
    }
  }
  
  
  /**
   * Handle a released key.
   * 
   * @param e the key event
   */
  @Override
  public void keyReleased(final KeyEvent e)
  {
    handleKey(e);
  }
  
  
  /**
   * Handle a mouse click.
   * 
   * @param e the mouse event
   */
  @Override
  public void mouseClicked(final MouseEvent e)
  {
  }
  
  
  /**
   * Handle a mouse enter.
   * 
   * @param e the mouse event
   */
  @Override
  public void mouseEntered(final MouseEvent e)
  {
  }
  
  
  /**
   * Handle a mouse exit.
   * 
   * @param e the mouse event
   */
  @Override
  public void mouseExited(final MouseEvent e)
  {
  }
  
  
  /**
   * Handle a mouse press.
   * 
   * @param e the mouse event
   */
  @Override
  public void mousePressed(final MouseEvent e)
  {
    // Only show the menu if the right-button was pressed
    if (e.getButton() == 3)
    {
      menu.show((Component) e.getSource(), e.getX(), e.getY());
    }
  }
  
  
  /**
   * Handle a mouse release.
   * 
   * @param e the mouse event
   */
  @Override
  public void mouseReleased(final MouseEvent e)
  {
    // Only show the menu if the right-button was pressed
    if (e.getButton() == 3)
    {
      menu.show((Component) e.getSource(), e.getX(), e.getY());
    }
    text.repaint();
  }
  
  
  /**
   * Handle a performed action (used by the popup menu).
   * 
   * @param e the action event
   */
  @Override
  public void actionPerformed(final ActionEvent e)
  {
    // Perform the selected popup menu item operation
    String cmd = e.getActionCommand();
    if (cmd.equals("Copy"))
    {
      text.copy();
    }
  }
  
  
  /**
   * Return the title.
   * 
   * @return the title for this panel
   */
  public String getTitle()
  {
    return "Console";
  }
  
  
  /**
   * Return the tooltip text.
   * 
   * @return the tooltip text for this panel
   */
  @Override
  public String getToolTipText()
  {
    return "Olympus Console";
  }
}
