package de.uni_leipzig.digital_text_forensics.model;

public class File {

	String text;
	String filename;

		public File() {

	}

	public File(String text) {
		this.text = text;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
