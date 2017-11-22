package pdfTest.test1;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

//import org.apache.pdfbox.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

public class App 
{
    public static void main( String[] args )
    {
      //pdf2text();
      getMetaData();      
    }
    
    private static void pdf2text() {
      
        String contents = "";
        PDDocument doc = null;
        File file = new File("/Users/David/Desktop/437.pdf");
        try {
            doc = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setLineSeparator("\n");
            //stripper.setStartPage(1);
            //stripper.setEndPage(15);// this mean that it will index the first 5 pages only
            contents = stripper.getText(doc);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        //System.out.println(contents);
        
        try {
          Files.write( Paths.get("/Users/David/Desktop/TextFile.txt"), contents.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } 
        
    }
    
    private static void getMetaData() {
      
      PDDocument doc = null;
      File file = new File("/Users/David/Desktop/437.pdf");
      try {
          doc = PDDocument.load(file);
      } catch(Exception e){
        e.printStackTrace();
      }  
      // https://pdfbox.apache.org/1.8/cookbook/workingwithmetadata.html  
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
