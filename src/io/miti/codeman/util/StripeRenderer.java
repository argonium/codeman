package com.nexagis.codeman.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Color;

public final class StripeRenderer extends DefaultListCellRenderer
{
  private static final long serialVersionUID = 1L;

  private static final Color colorEvenRows = new Color(230, 230, 255);
  private static final Color colorOddRows  = new Color(255, 255, 255);
  private static final Color colorSelected = new Color(0, 0, 230);

  public StripeRenderer()
  {
    super();
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
                                                int index, boolean isSelected,
                                                boolean cellHasFocus)
  {
    JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                                          index, isSelected, cellHasFocus);

    // Check the conditions before setting the label's background color
    if (isSelected)
    {
      // The row is selected
      label.setBackground(colorSelected);
    }
    else if (index % 2 == 0)
    {
      // This is an even-numbered row
      label.setBackground(colorEvenRows);
    }
    else
    {
      // This is an odd-numbered row
      label.setBackground(colorOddRows);
    }

    return label;
  }
}
