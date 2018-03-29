# ﻿use warnings;
# use strict;

#<1>Extraktion von Quellen aus den Papers

opendir(DIR,'xmlFiles/') or die $!;                    #Öffnen Verzeichnis
my @files = readdir(DIR);                                                       #Files in Array
closedir (DIR) or die $!;

open(AUS, ">Skripte/output/Ausgabe.txt") or die $!;                                            #Öffnen Ausgabedatei

print"<1> Extraktion von Quellen aus den Papers";

foreach (@files) {                                                              #zeilenweise einlesen der Dateien und schreiben in die Ausgabedatei
    if ($_ =~ /xml$/){                                                          #Nur .xml Dokumente werden eingelesen
         my$file = $_;  
         open(FIL, 'xmlFiles'.'/'.$_) or die $!;      
         while(<FIL>) { 
 
              if($_ =~ /^\[\d{2}\]/){						#Nutzung von regulären Ausdrücken 
                     chomp($_);   
                  my$next = <FIL>;                                              #Entfernen von Zeilenumbrüchen damit die Zeilen auch hintereinander stehen und nicht untereinander
                  print AUS "$_$next  ";                                        #Extrahieren von 2 Zahlen die in [] stehen
                     }                                                  	#Nächste Zeile
 
                if($_ =~ /^\[\d\]/){                                            #1 Zahl in []        
                  chomp($_);
                  my$next = <FIL>;
                  print AUS "$_$next ";
                }
                                                                                                                                                  
                if($_ =~ /^\(\d{2}\)/){                                         #2 Zahlen in ()                                                               #   if($_ =~ /\[.*\]/){
                  chomp($_);
                  my$next =<FIL>;
                  print AUS "$_$next";
                }
                                                                                                         
                if($_ =~ /^\(\d\)/){                                            #1 Zahl in ()  
                  chomp($_);
                  my$next =<FIL>;
                  print AUS "$_$next";
                }
              # print "$_";                                                     #Sinnlose Ausgabe in der Konsole. Aber dadurch sieht man schnell, ob was passiert bei den entsprechenden Filtern.
          }									#Ausgabedatei "Ausgabe.txt" enthält nun die Quellen
    }
close(FIL);	
}
close(AUS) or die $!;

#</1>
#<2> Zeichenkettenersteller
print "<2> Zeichenkettenersteller";

open (DATEI, "Skripte/output/Ausgabe.txt") or die $!;
   my @daten = <DATEI>;
close (DATEI);

open(AUS, ">Skripte/output/Quellen.txt") or die $!;                                           
#Aufteilen der "Titel" in Zeichenketten selbst ausgewählter Länge
foreach (@daten)
{
my ( $chunksize, $longtextlength ) = ( 30, length($_) );	#Hier kann die Länge der Zeichenkette festgelegt werden. Aktueller Wert: 30
my @stringChunksArray = ();

for( my $counter = 0; $counter< $longtextlength; $counter += $chunksize ) {
    push @stringChunksArray, substr( $_, $counter, $chunksize )
  }
for($i=0;$i<@stringChunksArray;$i++)
  {
   print AUS $stringChunksArray[$i]."\n";			#Ausgabe in Ausgabedatei "Quellen.txt"
  # print length($stringChunksArray[$i]) . "\n\n -=-=-=- \n\n";
  }
}
	
close(AUS) or die $!;

#</2>
#<3> Titelextraktor
#Extrahieren der Titel und Filenames aus den Dokumenten, um einen Vergleich durchführen zu können

print "<3> Titelextraktor";

opendir(DIR,'xmlFiles') or die $!;                    #Öffnen Verzeichnis
my @files = readdir(DIR);                                                       #Files in Array
closedir (DIR) or die $!;

 
open(AUS, ">Skripte/output/TitelConf.txt") or die $!;                                            #Öffnen Ausgabedatei
print AUS "\n";

foreach (@files) {                                                              #Zeilenweise einlesen der Dateien 
    if ($_ =~ /xml$/){                                                          #Nur xml
         my$file = $_;  
         open(FIL, 'xmlFiles'.'/'.$_) or die $!;      
         while(<FIL>) { 
           $daten = $daten.$_;
           
           if($_ =~ /(?<=<fileName>)(.*?)(?=<\/fileName>)/g){       		#regex auf filename und title
		chomp ($_);
		print AUS "$_";   
		# print "$_";                                                                   
              }
             if($_ =~ /(?<=<title>)(.*?)(?=<\/title>)/g){       
		print AUS "$_\n";   
		# print "$_\n";                                                                   
              }
               									#Ausgabe in "TitelConf.txt"
                                                                       
         }
         if($daten !~  /(?<=<title>)(.*?)(?=<\/title>)/g){
               print AUS "\n\n\n";
               # print "\n\n\n";
               } 
         if($daten !~  /(?<=<fileName>)(.*?)(?=<\/fileName>)/g){
               print AUS "\n\n\n";
               # print "\n\n\n";						
               }
               $daten = "NULL";
     }
close(FIL);	
}
close(AUS) or die $!;
#</3>
#<4> Titelvergleicher
print "<4> Titelvergleicher";

open (QUELLEN,"<", "Skripte/output/Quellen.txt") or die $!;        #die zerhackstückelten Quellen
   my @quellen = <QUELLEN>;
close (QUELLEN);
open (DATEI2, "<","Skripte/output/TitelConf.txt") or die $!;       #Eine Liste mit den tatsächlichen Titeln
   my @titel = <DATEI2>;
close (DATEI2);
open(AUS, ">Skripte/output/Counter.txt") or die $!; 

#Abgleich der Quellen und Titel
#Ausgabe in "Counter.txt"

OUTER: foreach my $i (@quellen) {
  chomp(@quellen);
  foreach my $j (@titel) {
    if ($j =~ /\Q$i/) {
      print AUS "$j \n";
         # print "$j\n";
      next OUTER;
    }
  }
}
 #Die Datei TitelConf muss mit einer Leerzeile anfangen!
close(AUS) or die $!;

#</4>

#<5> Titelcount
print "<5> Titelcount";

#Zählen und Sortieren der Titel
#Ausgabe in "FinalOutput.txt"

open (Titel, "Skripte/output/counter.txt") or die $!;
   my @a = <Titel>;
close (Titel);

open(AUS, ">Skripte/output/final.txt") or die $!; 
chomp @a;
map {$hash{$_}++} @a;

@sortiert = map { $_->[0] }
sort { $b->[1] <=> $a->[1] }
map { ["$_ $hash{$_}", $hash{$_}] } (keys %hash) ;


foreach(@sortiert){
print AUS " $_ </counter> \n";
}

close(AUS) or die $!;

open (BER, "Skripte/output/final.txt") or die $!;
   my @a = <BER>;
close (BER);

open(AUS, ">Skripte/output/FinalOutput.txt") or die $!;		#"Müll" entfernen
shift @a;
shift @a;
print @a;
print AUS @a;
close(AUS) or die $!;

#</5>

#<6>
#finale Überarbeitung um reibungslose Weiterverarbeitung zu gewährleisten 
print "<6> Final step.";
#Ausgabe in "finall.xml"
# #entfernt unnötige Leerzeichen 
sub trim() {
  my $str = $_[0];
  $str =~ s/^\s+|\s+$//g;
  return $str;
};

#"umwandeln in xml"

open (BER, "Skripte/output/FinalOutput.txt") or die $!;
   my @a = <BER>;
close (BER);
open(AUS, ">Skripte/output/finall.xml") or die $!;                                            



foreach(@a){

 if($_ =~  /(?<=<title>)(.*?)(?=<\/title>)/g){
               $_ = &trim($_);					#Function trim
               print AUS "$_ \n";
               # print "$_ \n";
               } 
             }
             
close(AUS) or die $!;
             


open (BER, "Skripte/output/finall.xml") or die $!;

 while(<BER>) { 
           $daten = $daten.$_;
          }      
close (BER);

#Bearbeitung für finale xml Ausgabe über regex - ersetzen
#Trennen der Einträge durch <entry></entry>

$daten =~ s/(?<=<title>)(.*?)(?=<\/title>)//g;
$daten =~ s/<title>/\n  <counter>/g;
$daten =~ s/<\/title>//g;
$daten =~ s/ //g;
$daten =~ s/NULL//g;
$daten =~ s/.pdf/.xml/g;
$daten =~ s/<fileName>/<\/entry>\n \n <entry> \n  <fileName>/g;
$daten =~ s/<counter>/  <counter>/g;
$daten =~ s/ <entry>/<entry>/g;
$daten =~ s/<\/entry>/<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<entries>/;	#Header .xml
open(AUS, ">Skripte/output/finall.xml") or die $!;  
print AUS $daten;
close(AUS) or die $!;

print $daten;

open (BER, "Skripte/output/finall.xml") or die $!;
   my @a = <BER>;
close (BER);

push (@a,("</entry> \n\n</entries>"));		#Tags schließen
open(AUS, ">Skripte/output/finall.xml") or die $!;  
print AUS @a;
close(AUS) or die $!;

print @a;				#Ausgabe des finalen Ergebnisses auf Konsole

#</6>
