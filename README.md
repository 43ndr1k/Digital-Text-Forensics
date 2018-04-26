# Digital-Text-Forensics
## Author
- Hendrik Sawade
- Tobis Wenzel
- David Drost
- Edward Kupfer
- created 03.2018

## Components / Requerments
- Java 1.8.0_161
- Maven  3.3.9
- Database: H2
- Spring-Boot 1.5.8.RELEASE
- Apache Lucene 7.1.0
- Apache Pdfbox 2.0.7
- Perl v5.12.3

### Initial
Download and unzip the source repository for this guide, or clone it using Git: 
```
git clone https://github.com/43ndr1k/Digital-Text-Forensics.git
```


### Import the Project
- Create a Project from existing sources:
  - As Maven Project from the **Digital-Text-Forensics** directory.
  
### Database
The database is created on the first start and is stored in the user's home directory under directory **H2**.

### Create a new Lucene index
#### Preprocessing

**Mind:** The PDF-files are not included. Please add them into &rightarrow; [pdfDocs](./pdfDocs).


Run the **Main** class in  package de.uni_leipzig.digital_text_forensics.preprocessing. This will 
- extract the PDF content and create corresponding XML-files and
 - count how many times each article is quoted with a perl-script (_refCountScript.pl_) which started from within java.
The results are merged into the existing XML-files using the _mergeRefCountsToXMLFiles_ method. 
- Finally HeuristicTitleSearch is run to correct meta-data whilst comparing the fullText-Element against a precompiled dataset. 

For more detailed information on this read the [documentation](./Dokumentation/arbeit.pdf). 


#### Indexing 
**Precondition:** Both the XML-files (&rightarrow; [xmlFiles](./xmlFiles)) and the original PDFs (&rightarrow; [pdfDocs](./pdfDocs))  have to be present in the corresponding folders.


Run the **Main** class in the package de.uni_leipzig.digital_text_forensics.lucene. This will start the  XMLFileIndexer
  class and with it the indexing.
  



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
### Login
For indexing the uploaded files you must be logged in.

Default user: admin
Default Password: admin
```
http://localhost:8080/login
```
After login you can go to:
```
http://localhost:8080/uploaded-files
```

### File Upload paths
The search engine upload can be accessed under following URL:
```
http://localhost:8080/upload

```
### Reference count indexing
For the reference counting is Perl 5 reqired. The script was tested with Perl v5.12.3.

- Linux should already have Perl installed. Check if the correct version is installed, if not install perl manually: (https://wiki.ubuntuusers.de/Perl/)
```
perl -v
sudo apt-get install perl
```
- Mac should already have Perl installed.  Check if the correct version is installed, if not not install perl manually: (http://macappstore.org/perl/)
```
perl -v
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" < /dev/null 2> /dev/null
brew install perl
```
- Windows does not have Perl preinstalled. Download and install Padre, the Perl IDE/editor (Strawberry Perl version 5.12.3 comes as part of the install). Confirm that the Installation worked by typing "perl -v" into the cmd. (https://learn.perl.org/installing/windows.html)
