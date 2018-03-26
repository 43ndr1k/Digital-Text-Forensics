package de.uni_leipzig.digital_text_forensics.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import de.uni_leipzig.digital_text_forensics.preprocessing.Article;
import de.uni_leipzig.digital_text_forensics.preprocessing.ConvertPdfXML;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;

public class XMLFileIndexer {

	  public final static String indexLocation = "LuceneIndex";
	  public final static String filesForLucene = "xmlFiles";
	  private static StandardAnalyzer analyzer = new StandardAnalyzer();

	  private IndexWriter writer;
	  private ConvertPdfXML converter;
	  private ArrayList<File> queue = new ArrayList<File>();


	  public void start() throws IOException {
	    XMLFileIndexer indexer = null;

	    try {
	      indexer = new XMLFileIndexer(indexLocation);
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

	  public void run() throws IOException {
		    XMLFileIndexer indexer = null;

		    //===================================================
		    //read input from user until he enters q for quit
		    //===================================================
		      try {
		        indexFileOrDirectory(filesForLucene);
		      } catch (Exception e) {
		        System.out.println("Error indexing " + " : " + e.getMessage());
		      }
		    //===================================================
		    //after adding, we always have to call the
		    //closeIndex, otherwise the index is not created
		    //===================================================
		    closeIndex();
		  }

	  /**
	   * Constructor
	   * @param indexDir the name of the folder in which the index should be created
	   * @throws java.io.IOException when exception creating index.
	   */
	  XMLFileIndexer(String indexDir) throws IOException {
	    // potentially overwriting any existing files there.
	    FSDirectory dir = FSDirectory.open(Paths.get(indexDir));
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    converter = new ConvertPdfXML(); // outputPath not needed here.
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
	    addXMLFiles(new File(fileName));

	    int originalNumDocs = writer.numDocs();
	    for (File file : queue) {
	      try {
	        Document doc = new Document();
	        Article article = converter.getArticleFromXML(file);
	        //===================================================
	        // add contents of file
	        //===================================================
	        doc.add(new StringField("id", article.getDoi().toString() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.TITLE, article.getTitle() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.AUTHOR, article.getAuthorsAsString() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.PUBLICATION_DATE, article.getPublicationDate() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.CONTENTS, article.getFullText() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.FILE_NAME, article.getFileName() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.FILE_PATH, article.getFilePath() , Field.Store.YES));
	        doc.add(new TextField(LuceneConstants.REF_COUNT, article.getScore() , Field.Store.YES));

	        writer.addDocument(doc);
	        System.out.println("Added: " + file);
	      } catch (Exception e) {
	        System.out.println("Could not add: " + file);
	      }
	    }

	    int newNumDocs = writer.numDocs();
	    System.out.println("");
	    System.out.println("************************");
	    System.out.println((newNumDocs - originalNumDocs) + " documents added.");
	    System.out.println("************************");

	    queue.clear();
	  }

	  private void addXMLFiles(File file) {

	    if (!file.exists()) {
	      System.out.println(file + " does not exist.");
	    }
	    if (file.isDirectory()) {
	      for (File f : file.listFiles()) {
	        addXMLFiles(f);
	      }
	    } else {
	      String filename = file.getName().toLowerCase();
	      //===================================================
	      // Only index text files
	      //===================================================
	      if (filename.endsWith(".xml") ) {
	        queue.add(file);
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
