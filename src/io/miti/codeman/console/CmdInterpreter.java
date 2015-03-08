package io.miti.codeman.console;

import java.util.Scanner;
import javax.swing.JTextArea;

/**
 * The command interpreter for JConsole.
 * 
 * @author mwallace
 */
public final class CmdInterpreter implements Interpreter
{
  /**
   * Whether to append a newline after each row, when exporting to a string.
   */
  @SuppressWarnings("unused")
  private static final boolean appendNewline = true;
  
  /**
   * Default constructor.
   */
  public CmdInterpreter()
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
    text.append("> ");
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
    
    // Parse/scan the command
    scanCommand(text, cmd);
  }
  
  
  /**
   * Process a command entered by the user.
   * 
   * @param text the text area control
   * @param cmd the text command
   */
  private void scanCommand(final JTextArea text, final String cmd)
  {
    // Scan the command string
    Scanner scan = new Scanner(cmd);
    if (!scan.hasNext())
    {
      error(text);
    }
    else
    {
      String str = scan.next();
      if (str.equals("help"))
      {
        cmdHelp(text);
      }
      else if (str.equals("clear"))
      {
        // cmdClear(text, scan);
      }
      else if (str.equals("show"))
      {
        // cmdShow(text, scan);
      }
      else if (str.equals("reset"))
      {
        // cmdReset(text, scan);
      }
      else if (str.equals("check"))
      {
        // cmdCheck(text, scan);
      }
      else if (str.equals("sql"))
      {
        // cmdSql(text, scan);
      }
      else
      {
        text.append("\nUnknown command: " + cmd + "\n");
      }
    }
    
    scan.close();
  }
  
  
  /**
   * Write a generic error message.
   * 
   * @param text the text area control
   */
  private void error(final JTextArea text)
  {
    error(text, "\nError: Unknown command\n");
  }
  
  
  /**
   * Write a specific error message.
   * 
   * @param text the text area control
   * @param msg the error message
   */
  private void error(final JTextArea text, final String msg)
  {
    text.append(msg);
  }
  
  
  /**
   * Write the available commands.
   * 
   * @param text the text area control
   */
  private void cmdHelp(final JTextArea text)
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("\nshow missions - Show all missions")
      .append("\nshow mission - Show the current mission")
      .append("\nshow mission <id> - Show the mission with ID of 'id'")
      .append("\nshow users - show all users")
      .append("\nshow user <id> - Show the user with ID of 'id'")
      .append("\nshow dives - show all dives")
      .append("\nshow dives mission - show dives for the current mission")
      .append("\nshow dive <user-id> - show the dives for the user with ID of 'user-id'")
      .append("\nshow mines - show all mines/flags")
      .append("\nclear - clear the screen")
      .append("\nclear cache - clear all cached data from the database")
      .append("\nreset database - delete the existing database and recreate it")
      .append("\ncheck sanity - check database consistency")
      .append("\nsql <file> - load the specified SQL file")
      .append("\nhelp - print this information\n");
    text.append(sb.toString());
  }
}
