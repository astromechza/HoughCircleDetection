#######################
# MAKEFILE
#######################

JFLAGS = -g
JC = javac
OUTDIR = out
.SUFFIXES: .java .class
.java.class:
	$(JC) -sourcepath src -classpath $(OUTDIR) -d $(OUTDIR) $(JFLAGS) $*.java

CLASSES = \
	src/org/uct/cs/hough/util/Constants.java \
	src/org/uct/cs/hough/util/IntIntPair.java \
	src/org/uct/cs/hough/util/Circumpherence.java \
	src/org/uct/cs/hough/util/Circle.java \
	src/org/uct/cs/hough/util/CircleAdder.java \
	src/org/uct/cs/hough/writer/ImageWriter.java \
	src/org/uct/cs/hough/display/PopUp.java \
	src/org/uct/cs/hough/reader/ShortImageBuffer.java \
	src/org/uct/cs/hough/processors/HighPassFilter.java \
	src/org/uct/cs/hough/processors/Normalizer.java \
	src/org/uct/cs/hough/processors/SobelEdgeDetector.java \
	src/org/uct/cs/hough/processors/HoughFilter.java \
	src/org/uct/cs/hough/CircleDetection.java \
	src/org/uct/cs/hough/CliDriver.java

######################
# RULES
######################

default: createoutdir extractlibs compileclasses compilejar

compileclasses: $(CLASSES:.java=.class)

createoutdir:
	mkdir $(OUTDIR)

extractlibs:
	unzip -ud $(OUTDIR) ./lib/commons-cli-1.2.jar

compilejar:
	jar cvfe CircleDetection.jar org.uct.cs.hough.CliDriver -C $(OUTDIR) .

clean:
	rm -rf $(OUTDIR)
