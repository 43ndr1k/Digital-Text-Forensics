

package pdfboxtest; // !!! anpassen !!!
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;


// https://pdfbox.apache.org/1.8/cookbook/workingwithmetadata.html  




public class TestPdfboxClass 
{
	public static void main( String[] args )
	{
		String filename = "/home/tobias/Dokumente/authorship-material-stathis/obernowacl06.pdf";
		PDDocument doc = null;
		File file = new File(filename);
		try {
			doc = PDDocument.load(file);
		} catch(Exception e){
			e.printStackTrace();
		}  
		getMetaData(doc);     
		getText(doc);
	}
	
	
	

	/* Print Pdf-content (no encoding etc.)
	 * 
	 * @param  doc
	 */
	private static void getText(PDDocument doc) {
		PDFTextStripper stripper = null;
		try {
			stripper = new PDFTextStripper();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {

			stripper.setStartPage(0);
			stripper.setEndPage(1);
			String pdftext = stripper.getText(doc);


			int characterNumber = pdftext.length()/8;
			String pdfbeginning = pdftext.substring(0, characterNumber);
			// besser als attribut.
			MyNameGetter nameFinder = new MyNameGetter();
			nameFinder.findName(pdfbeginning);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Load MetaData
	 * @param  doc
	 */
	private static void getMetaData(PDDocument doc) {
		// metanames?
		PDDocumentInformation info = doc.getDocumentInformation();  

		System.out.println( "Page Count=" + doc.getNumberOfPages() );
		System.out.println( "Title=" + info.getTitle() );
		System.out.println( "Author=" + info.getAuthor() );
		System.out.println( "Subject=" + info.getSubject() );
		System.out.println( "Keywords=" + info.getKeywords() );
		System.out.println( "Creator=" + info.getCreator() );
		System.out.println( "Producer=" + info.getProducer() );
		System.out.println( "Creation Date=" + info.getCreationDate() );
		System.out.println( "Modification Date=" + info.getModificationDate());
		System.out.println( "Trapped=" + info.getTrapped() );     

	}



}