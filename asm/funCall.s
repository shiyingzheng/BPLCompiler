.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.String0: .string "in h"
.String1: .string "hi"
.String2: .string "bye"
.String3: .string "pewpew"
.String4: .string "meow"
.text
.globl main
.comm y, 8, 32
.comm z, 8, 32
.comm C, 80, 32

h:
	movq %rsp, %rbx
	movq $.String0, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movq $1, %rax 	# evaluate number
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
	movq $5, %rax 	# evaluate number
	push %rax 	# comparison
	movq $4, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jg .Label0
	movl $0, %eax
	jmp .Label1
.Label0:
	movl $1, %eax
.Label1:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je .Label2
	movq $.String1, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movq %rbx, %rsp 	# return statement
	ret
.Label2:
	movq $.String2, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	ret

f:
	movq %rsp, %rbx
	subq $24, %rsp 	# Allocate space for local variables
	movq %rbx, %rax 	# array factor
	addq $-24, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call h 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	movq $55, %rax 	# evaluate number
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-24, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $5, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq 16(%rbx), %rax 	# param x
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, 16(%rbx) 	# assign to variable x
	movq 16(%rbx), %rax 	# param x
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 24(%rbx), %rax 	# param y
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 32(%rbx), %rax 	# param p
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movq 16(%rbx), %rax 	# param x
	push %rax 	# addition/subtraction here
	movq $7, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 24(%rbx), %rax 	# param y
	push %rax 	# addition/subtraction here
	movq 16(%rbx), %rax 	# param x
	push %rax 	# addition/subtraction here
	movq $7, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq 24(%rbx), %rax 	# param y
	push %rax 	# multiplication here
	movq 16(%rbx), %rax 	# param x
	imul 0(%rsp) , %eax
	addq $8, %rsp
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq %rbx, %rsp 	# return statement
	movq 24(%rbx), %rax 	# param y
	push %rax 	# addition/subtraction here
	movq 16(%rbx), %rax 	# param x
	push %rax 	# addition/subtraction here
	movq $7, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	ret
	addq $24, %rsp 	# Deallocate space for local variables
	ret

main:
	movq %rsp, %rbx
	subq $64, %rsp 	# Allocate space for local variables
	movq $512, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable x
	movq $55, %rax 	# evaluate number
	movq %rax, y 	# assign to global variable y
	movq $55, %rax 	# evaluate number
	movq %rax, -16(%rbx) 	# assign to variable w
	movq $.String3, %rax 	# evaluate string
	movq %rax, z 	# assign to global variable z
	movq $.String4, %rax 	# evaluate string
	movq %rax, -64(%rbx) 	# assign to variable p
	movq $3, %rax 	# evaluate number
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-56, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $2, %rax 	# evaluate number
	push %rax
	movq $0, %rax 	# evaluate number
	push %rax
	movq $C, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $1, %rax 	# evaluate number
	push %rax 	# get array A element
	movq %rbx, %rax 	# array factor
	addq $-56, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $0, %rax 	# evaluate number
	push %rax 	# get array C element
	movq $C, %rax
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
	movq -8(%rbx), %rax 	# local variable x
	neg %eax 	# negation
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	movq y, %rax 	# global variable y
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq z, %rax 	# global variable z
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movq -64(%rbx), %rax 	# local variable p
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	movq -64(%rbx), %rax 	# local variable p
	push %rax 	# string argument
	movq y, %rax 	# global variable y
	push %rax 	# int argument
	movq -8(%rbx), %rax 	# local variable x
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $24, %rsp 	# remove args
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq -64(%rbx), %rax 	# local variable p
	push %rax 	# string argument
	movq -16(%rbx), %rax 	# local variable w
	push %rax 	# int argument
	movq -8(%rbx), %rax 	# local variable x
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $24, %rsp 	# remove args
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $55, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $512, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $7, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $9, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $8, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $3, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	imul 0(%rsp) , %eax
	addq $8, %rsp
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	addq $64, %rsp 	# Deallocate space for local variables
	ret
