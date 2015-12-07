.PHONY: all clean test

all:
	mvn -q package

clean:
	mvn -q clean

test: clean
	mvn test
