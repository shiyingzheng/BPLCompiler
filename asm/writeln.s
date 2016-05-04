.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

main: 
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	ret
