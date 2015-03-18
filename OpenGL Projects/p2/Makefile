#
# Revised Makefile for Java OpenGL applications suitable for CS770/870
#   rdb
#   09/10/2013 Created
#
# Labeled entries:
#     all:    build + run
#     build:  compile all java files
#     run:    run the java class designated as MAIN
#     clean:  delete all class files, build, run
#     clear:  delete all class files
#
# The default MAIN variable is the name of the directory. Thus, if the
#   main class name (which should start with a capital letter is the name
#   of the directory (starting with a capital letter), do nothing.
# Else simply modify the MAIN variable to specify the name of the main
#   class to be executed
#
# This make file assumes that the correct JOGL jars are specified
#   in the current CLASSPATH
# This can be done by the cs770 shell scripts:
#      jogampCP.bash
#      jogampCP.csh
# which work as long as the appropriate script is edited
#     to refer to the location of the jogamp jar installation
#     which defaults to:
#        $HOME/cs770/jogamp
#
#---------------------------------------------------------------------

#MAIN =  $(notdir ${PWD} )
MAIN = ThreeD

# This JCFLAGS turns off annotation processing. We use it to avoid a
#   warning message from compiling in the context of the gluegen 
#   annotation processor, which was compiled for version 6 java.
JCFLAGS = -proc:none

#---------- Application info ------------------------------------------

SRCS = $(wildcard *java)

# for every .cpp input, need to produce a .o
OBJS = $(SRCS:.java=.class) 

#------------------- dependencies/actions ------------------------
# dependency: need1 need2 ...  
#         action(s)
#
all:	build run

build: compile $(MAIN)

compile: $(OBJS) 

clean: clear build run

%.class : %.java 
	javac $(JCFLAGS) $*.java

$(MAIN): $(OBJS) $(COBJS)

run: build
	java $(MAIN)

clear:
	rm -f *.class 
