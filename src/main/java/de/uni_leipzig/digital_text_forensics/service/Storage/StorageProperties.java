package de.uni_leipzig.digital_text_forensics.service.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    @Value("${pdfDocDir}")
    private String pdfDocs;

    @Value("${xmlFileDir}")
    private String xmlFiles;

    /**
     * Folder location for storing files
     */
    @Value("${upload-dir}")
    private String location = "upload-dir";
    private String xmlLocation = location + "/selectedMetadata";
;

	public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getXmlLocation() {
        return xmlLocation;
    }

    public void setXmlLocation(String xmlLocation) {
        this.xmlLocation = xmlLocation;
    }

    public String getPdfDocs() {
        return pdfDocs;
    }

    public void setPdfDocs(String pdfDocs) {
        this.pdfDocs = pdfDocs;
    }

    public String getXmlFiles() {
        return xmlFiles;
    }

    public void setXmlFiles(String xmlFiles) {
        this.xmlFiles = xmlFiles;
    }
}
