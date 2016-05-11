.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

f:
	movq %rsp, %rbx
	movq $23, %rax 	# evaluate number
	movq %rax, 16(%rbx) 	# assign to variable x
	ret

main:
	movq %rsp, %rbx
	subq $16, %rsp 	# Allocate space for local variables
	movq $4, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable a
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	movq -8(%rbx), %rax 	# variable a
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	addq $16, %rsp 	# Deallocate space for local variables
	ret
