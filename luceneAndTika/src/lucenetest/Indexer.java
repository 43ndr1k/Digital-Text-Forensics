package lucenetest;
// please add docear to dependencies!
// https://github.com/Docear/PDF-Inspector.git

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang.WordUtils;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.docear.pdf.PdfDataExtractor;
import org.xml.sax.SAXException;

import com.google.common.base.Objects;

public class Indexer {

   private IndexWriter writer;
	static int MIN_TITLE_LENGTH = 5;
	// could also be used for author.
	static String NO_TITLE = "No proper data found.";
	static String BLOCK_WORDS[] = {".pdf",".doc",".dvi","title","Chapter","rights reserved","Article", "http"};
	//static String DATA_DIR = "/home/tobias/Dokumente/authorship-material-stathis/";
   
   public Indexer(String indexDirectoryPath) throws IOException {

      FSDirectory dir = FSDirectory.open(Paths.get(indexDirectoryPath));

      IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
      writer = new IndexWriter(dir, config);
   }

   public void close() throws CorruptIndexException, IOException {
      writer.close();
   }

	/*
	 * @argument title
	 */
	private static String clean_field(String title){
		String new_title = null;
		if (title == null) {
			return NO_TITLE;
		}
		if(!title.matches("[A-Za-z0-9]+") && title.trim().length() > MIN_TITLE_LENGTH) {
		    new_title = title;
		} else {
			return NO_TITLE;
		}
		if (title.length()==0){
			return NO_TITLE;
		}
		for (String word : BLOCK_WORDS) {
			if (title.contains(word)) {
				return NO_TITLE;
			}
		}
		String trimmed_new_title = new_title.replaceAll("[^\\p{L}\\p{Z}]","").trim();
		if (trimmed_new_title.length()==0){
			return NO_TITLE;
		} else {
			return trimmed_new_title.replaceAll("\\s+", " ");
		}
		
	}
   

	/*
	 * @argument file
	 */
	private static String getFieldDocearStyle(File file) {
		boolean empty = true;
		StringBuilder sb = new StringBuilder();
		PdfDataExtractor extractor = new PdfDataExtractor(file);
		try {
			if (!empty) {
				sb.append("|");
			}
			try {

				String title = extractor.extractTitle();
				// also possible here.
				//System.out.println(extractor.extractPlainText());
				if (title != null) {
					sb.append(clean_field(title));
					empty = false;
				}
			}
			catch (IOException e) {
				sb.append(NO_TITLE);
			}
			catch (de.intarsys.pdf.cos.COSSwapException e){
				sb.append(NO_TITLE);
			}
		}
		finally {
			extractor.close();
			extractor = null;
		}
		return sb.toString();
	}
	
	/*
	 * @argument file
	 * @argument id
	 */
   private Document getDocumentTikaDocear(File file, int id) throws IOException {
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

		document.add(new TextField(LuceneConstants.TEXT, handler.toString(), Field.Store.YES));
		document.add(new TextField(LuceneConstants.FILE_NAME, file.getName(),
				Field.Store.YES));
		document.add(new TextField(LuceneConstants.FILE_PATH, file
				.getCanonicalPath(), Field.Store.YES));
		String author = clean_field(metadata.get("Author"));
		
		document.add(new TextField(LuceneConstants.AUTHOR, author, Field.Store.YES));
		
		String tika_title = clean_field(metadata.get("title").trim());
		String title = null;
		/*
		 * if this didn't work try docear.
		 * @todo simply return null 
		 */
		if (Objects.equal(tika_title, NO_TITLE) || (tika_title.length()==0)) {
			title = clean_field(getFieldDocearStyle(file).trim());
		} else {
			title = tika_title;
		}
		document.add(new TextField(LuceneConstants.TITLE, WordUtils.capitalize(title), Field.Store.YES));
	      
      return document;
   }   

   
	/* could also be one function, see above- but we anyway don't want to have too many packages,
	 * right?
	 * @argument file
	 * @argument id
	 */
  private Document getDocumentPdfBoxDocear(File file, int id) throws IOException {
     Document document = new Document();

     PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
		} catch(Exception e){
			e.printStackTrace();
		}  


		PDFTextStripper stripper = null;
		try {
			stripper = new PDFTextStripper();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			document.add(new TextField(LuceneConstants.TEXT, stripper.getText(doc), Field.Store.YES));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		document.add(new TextField(LuceneConstants.FILE_NAME, file.getName(),
				Field.Store.YES));
		document.add(new TextField(LuceneConstants.FILE_PATH, file
				.getCanonicalPath(), Field.Store.YES));
		
		String author = clean_field(doc.getDocumentInformation().getAuthor());
		
		document.add(new TextField(LuceneConstants.AUTHOR, author, Field.Store.YES));
		
		String pdfbox_title = clean_field(doc.getDocumentInformation().getTitle());
		String title = null;
		/*
		 * if this didn't work try docear.
		 * @todo simply return null 
		 */
		if (Objects.equal(pdfbox_title, NO_TITLE) || (pdfbox_title.length()==0)) {
			title = clean_field(getFieldDocearStyle(file).trim());
		} else {
			title = pdfbox_title;
		}
		document.add(new TextField(LuceneConstants.TITLE, WordUtils.capitalize(title), Field.Store.YES));
	      
     return document;
  } 
   
   private void indexFile(File file, int index) throws IOException {
      System.out.println("Indexing "+file.getCanonicalPath());
      //Document document = getDocumentTikaDocear(file, index);
      Document document = getDocumentPdfBoxDocear(file, index);

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