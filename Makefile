.PHONY: build clean

build: Supercomputer.class Ferate.class Teleportare.class Magazin.class

run-p1:
	java Supercomputer

run-p2:
	java Ferate

run-p3:
	java Teleportare

run-p4:
	java Magazin




Supercomputer.class: Supercomputer.java
		javac $^

Ferate.class: Ferate.java
		javac $^

Teleportare.class: Teleportare.java
		javac $^

Magazin.class: Magazin.java
		javac $^




clean:
	rm -f *.class
