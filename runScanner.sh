#!/bin/bash

# a shellscript to run tests.
# usage: ./runScanner.sh testfiles/testfile
if [ $# -lt 1 ] # if there are fewer than 1 argument
then
    echo "Please supply a file name as argument."
    echo "Usage example: ./runScanner.sh testfiles/testfile"
    echo "Some sample test files are in the testfiles/ directory."
else
    java -cp bin/main/ BPLScanner $1 # else just run the class specified by the first argument
fi
