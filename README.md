# Digital-Text-Forensics
## Author
- Hendrik Sawade
- created 03.2018

## Components
- Java 1.8.0_161
- Maven  3.3.9
- Database: H2
- Spring-Boot 1.5.8.RELEASE
- Apache Lucene 7.1.0
- Apache Pdfbox 2.0.7

### Initial
Download and unzip the source repository for this guide, or clone it using Git: 
```
git clone https://github.com/43ndr1k/crypto-news-docs.git
```


### Import the Project
- Create a Project from existing sources:
  - As Maven Project from the **Digital-Text-Forensics** directory.
  
### Database
The database is created on the first start and is stored in the user's home directory under directory **H2**.

### Create a new Lucene index
#### Preprocessing

**Mind:** The PDF-files are not included.


Run the **ConvertPdfXMLController** class in  package de.uni_leipzig.digital_text_forensics.preprocessing to extract the PDF content and create corresponding XML-files.  To count how many times articles are quoted, run
the _6UnitedComparer.pl_ script and merge the results using the _mergeRefCountsToXMLFiles_ method. Finally run **HeuristicTitleSearch** to correct meta-data whilst comparing the fullText-Element against a precompiled dataset. 
For more information on this read the [documentation](./Dokumentation/arbeit.pdf). 

This can be easily achieved running the **Main** class.

#### Indexing 
Run the **XMLFileIndexer** class in the package de.uni_leipzig.digital_text_forensics.lucene.  

**Precondition:** Both the XML-files (&rightarrow; **xmlFiles**) and the original PDFs (&rightarrow; **pdfDocs**)  have to be present in the corresponding folders.

_Proposal_  just move everything concerning preprocessing into one main-method.


### Email configuration
Setup the email configuration in the **resources/application.properties** file.
Set the 
* Host, 
* Port, 
* Username, 
* Passwort, 
* email from 
* email to 

from a Email Provider.

### Start the Application
Run the **Apllication** class in the package de.uni_leipzig.digital_text_forensics

### Open the search engine
The search engine can be accessed under following URL:
```
http://localhost:8080/
```
