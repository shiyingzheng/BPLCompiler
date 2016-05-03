.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.String0: .string "meow"
.String1: .string "haha"
.text
.globl main

.comm meow, 8, 32

.comm mm, 8, 32

main: 

