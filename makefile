#######################
# MAKEFILE
#######################

JFLAGS = -g
JC = javac
OUTDIR = bin
.SUFFIXES: .java .class
.java.class:
	$(JC) -sourcepath src -classpath $(OUTDIR) -d $(OUTDIR) $(JFLAGS) $*.java

CLASSES = \
	src/org/uct/cs/hough/util/Circle.java \
	src/org/uct/cs/hough/util/CircleAdder.java \
	src/org/uct/cs/hough/util/Timer.java \
	src/org/uct/cs/hough/util/ImageFileFilter.java \
	src/org/uct/cs/hough/writer/ImageWriter.java \
	src/org/uct/cs/hough/display/PopUp.java \
	src/org/uct/cs/hough/reader/ShortImageBuffer.java \
	src/org/uct/cs/hough/processors/HighPassFilter.java \
	src/org/uct/cs/hough/processors/Normalizer.java \
	src/org/uct/cs/hough/processors/SobelEdgeDetector.java \
	src/org/uct/cs/hough/processors/HoughFilter.java \
	src/org/uct/cs/hough/CircleDetection.java \
	src/org/uct/cs/hough/gui/ScalingImagePanel.java \
	src/org/uct/cs/hough/GuiDriver.java \
	src/org/uct/cs/hough/CliDriver.java

######################
# RULES
######################

default: createoutdir extractlibs compileclasses compilejar

compileclasses: $(CLASSES:.java=.class)

createoutdir:
	mkdir -p $(OUTDIR)

extractlibs:
	unzip -ud $(OUTDIR) ./lib/commons-cli-1.2.jar

compilejar:
	jar cvfe CircleDetectionCli.jar org.uct.cs.hough.CliDriver -C $(OUTDIR) .
	jar cvfe CircleDetectionGui.jar org.uct.cs.hough.GuiDriver -C $(OUTDIR) .

clean:
	rm -rf $(OUTDIR)
	rm -f CircleDetectionCli.jar
	rm -f CircleDetectionGui.jar
