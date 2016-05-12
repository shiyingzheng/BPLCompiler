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
	movq 16(%rbx), %rax 	# param x
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $23, %rax 	# evaluate number
	movq 16(%rbx), %rdx
	movq %rax, 0(%rdx) 	# assign to pointer variable x
	movq 16(%rbx), %rax 	# param x
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret

main:
	movq %rsp, %rbx
	subq $16, %rsp 	# Allocate space for local variables
	movq %rbx, %rax 	# pointer reference local var a
	addq $-8, %rax
	movq %rax, -16(%rbx) 	# assign to variable b
	movq $4, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable a
	movq $5, %rax 	# evaluate number
	movq -16(%rbx), %rax 	# local variable b
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq -16(%rbx), %rax 	# local variable b
	push %rax 	# pointer argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	movq -8(%rbx), %rax 	# local variable a
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	addq $16, %rsp 	# Deallocate space for local variables
	ret
