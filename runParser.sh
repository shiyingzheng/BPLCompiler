#!/bin/bash

# a shellscript to run tests.
# usage: ./runParser.sh testfiles/parserTests/simplest_id_test
if [ $# -lt 1 ] # if there are fewer than 1 argument
then
    echo "Please supply a file name as argument."
    echo "Usage example: ./runParser.sh testfiles/parserTests/simplest_id_test"
    echo "Some sample test files are in the testfiles/parserTests directory."
else
    java -cp bin/main/ BPLParser $1 # else just run on the file specified by the first argument
fi
