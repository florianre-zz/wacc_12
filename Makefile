# Sample Makefile for the WACC Compiler lab: edit this to build your own comiler
# Locations

ANTLR_DIR	:= antlr
SOURCE_DIR	:= src
OUTPUT_DIR	:= bin
TESTS       := tests/*.java
CLASS_PATH  := lib/antlr-4.4-complete.jar

# Tools

ANTLR	:= antlrBuild
FIND	:= find
RM	:= rm -rf
MKDIR	:= mkdir -p
JAVA	:= java
JAVAC	:= javac

JFLAGS	:= -sourcepath $(SOURCE_DIR) -d $(OUTPUT_DIR) -cp $(CLASS_PATH)

# the make rules

all: rules tests

# runs the antlr build script then attempts to compile all .java files within src
rules:
	cd $(ANTLR_DIR) && ./$(ANTLR)
	$(FIND) $(SOURCE_DIR) -name '*.java' > $@
	$(MKDIR) $(OUTPUT_DIR)
	$(JAVAC) $(JFLAGS) @$@
	$(JAVAC) $(TESTS) $(SOURCE_DIR)/antlr/WACC*.java -d $(OUTPUT_DIR) -cp $(CLASS_PATH)
	$(RM) rules

# compiles all .java files in tests directory
tests:
	javac -classpath lib/antlr-4.4-complete.jar tests/*.java src/antlr/WACC*.java -d bin/

clean:
	$(RM) rules $(OUTPUT_DIR)
