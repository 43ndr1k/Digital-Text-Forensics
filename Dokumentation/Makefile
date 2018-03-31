IN   := arbeit.tex
OUT  := arbeit.pdf
TEX  := $(shell ls *.tex)
BIB  := $(shell ls *.bib)
DIRS := img

all: $(TEX) $(BIB)
#	for i in $(DIRS); do make -C $$i; done
	@echo "Creating the pdf file."
	latexmk -pdf $(IN) -bibtex -interaction=nonstopmode && make clean

.PHONY: clean
clean:
	@echo "Cleaning the texfiles"
	rm -f *.aux *.log *.maf *.out *.toc *.bbl *.blg *.fls *.run.xml *.bcf *.lol *.lof *.lot *.loa *.fdb_latexmk *.tdo *.snm *.nav

pdfclean:
	make -C $(DIRS) clean 
