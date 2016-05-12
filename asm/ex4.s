.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

Print:
	movq %rsp, %rbx
	subq $8, %rsp 	# Allocate space for local variables
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
.Label0:
	movq -8(%rbx), %rax 	# local variable i
	push %rax 	# comparison
	movq 24(%rbx), %rax 	# param n
	cmpl %eax, 0(%rsp)
	jl .Label2
	movl $0, %eax
	jmp .Label3
.Label2:
	movl $1, %eax
.Label3:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label1
	movq -8(%rbx), %rax 	# local variable i
	push %rax 	# get array A element
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# local variable i
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable i
	jmp .Label0
.Label1:
	addq $8, %rsp 	# Deallocate space for local variables
	ret

Sort:
	movq %rsp, %rbx
	subq $32, %rsp 	# Allocate space for local variables
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
.Label4:
	movq -8(%rbx), %rax 	# local variable i
	push %rax 	# comparison
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq 24(%rbx), %rax 	# param n
	subq 0(%rsp), %rax
	addq $8, %rsp
	cmpl %eax, 0(%rsp)
	jl .Label6
	movl $0, %eax
	jmp .Label7
.Label6:
	movl $1, %eax
.Label7:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label5
	movq -8(%rbx), %rax 	# local variable i
	movq %rax, -16(%rbx) 	# assign to variable small
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# local variable i
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -24(%rbx) 	# assign to variable j
.Label8:
	movq -24(%rbx), %rax 	# local variable j
	push %rax 	# comparison
	movq 24(%rbx), %rax 	# param n
	cmpl %eax, 0(%rsp)
	jl .Label10
	movl $0, %eax
	jmp .Label11
.Label10:
	movl $1, %eax
.Label11:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label9
	movq -24(%rbx), %rax 	# local variable j
	push %rax 	# get array A element
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	push %rax 	# comparison
	movq -16(%rbx), %rax 	# local variable small
	push %rax 	# get array A element
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	cmpl %eax, 0(%rsp)
	jl .Label12
	movl $0, %eax
	jmp .Label13
.Label12:
	movl $1, %eax
.Label13:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je .Label14
	movq -24(%rbx), %rax 	# local variable j
	movq %rax, -16(%rbx) 	# assign to variable small
.Label14:
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -24(%rbx), %rax 	# local variable j
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -24(%rbx) 	# assign to variable j
	jmp .Label8
.Label9:
	movq -8(%rbx), %rax 	# local variable i
	push %rax 	# get array A element
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rax, -32(%rbx) 	# assign to variable t
	movq -16(%rbx), %rax 	# local variable small
	push %rax 	# get array A element
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	push %rax
	movq -8(%rbx), %rax 	# local variable i
	push %rax
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq -32(%rbx), %rax 	# local variable t
	push %rax
	movq -16(%rbx), %rax 	# local variable small
	push %rax
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# local variable i
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable i
	jmp .Label4
.Label5:
	addq $32, %rsp 	# Deallocate space for local variables
	ret

main:
	movq %rsp, %rbx
	subq $800, %rsp 	# Allocate space for local variables
	movq $23, %rax 	# evaluate number
	push %rax
	movq $0, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $14, %rax 	# evaluate number
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $7, %rax 	# evaluate number
	push %rax
	movq $2, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $19, %rax 	# evaluate number
	push %rax
	movq $3, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $29, %rax 	# evaluate number
	push %rax
	movq $4, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $12, %rax 	# evaluate number
	push %rax
	movq $5, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $6, %rax 	# evaluate number
	push %rax 	# int argument
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call Sort 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $16, %rsp 	# remove args
	movq $6, %rax 	# evaluate number
	push %rax 	# int argument
	movq %rbx, %rax 	# array factor
	addq $-800, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call Print 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $16, %rsp 	# remove args
	addq $800, %rsp 	# Deallocate space for local variables
	ret
