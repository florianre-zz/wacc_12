###Build Status

Master [![Build Status](https://magnum.travis-ci.com/egnwd/wacc_12.svg?token=yTGVwCpHZBQuhLqD9VAk&branch=master)](https://magnum.travis-ci.com/egnwd/wacc_12)

Semantic analysis [![Build Status](https://magnum.travis-ci.com/egnwd/wacc_12.svg?token=yTGVwCpHZBQuhLqD9VAk&branch=semantic-analysis)](https://magnum.travis-ci.com/egnwd/wacc_12)

----------------------------
Provided files/directories  
----------------------------

`pom.xml`

Configures Maven with project information and the required dependencies and plugins.

`.travis.yml`

Configures Travis

`config/checkstyle.xml`

Configures the Maven checkstyle

`src/main/antlr4/antlr`

The antlr directory contains the ANTLR lexer and parser specification
files WACCLexer.g4 and WACCParser.g4.

`src/main/java`

Where we expect you to write your compiler code.

`grun`

The grun script allows you to run the ANTLR TestRig program that can assist you
in debugging you lexer and parser (more details below).

`compile`

The compile script should be edited to provide a frontend interface to your WACC
compiler. You are free to change the language used in this script, but do not
change its name (more details below).

`Makefile`

The Makefile will make the appropriate calls to Maven, and should not need to be edited.

`make` (or `make all`) will generate the ANTLR source and class files and compile
all java files in the `src/main/java` directory.

`make clean` - default behaviour

`make test` - cleans, then runs all unit tests

----------------------------
Using the provided scripts
----------------------------

`grun`

**Important: You must `make` before running grun the first time!**

This script provides access to the ANTLR TestRig program. You will probably find
this helpful for testing your lexer and parser. The script is just a wrapper for
the TestRig in the project environment. You need to tell it what grammar to use
what rule to start parsing with and what kind of output you want.

For example:
  ./grun -tokens
will run the TestRig using the WACC grammar. To see how the parser groups these
tokens you can use the -tree or -gui options instead, such as:
  ./grun -gui
In either case you will need to type in your input program and then close the
input stream with ctrl-D.

Rather than typing your input programs in by hand, you can pass the TestRig a
file to read by piping it in through stdin with
  ./grun -gui < testfile
When using the TestRig in this way you won't need to hit ctrl-D to close the
input stream as the EOF character in the file does this for you.

`compile`

This script call the WACCCompile class. It can be called with the `-d` or `--debug` flag to show all of maven's output. You can call the script with an input file as an argument which will be used as the `System.in` for the WACCCompile program.
###### TODO
 - check that the file is of the format `*.wacc`

You **do not** need to add the ANTLR jar file to the classpath of your calls
to Java, as Maven handles this automatically.
