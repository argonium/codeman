package com.nexagis.codeman.domain;

import java.text.DecimalFormat;

public class SearchResult
{
  private String doc = null;
  private double score = 0.0;
  private String formattedScore = null;
  
  private static final DecimalFormat formatter;
  
  static
  {
    formatter = new DecimalFormat("0.0000");
  }
  
  /** Default constructor. */
  public SearchResult()
  {
    super();
  }
  
  
  public SearchResult(final String sDoc, final double dScore)
  {
    doc = sDoc;
    score = dScore;
    formattedScore = formatter.format(score);
  }
  
  
  public String getDoc()
  {
    return doc;
  }
  
  
  public double getScore()
  {
    return score;
  }
  
  
  public String getFormattedScore()
  {
    return formattedScore;
  }
}
