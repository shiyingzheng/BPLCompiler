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
	subq $8, %rsp 	# Allocate space for local variables
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
.Label0:
	movq -8(%rbx), %rax 	# local variable i
	push %rax 	# comparison
	movq $5, %rax 	# evaluate number
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

main:
	movq %rsp, %rbx
	subq $40, %rsp 	# Allocate space for local variables
	movq $5, %rax 	# evaluate number
	push %rax
	movq $0, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $3, %rax 	# evaluate number
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $4, %rax 	# evaluate number
	push %rax
	movq $2, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $6, %rax 	# evaluate number
	push %rax
	movq $3, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $3, %rax 	# evaluate number
	push %rax
	movq $4, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	addq $40, %rsp 	# Deallocate space for local variables
	ret
