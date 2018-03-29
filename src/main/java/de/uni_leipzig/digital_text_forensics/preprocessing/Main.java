package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		ConvertPdfXMLController xmlController = new ConvertPdfXMLController();

//		try {
//			xmlController.runPreproccessing(false); // param: skip existing
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		xmlController.runRefCountAnalysis();

//		 HeuristicTitleSearch hts = new HeuristicTitleSearch();
//		 hts.run();

		System.out.println("ready");

	}

}