#
#	File:		makefile
#	Author:		Anonymous
#

JC=javac
JFLAGS=-cp ".:./lib/*"
SRC_DIR=src/skip
OUTPUT_DIR=bin
DOC_DIR=doc

default: 
	$(JC) $(JFLAGS) $(SRC_DIR)/*.java -d $(OUTPUT_DIR)

doc:
	javadoc $(JFLAGS) $(SRC_DIR)/*.java -d $(DOC_DIR)

clean:
	$(RM) $(OUTPUT_DIR)/*.class
