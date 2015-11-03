.PHONY: all clean test

all:
	mvn -q compile

clean:
	mvn -q clean

test: clean
	mvn test
