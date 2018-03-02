package de.uni_leipzig.digital_text_forensics.preprocessing;

public class RefCountObj {
	private String counter;
	private String fileName;
	
	public String getCounter() {
		return counter;
	}
	public void setCounter(String counter) {
		this.counter = counter;
	}
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {

		if (fileName.endsWith(".pdf")) {
			this.fileName = fileName.substring(0, fileName.length()-".pdf"
					.length())+".xml";

		} else {
		this.fileName = fileName;
		}
	}
	


}
