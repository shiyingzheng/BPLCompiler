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
	subq $8, %rsp 	# Allocate space for local variables
	movq $1, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable n
.Label0:
	movq -8(%rbx), %rax 	# variable n
	push %rax 	# comparison
	movq $1, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	je .Label2
	movl $0, %eax
	jmp .Label3
.Label2:
	movl $1, %eax
.Label3:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label1
	movq -8(%rbx), %rax 	# variable n
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $5, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# variable n
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable n
	movq -8(%rbx), %rax 	# variable n
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	jmp .Label0
.Label1:
	movq -8(%rbx), %rax 	# variable n
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	addq $8, %rsp 	# Deallocate space for local variables
	ret
