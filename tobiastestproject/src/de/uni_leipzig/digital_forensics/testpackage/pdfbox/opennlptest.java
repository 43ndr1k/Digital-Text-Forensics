package de.uni_leipzig.digital_forensics.testpackage.pdfbox;

import java.io.IOException;

import org.junit.Test;

import de.uni_leipzig.digital_forensics.testpackage.pdfbox.MyNameGetter;

public class OpenNlpTest {
	@Test
	public static void main(String[] args) throws IOException {
		//String test_paragraph = "Juola & Associates 256 W. Schwab Avenue Munhall, PA 15210 Email : pjuola@juolaassoc.com Abstract:  Traditional document analysis can fail when there is no traditional document, as in blog posts, email, or Word files.  We describe an emerging forensic discipline of stylometry, the analysis";
		String test = "Juola Lyons 	Julia Intel Labs 				Santa Clara, CA, USA 				kent.lyons@intel.com 			Abstract 				In this paper, we address the problem of op- 				timizing the style of textual content to make 				it more suitable to being listened to by a user				as opposed to being read. We study the dif-				ferences between the written style and the au-				dio style by consulting the linguistics and jour-				nalism literatures. Guided by this study, we";
		MyNameGetter nameFinder = new MyNameGetter();
		nameFinder.findName(test);
	}
}
