package de.uni_leipzig.digital_forensics.testpackage.lucene;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.metadata.Metadata;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TikaAndLuceneWriter {
	private static final String INDEX_DIR = "/home/tobias/workspace/lucenetest/indexdir";

	public static void main(String[] args) {
		String filename = "/home/tobias/Downloads/authorship-material-stathis/amasyali-and-diri-2006.pdf";
		try {
			createIndex(filename);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
	}
	private static void createIndex(String filename) throws IOException,TikaException{
		// bisschen unuebersichlich.
		

		IndexWriter writer = createWriter();
		List<Document> documents = new ArrayList<Document>();

		// brauche ich fuer jedes file einen neuen handler?
		
		// WHILE LOOPING OVER FOLDER
		
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File(filename));
		ParseContext pcontext = new ParseContext();

		PDFParser pdfparser = new PDFParser(); 
		try {
			pdfparser.parse(inputstream, handler, metadata,pcontext);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		// hier werden wir entweder nachhelfen muessen oder auf dem kompletten text suchen muessen.
		String title = metadata.get("title");
		String author = metadata.get("Author");
		String created = metadata.get("created");
		Document document1 = createDocument(1, title, author, created, handler.toString());
		documents.add(document1);

		System.out.println(title);

		// END OF WHILE
		
		//Let's clean everything first
		writer.deleteAll();

		writer.addDocuments(documents);
		writer.commit();
		writer.close();

	   }
	
	
    private static Document createDocument(Integer id, String title, String author, String date, String text)
    {
        Document document = new Document();
//        document.add(new StringField("id", id.toString() , Field.Store.YES));
//        document.add(new TextField("title", title , Field.Store.YES));
//        document.add(new TextField("author", author , Field.Store.YES));
//        document.add(new TextField("date", date , Field.Store.YES));
        document.add(new TextField("text", date , Field.Store.YES));

        return document;
    }
 
    private static IndexWriter createWriter() throws IOException
    {
        FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        return writer;
    }
	
}
