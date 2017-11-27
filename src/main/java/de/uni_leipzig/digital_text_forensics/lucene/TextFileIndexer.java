package de.uni_leipzig.digital_text_forensics.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TextFileIndexer {
  
  public final static String indexLocation = "LuceneIndex";
  public final static String filesForLucene = "FilesForLucene";
  private static StandardAnalyzer analyzer = new StandardAnalyzer();

  private IndexWriter writer;
  private ArrayList<File> queue = new ArrayList<File>();

  public static void main(String[] args) {
    
    try {
      start();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public static void start() throws IOException {
    TextFileIndexer indexer = null;
    try {
      indexer = new TextFileIndexer(indexLocation);
    } catch (Exception ex) {
      System.out.println("Cannot create index..." + ex.getMessage());
      System.exit(-1);
    }
    //===================================================
    //read input from user until he enters q for quit
    //===================================================
      try {
        //try to add file or folder into the index
        indexer.indexFileOrDirectory(filesForLucene);
      } catch (Exception e) {
        System.out.println("Error indexing " + " : " + e.getMessage());
      }
    //===================================================
    //after adding, we always have to call the
    //closeIndex, otherwise the index is not created    
    //===================================================
    indexer.closeIndex();
  }

  /**
   * Constructor
   * @param indexDir the name of the folder in which the index should be created
   * @throws java.io.IOException when exception creating index.
   */
  TextFileIndexer(String indexDir) throws IOException {
    // potentially overwriting any existing files there.
    FSDirectory dir = FSDirectory.open(Paths.get(indexDir));

    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    writer = new IndexWriter(dir, config);
  }

  /**
   * Indexes a file or directory
   * @param fileName the name of a text file or a folder we wish to add to the index
   * @throws java.io.IOException when exception
   */
  private void indexFileOrDirectory(String fileName) throws IOException {
    //===================================================
    //gets the list of files in a folder (if user has submitted
    //the name of a folder) or gets a single file name (is user
    //has submitted only the file name) 
    //===================================================
    addFiles(new File(fileName));
    
    int originalNumDocs = writer.numDocs();
    for (File f : queue) {
      FileReader fr = null;
      try {
        Document doc = new Document();

        //===================================================
        // add contents of file
        //===================================================
        fr = new FileReader(f);
        //doc.add(new TextField("contents", fr));
        //doc.add(new TextField("title", fr));
        String fileContent = new String(Files.readAllBytes(f.toPath()));
        doc.add(new TextField("contents", fileContent, Field.Store.YES));
        doc.add(new StringField("path", f.getPath(), Field.Store.YES));
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));

        writer.addDocument(doc);
        System.out.println("Added: " + f);
      } catch (Exception e) {
        System.out.println("Could not add: " + f);
      } finally {
        fr.close();
      }
    }
    
    int newNumDocs = writer.numDocs();
    System.out.println("");
    System.out.println("************************");
    System.out.println((newNumDocs - originalNumDocs) + " documents added.");
    System.out.println("************************");

    queue.clear();
  }

  private void addFiles(File file) {

    if (!file.exists()) {
      System.out.println(file + " does not exist.");
    }
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        addFiles(f);
      }
    } else {
      String filename = file.getName().toLowerCase();
      //===================================================
      // Only index text files
      //===================================================
      if (filename.endsWith(".htm") || filename.endsWith(".html") || 
              filename.endsWith(".xml") || filename.endsWith(".txt")) {
        queue.add(file);
      } else {
        System.out.println("Skipped " + filename);
      }
    }
  }

  /**
   * Close the index.
   * @throws java.io.IOException when exception closing
   */
  public void closeIndex() throws IOException {
    writer.close();
  }
}
