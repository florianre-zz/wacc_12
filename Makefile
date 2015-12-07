.PHONY: all clean test

all:
	export JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-x64/ 
	mvn -q package

clean:
	mvn -q clean

test: clean
	mvn test
