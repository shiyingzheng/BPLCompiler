To compile, run

        make
        make compiletest

To run the Code Generator on a test file named "testfile", you may use the
runCodeGen.sh script:

        ./runCodeGen.sh testfile

Or, to compile everything from testfile/codeGenTests into x86 assembly, run

        ./assemble

And then you can find the executables in /bin.


To run the Type Checker on a test file named "testfile" that is placed in the root
directory of the project, you may use the runTypeChecker.sh script:

        ./runTypeChecker.sh testfile

To run the Parser on a test file named "testfile" that is placed in the root
directory of the project, you may use the runParser.sh script:

        ./runParser.sh testfile

To run the Scanner on a test file named "testfile" that is placed in the root
directory of the project, you may use the runScanner.sh script:

        ./runScanner.sh testfile

To run the junit test suite, do

        ./runTests.sh

To run individual tests, do

        ./runTests.sh TokenTest
        ./runTests.sh BPLScannerTest
        ./runTests.sh BPLParserTest
        ./runTests.sh BPLTypeCheckerTest
