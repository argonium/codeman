package com.nexagis.codeman.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public final class StyleTyper
{
  /**
   * Default constructor.
   */
  private StyleTyper()
  {
    super();
  }
  
  
  /**
   * Return the file extension for the specified file name.
   * 
   * @param fname the input file name
   * @return the file extension
   */
  private static String getFileExtension(final String fname)
  {
    // If the filename is null or empty, return an empty string
    if ((fname == null) || (fname.trim().length() == 0))
    {
      return "";
    }
    
    // Find the last period in the name; if there is no period, or
    // the period is the last character in the file name, return the
    // whole filename
    final int index = fname.lastIndexOf('.');
    if ((index < 0) || (index == (fname.length() - 1)))
    {
      return fname;
    }
    
    // Return everything after the period
    return fname.substring(index + 1);
  }
  
  
  /**
   * Add a style and its extension(s) to the map.
   * 
   * @param map the map of style to extensions
   * @param style the editor style
   * @param exts the list of extensions
   */
  private static void addToMap(final Map<String, Set<String>> map,
                               final String style,
                               final String... exts)
  {
    Set<String> set = new HashSet<String>(exts.length);
    for (String ext : exts)
    {
      set.add(ext);
    }
    
    map.put(style, set);
  }
  
  
  /**
   * Return the style for the extension.
   * 
   * @param fname the input file name
   * @return the style
   */
  public static String getTextStyle(final String fname)
  {
    // Get the file extension
    final String ext = getFileExtension(fname).toLowerCase();
    
    // Build a map of extensions for each style
    Map<String, Set<String>> map = new HashMap<String, Set<String>>(10);
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT, "as");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86, "asm");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_C, "c");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_CLOJURE, "clj");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS, "cpp");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_CSHARP, "cs");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_CSS, "css", "less");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_DELPHI, "pas");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_DTD, "dtd");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_FORTRAN, "for", "f90", "f95", "f03");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_GROOVY, "groovy", "gvy", "gy", "gsh");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_HTML, "html", "htm", "handlebar");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_JAVA, "java");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, "js");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_JSON, "json");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_JSP, "jsp");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_LATEX, "tex");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_LISP, "lisp");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_LUA, "lua");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_MAKEFILE, "mk", "make");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_MXML, "mxml");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_NONE, "story");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_NSIS, "nsis");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_PERL, "perl", "pl");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_PHP, "php");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE, "prop", "properties");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_PYTHON, "py");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_RUBY, "rb");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_SAS, "sas");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_SCALA, "scala");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_SQL, "sql");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_TCL, "tcl");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL, "sh", "bash", "ksh", "csh");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC, "vb");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH, "bat");
    addToMap(map, SyntaxConstants.SYNTAX_STYLE_XML, "xml");
    
    // Find the style for this extension
    String style = SyntaxConstants.SYNTAX_STYLE_NONE;
    for (Entry<String, Set<String>> entry : map.entrySet())
    {
      // Check if we found a match
      if (entry.getValue().contains(ext))
      {
        // The extension was found in the set, so return the style
        style = entry.getKey();
        break;
      }
    }
    
    return style;
  }
}
