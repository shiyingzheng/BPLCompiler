.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

main: 
	# writeln
	movl $0, %eax
	movq $.WritelnString, %rdi
	call printf

