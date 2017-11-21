#!/bin/bash
OLD="\/home\/tobias\/Dokumente"
NEW="\~\/Digital-Text-Forensics\/pdfDocs"
DPATH="/home/tobias/Dokumente/xmlOutput/*.xml"
BPATH="/home/tobias/Dokumente/xmlOutput_backup/*.xml"
TFILE="/tmp/out.tmp.$$"
[ ! -d $BPATH ] && mkdir -p $BPATH || :
for f in $DPATH
do
  if [ -f $f -a -r $f ]; then
    /bin/cp -f $f $BPATH
   sed "s/$OLD/$NEW/g" "$f" > $TFILE && mv $TFILE "$f"
  else
   echo "Error: Cannot read $f"
  fi
done
/bin/rm $TFILE
