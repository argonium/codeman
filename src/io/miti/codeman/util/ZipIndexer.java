package io.miti.codeman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import io.miti.codeman.domain.SearchResult;

public final class ZipIndexer
{
  /** The analyzer for indexing and searching. */
  private StandardAnalyzer analyzer = null;
  
  /** Default constructor. */
  public ZipIndexer()
  {
    analyzer = new StandardAnalyzer(Version.LUCENE_42);
  }
  
  
  public void indexZipFile(final String fileName, final File indexDir)
  {
    // Open the zip file
    final File file = new File(fileName);
    if (!file.exists())
    {
      Logger.error("Input zip file was not found. Exiting.");
      return;
    }
    
    try
    {
      // Ensure the index directory exists
      indexDir.mkdirs();
      
      Directory index  = new SimpleFSDirectory(indexDir);
      
      // Create the indexer
      IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
      IndexWriter writer = new IndexWriter(index, config);
      writer.deleteAll();
      
      // Iterate over the contents of the zip file
      ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
      ZipEntry entry = null;
      while ((entry = zis.getNextEntry()) != null)
      {
        // If it's a directory, skip it
        if (entry.isDirectory())
        {
          continue;
        }
        
        // Get the contents of the file
        final String name = entry.getName();
        StringBuilder sb = new StringBuilder(1024);
        int numRead = 0;
        byte[] buf = new byte[1024];
        while ((numRead = zis.read(buf)) != -1)
        {
          sb.append(new String(buf, 0, numRead));
          java.util.Arrays.fill(buf, ((byte) 0));
        }
        String content = sb.toString();
        
        // Index the contents of zipEntry
        Document doc = new Document();
        doc.add(new TextField("title", name, Field.Store.YES));
        doc.add(new TextField("text", content, Field.Store.YES));
        writer.addDocument(doc);
      }
      
      // Close the writer
      writer.close();
      index.close();
      zis.close();
    }
    catch (ZipException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  
  public List<SearchResult> queryIndex(final String term, final File indexDir)
  {
    List<SearchResult> results = new ArrayList<SearchResult>(10);
    try
    {
      // Create the query object for searching the file contents
      final Query query = new QueryParser(Version.LUCENE_41, "text", analyzer).parse(term);
      
      // Create a search for the index
      Directory index  = new SimpleFSDirectory(indexDir);
      final DirectoryReader reader = DirectoryReader.open(index);
      final IndexSearcher searcher = new IndexSearcher(reader);
      
      // Query the collection
      final TopScoreDocCollector collector = TopScoreDocCollector.create(10000, true);
      searcher.search(query, collector);
      
      // Iterate over the search results
      final ScoreDoc[] hits = collector.topDocs().scoreDocs;
      for (ScoreDoc hit : hits)
      {
        Document doc = searcher.doc(hit.doc);
        results.add(new SearchResult(doc.get("title"), hit.score));
      }
      
      // Close the reader
      reader.close();
      index.close();
    }
    catch (ParseException e)
    {
      System.err.println("Parse exception: " + e.getMessage());
      // e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return results;
  }
  
  
  /**
   * Entry point to the application.
   * 
   * @param args arguments to the app
   */
  public static void main(final String[] args)
  {
    ZipIndexer zi = new ZipIndexer();
    zi.indexZipFile("/home/mike/Downloads/pti-src.zip", new File("tempindex"));
    System.out.println("Finished indexing.");
    zi.queryIndex("excel AND controller", new File("tempindex"));
    System.out.println("Finished searching.");
  }
}
