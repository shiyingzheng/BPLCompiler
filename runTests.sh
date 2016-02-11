#!/bin/bash

# a shellscript to run junit tests.
# usage: ./runTests.sh or ./runTests.sh TokenTest
if [ $# -lt 1 ] # if there are fewer than 1 argument
then
    java -cp "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/test:bin/main" org.junit.runner.JUnitCore BPLCompilerTestSuite
else
	java -cp "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/test:bin/main" org.junit.runner.JUnitCore $1 # else just run the class specified by the first argument
fi
