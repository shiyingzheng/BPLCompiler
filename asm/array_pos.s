.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

main:
	movq %rsp, %rbx
	subq $48, %rsp 	# Allocate space for local variables
	addq $48, %rsp 	# Deallocate space for local variables
	ret
