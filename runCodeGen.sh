#!/bin/bash

# a shellscript to run the code generater.
# usage: ./runCodeGen.sh testfile
if [ $# -lt 1 ] # if there are fewer than 1 argument
then
    echo "Please supply a file name as argument."
    echo "Usage example: ./runCodeGen.sh testfile"
else
    java -cp bin/main/ BPLCodeGen $1 # else just run on the file specified by the first argument
fi
