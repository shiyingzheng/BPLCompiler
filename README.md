To compile, run

        make
        make compiletest

To run the Parser on a test file named "testfile" that is placed in the root
directory of the project, you may use the runScanner.sh script:

        ./runParser.sh testfile

To run the Scanner on a test file named "testfile" that is placed in the root
directory of the project, you may use the runScanner.sh script:

        ./runScanner.sh testfile

To run the junit test suite, do

        ./runTests.sh

To run individual tests, do

        ./runTests.sh TokenTest
        ./runTests.sh BPLScannerTest
