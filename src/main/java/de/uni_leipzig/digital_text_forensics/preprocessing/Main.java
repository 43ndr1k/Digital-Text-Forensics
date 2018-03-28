package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.File;
import java.io.IOException;

public class Main {

	
		public static void main(String[] args) {
			
			ConvertPdfXMLController xmlController = new ConvertPdfXMLController(); 
			
			
			ConvertPdfXML converter = new ConvertPdfXML();
			converter.setOutputPath("/home/tobias/Dokumente/Information Retrieval/test/");
			File file = new File("upload-dir/06847369.pdf");
			
			double startTime = 0;
			double estimatedTime = 0;
			
			startTime = System.currentTimeMillis();

			try {
				converter.run(file, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println(estimatedTime/1000);
			
			startTime = System.currentTimeMillis();
			File xmlFile = new File("/home/tobias/Dokumente/Information Retrieval/test/06847369.xml");
			HeuristicTitleSearch hts = new HeuristicTitleSearch();
			hts.runOnFile("/home/tobias/Dokumente/Information Retrieval/test/06847369.xml");
			estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println(estimatedTime/1000);
				
			
			//			try {
			//				xmlController.runPreproccessing(false); // param: skip existing
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			}
			
			//			try {
			//				Runtime.getRuntime().exec("perl D:\\PerlProgrammierung\\6UnitedComparer.pl");
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			} 
			
			//			xmlController.mergeRefCountsToXMLFiles(new File("src/main/resources/preprocessing/refCount.xml"));
			//			xmlController.repairXML();
			//		HeuristicTitleSearch hts = new HeuristicTitleSearch();
			//		hts.run();

			
			System.out.println("ready");

		}

	


  }