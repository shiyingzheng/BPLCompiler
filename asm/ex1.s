.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

fact:
	movq %rsp, %rbx
	movq 16(%rbx), %rax 	# variable n
	push %rax 	# comparison
	movq $0, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	je .Label0
	movl $0, %eax
	jmp .Label1
.Label0:
	movl $1, %eax
.Label1:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je .Label2
	movq %rbx, %rsp 	# return statement
	movq $1, %rax 	# evaluate number
	ret
.Label2:
	movq %rbx, %rsp 	# return statement
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call fact 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	push %rax 	# multiplication here
	movq 16(%rbx), %rax 	# variable n
	imul 0(%rsp) , %eax
	addq $8, %rsp
	ret
	ret

main:
	movq %rsp, %rbx
	subq $8, %rsp 	# Allocate space for local variables
	movq $1, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable x
.Label3:
	movq -8(%rbx), %rax 	# variable x
	push %rax 	# comparison
	movq $10, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jl .Label5
	movl $0, %eax
	jmp .Label6
.Label5:
	movl $1, %eax
.Label6:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label4
	movq -8(%rbx), %rax 	# variable x
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call fact 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# variable x
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable x
	jmp .Label3
.Label4:
	addq $8, %rsp 	# Deallocate space for local variables
	ret
