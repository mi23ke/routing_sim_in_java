JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
				$(JC) $(JFLAGS) $*.java

CLASSES = \
				DistanceVector.java \
				RouterNode.java \
				Link.java

default: classes

classes: $(CLASSES:.java=.class)

%.class : %.java
			$(JC) $(JFLAGS) *.java

clean:
				$(RM) *.class
