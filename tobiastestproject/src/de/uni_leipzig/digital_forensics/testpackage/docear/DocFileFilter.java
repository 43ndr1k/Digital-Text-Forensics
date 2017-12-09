package de.uni_leipzig.digital_forensics.testpackage.docear;

import java.io.File;
import java.io.FileFilter;

public class DocFileFilter implements FileFilter {
   public boolean accept(File pathname) {
      return pathname.getName().toLowerCase().endsWith(".doc");
   }
}