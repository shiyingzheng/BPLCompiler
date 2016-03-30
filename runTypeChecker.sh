#!/bin/bash

# a shellscript to run tests.
# usage: ./runTypeChecker.sh testfile
if [ $# -lt 1 ] # if there are fewer than 1 argument
then
    echo "Please supply a file name as argument."
    echo "Usage example: ./runTypeChecker.sh testfile"
    echo "Some sample test files are in the testfiles/typeCheckerTests directory."
else
    java -cp bin/main/ BPLTypeChecker $1 # else just run on the file specified by the first argument
fi
