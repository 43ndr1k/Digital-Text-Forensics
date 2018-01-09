# Erstellt ein Docker Images des Services.
FROM java:8
VOLUME /tmp
VOLUME /pdfDocs
ADD digital-text-forensics-1.0.jar app.jar
ADD LuceneIndex LuceneIndex
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]