.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main
.comm x, 80, 32

switch:
	movq %rsp, %rbx
	subq $8, %rsp 	# Allocate space for local variables
	movq 24(%rbx), %rax 	# variable i
	push %rax 	# comparison
	movq 32(%rbx), %rax 	# variable j
	cmpl %eax, 0(%rsp)
	jne .Label0
	movl $0, %eax
	jmp .Label1
.Label0:
	movl $1, %eax
.Label1:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je .Label2
	movq 24(%rbx), %rax 	# variable i
	imul $8, %eax
	imul $8, %eax
	addq %rbx, %rax
	addq $16, %rax
	movq %rax, -8(%rbx) 	# assign to variable temp
	movq 32(%rbx), %rax 	# variable j
	imul $8, %eax
	imul $8, %eax
	addq %rbx, %rax
	addq $16, %rax
	push %rax
	movq 24(%rbx), %rax 	# variable i
	imul $8, %eax
	addq %rbx, %rax
	addq $16, %rax
	movq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq -8(%rbx), %rax 	# variable temp
	push %rax
	movq 32(%rbx), %rax 	# variable j
	imul $8, %eax
	addq %rbx, %rax
	addq $16, %rax
	movq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
.Label2:
	addq $8, %rsp 	# Deallocate space for local variables
	ret

sort:
	movq %rsp, %rbx
	subq $24, %rsp 	# Allocate space for local variables
	movq 24(%rbx), %rax 	# variable first
	movq %rax, -8(%rbx) 	# assign to variable i
.Label3:
	movq -8(%rbx), %rax 	# variable i
	push %rax 	# comparison
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq 32(%rbx), %rax 	# variable last
	subq 0(%rsp), %rax
	addq $8, %rsp
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
	movq -16(%rbx), %rax 	# variable j
	movq %rax, -24(%rbx) 	# assign to variable small
.Label7:
	movq -16(%rbx), %rax 	# variable j
	push %rax 	# comparison
	movq 32(%rbx), %rax 	# variable last
	cmpl %eax, 0(%rsp)
	jl .Label9
	movl $0, %eax
	jmp .Label10
.Label9:
	movl $1, %eax
.Label10:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label8
	movq -16(%rbx), %rax 	# variable j
	imul $8, %eax
	imul $8, %eax
	addq %rbx, %rax
	addq $16, %rax
	push %rax 	# comparison
	movq -24(%rbx), %rax 	# variable small
	imul $8, %eax
	imul $8, %eax
	addq %rbx, %rax
	addq $16, %rax
	cmpl %eax, 0(%rsp)
	jl .Label11
	movl $0, %eax
	jmp .Label12
.Label11:
	movl $1, %eax
.Label12:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je .Label13
	movq -16(%rbx), %rax 	# variable j
	movq %rax, -24(%rbx) 	# assign to variable small
.Label13:
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -16(%rbx), %rax 	# variable j
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -16(%rbx) 	# assign to variable j
	jmp .Label7
.Label8:
	push %rax 	# int argument
	push %rax 	# int argument
	movq %rbx, %rax
	addq $16, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call switch 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $24, %rsp 	# remove args
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# variable i
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable i
	jmp .Label3
.Label4:
	addq $24, %rsp 	# Deallocate space for local variables
	ret

main:
	movq %rsp, %rbx
	subq $8, %rsp 	# Allocate space for local variables
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
.Label14:
	movq -8(%rbx), %rax 	# variable i
	push %rax 	# comparison
	movq $10, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jl .Label16
	movl $0, %eax
	jmp .Label17
.Label16:
	movl $1, %eax
.Label17:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label15
	subq $40, %rsp 	# read input
	movq %rsp, %rsi
	addq $24, %rsi
	movq $.ReadIntString, %rdi
	movl $0, %eax
	push %rbx
	call scanf
	pop %rbx
	movq 24(%rsp), %rax
	addq $40, %rsp
	push %rax
	movq -8(%rbx), %rax 	# variable i
	imul $8, %eax
	addq $x, %rax
	movq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# variable i
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable i
	jmp .Label14
.Label15:
	push %rax 	# int argument
	push %rax 	# int argument
	movq $x, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call sort 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $24, %rsp 	# remove args
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
.Label18:
	movq -8(%rbx), %rax 	# variable i
	push %rax 	# comparison
	movq $10, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jl .Label20
	movl $0, %eax
	jmp .Label21
.Label20:
	movl $1, %eax
.Label21:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label19
	movq -8(%rbx), %rax 	# variable i
	imul $8, %eax
	addq $x, %rax
	movq 0(%rax), %rax 	# assign to array elmt
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -8(%rbx), %rax 	# variable i
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -8(%rbx) 	# assign to variable i
	jmp .Label18
.Label19:
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	addq $8, %rsp 	# Deallocate space for local variables
	ret
