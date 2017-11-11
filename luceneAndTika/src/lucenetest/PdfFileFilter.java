package lucenetest;

import java.io.File;
import java.io.FileFilter;

public class PdfFileFilter implements FileFilter {
   public boolean accept(File pathname) {
      return pathname.getName().toLowerCase().endsWith(".pdf");
   }
}