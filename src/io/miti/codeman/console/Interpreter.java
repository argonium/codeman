package io.miti.codeman.console;

import javax.swing.JTextArea;

/**
 * Interface for an interpreter for the console control.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface Interpreter
{
  /**
   * Write the banner to the text area.
   * 
   * @param text the text area control
   */
  void writeBanner(final JTextArea text);
  
  /**
   * Write the prompt to the text area.
   * 
   * @param text the text area control
   */
  void writePrompt(final JTextArea text);
  
  /**
   * Process a command entered by the user.
   * 
   * @param text the text area control
   * @param cmd the text command
   */
  void processCommand(final JTextArea text, final String cmd);
}
