package io.miti.codeman.console;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

/**
 * A simple interpreter for JConsole.
 * 
 * @author mwallace
 */
public final class SimpleInterpreter implements Interpreter
{
  /** Declare the date formatter used to format the time for the prompt. */
  private static final SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss] > ");
  
  /**
   * Default constructor.
   */
  public SimpleInterpreter()
  {
    super();
  }
  
  
  /**
   * Write the banner to the text area.
   * 
   * @param text the text area control
   */
  @Override
  public void writeBanner(final JTextArea text)
  {
    text.append("Olympus Console.  Enter 'help' to see available commands.\n");
  }
  
  
  /**
   * Write the prompt to the text area.
   * 
   * @param text the text area control
   */
  @Override
  public void writePrompt(final JTextArea text)
  {
    String time = sdf.format(new Date());
    // text.append(time + " > ");
    text.append(time);
  }
  
  
  /**
   * Process a command entered by the user.
   * 
   * @param text the text area control
   * @param cmd the text command
   */
  @Override
  public void processCommand(final JTextArea text, final String cmd)
  {
    // Return on null command
    if (cmd == null)
    {
      return;
    }
    
    // Handle one command for now
    if (cmd.equals("quit"))
    {
      System.exit(0);
    }
    else if (cmd.equals("help"))
    {
      cmdHelp(text);
    }
    else if (cmd.equals("list users"))
    {
      text.append("\nNo users were found\n");
    }
    else if (cmd.equals("glorp"))
    {
      spitUp(text);
    }
    else if (cmd.equals("clear"))
    {
      text.setText("");
    }
    else
    {
      text.append("\nUnknown command: " + cmd + "\n");
    }
  }
  
  
  /**
   * Write random text.
   * 
   * @param text the text area control
   */
  private void spitUp(final JTextArea text)
  {
    StringBuilder sb = new StringBuilder(300);
    sb.append("\nlorem ipsum dolor sit amet, consectetur adipisicing elit, sed\ndo ")
      .append("eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut\n")
      .append("enim ad minim veniam, quis nostrud exercitation ullamco laboris\n")
      .append("nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in\n")
      .append("reprehenderit in voluptate velit esse cillum dolore eu fugiat\n")
      .append("pariatur. Excepteur sint occaecat cupidatat non proident, sunt\nculpa ")
      .append("qui officia deserunt mollit anim id est laborum.\n");
    
    text.append(sb.toString());
  }
  
  
  /**
   * Write the available commands.
   * 
   * @param text the text area control
   */
  private void cmdHelp(final JTextArea text)
  {
    StringBuilder sb = new StringBuilder(100);
    sb.append("\nlist users\nglorp\nhelp\nquit\nclear\n");
    text.append(sb.toString());
  }
}
