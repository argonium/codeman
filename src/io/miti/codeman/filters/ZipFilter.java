package io.miti.codeman.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public final class ZipFilter extends FileFilter
{
  public ZipFilter()
  {
    super();
  }

  @Override
  public boolean accept(File file)
  {
    if (file.isDirectory())
    {
      return true;
    }

    String extension = getExtension(file);
    if (extension != null)
    {
      return (extension.equals("zip"));
    }
    
    return false;
  }
  
  
  private static String getExtension(final File file)
  {
    String name = file.getName();
    int index = name.lastIndexOf('.');
    if (index == (name.length() - 1))
    {
      return null;
    }
    
    String ext = (index >= 0) ? name.substring(index + 1) : name;
    return ext.toLowerCase();
  }

  @Override
  public String getDescription()
  {
    return "Zip files";
  }
}
