#!/bin/bash
code_dir="testfiles/codeGenTests"
for file in `ls $code_dir`
do
    execname=${file%.bpl}
    ./runCodeGen.sh ${code_dir}/${file} > asm/${execname}.s
    gcc -o bin/${execname} asm/${execname}.s
done
