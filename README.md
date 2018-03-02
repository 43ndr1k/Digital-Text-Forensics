# Digital-Text-Forensics

## Components
- Java 1.8.0_161
- Maven  3.3.9
- Database: H2

### Initial
Download and unzip the source repository for this guide, or clone it using Git: 
```
git clone https://github.com/43ndr1k/crypto-news-docs.git
```

### Import the Project
- Create a Project from existing sources:
  - As Maven Project from the **Digital-Text-Forensics** directory.
  
### Database
The Database is created on the first start and is stored in the userhome directory under directory **H2**.

### Create a new Lucene index
Run the **TextFileIndexer** Class in the package de.uni_leipzig.digital_text_forensics.lucene 

**But** in the **xmlFile** folder the xml data must be included and in the pdfDocs folder the pdf files.

### Start the Application
Run the **Apllication** Class in the package de.uni_leipzig.digital_text_forensics

### Open the search engine
The Search Engine is ander following URL reachable:
```
http://localhost:8080/
```
