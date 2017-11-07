package testpackage;

/*
 * https://www.tutorialspoint.com/lucene/lucene_first_application.htm
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Indexer {

   private IndexWriter writer;

   public Indexer(String indexDirectoryPath) throws IOException {

      FSDirectory dir = FSDirectory.open(Paths.get(indexDirectoryPath));

      IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
      writer = new IndexWriter(dir, config);
   }

   public void close() throws CorruptIndexException, IOException {
      writer.close();
   }

   private Document getDocument(File file, int id) throws IOException {
      Document document = new Document();

      
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File(
				file.toString()));
		ParseContext pcontext = new ParseContext();

		PDFParser pdfparser = new PDFParser();
		try {
			pdfparser.parse(inputstream, handler, metadata, pcontext);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}


		document.add(new TextField("text", handler.toString(), Field.Store.YES));

		document.add(new TextField(LuceneConstants.FILE_NAME, file.getName(),
				Field.Store.YES));
		document.add(new TextField(LuceneConstants.FILE_PATH, file
				.getCanonicalPath(), Field.Store.YES));

	      
      return document;
      

   }   

   private void indexFile(File file, int index) throws IOException {
      System.out.println("Indexing "+file.getCanonicalPath());
      Document document = getDocument(file, index);
      writer.addDocument(document);
   }

   public int createIndex(String dataDirPath, FileFilter filter) 
      throws IOException {
      //get all files in the data directory
      File[] files = new File(dataDirPath).listFiles();

      int index = 0;
      for (File file : files) {
         if(!file.isDirectory()
            && !file.isHidden()
            && file.exists()
            && file.canRead()
            && filter.accept(file)
         ){
            indexFile(file, index);
            index++;
         }
      }
      return writer.numDocs();
   }
}