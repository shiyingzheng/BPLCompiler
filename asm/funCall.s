.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.String0: .string "bye"
.String1: .string "pewpew"
.text
.globl main
.comm y, 8, 32
.comm z, 8, 32

h:
	movq %rsp, %rbx
	movq $5, %rax 	# evaluate number
	push %rax 	# comparison
	movq $4, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jge Label0
	movl $0, %eax
	jmp Label1
Label0:
	movl $1, %eax
Label1:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je Label2
	movq %rbx, %rsp 	# return statement
	ret
Label2:
	movq $.String0, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	ret

f:
	movq %rsp, %rbx
	push %rbx 	# Push frame pointer
	call h 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $0, %rsp 	# remove args
	movq 16(%rbx), %rax 	# variable x
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 24(%rbx), %rax 	# variable y
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 16(%rbx), %rax 	# variable x
	push %rax 	# addition/subtraction here
	movq $7, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 24(%rbx), %rax 	# variable y
	push %rax 	# addition/subtraction here
	movq 16(%rbx), %rax 	# variable x
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
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq 24(%rbx), %rax 	# variable y
	push %rax 	# multiplication here
	movq 16(%rbx), %rax 	# variable x
	imul 0(%rsp) , %eax
	addq $8, %rsp
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq %rbx, %rsp 	# return statement
	movq 24(%rbx), %rax 	# variable y
	push %rax 	# addition/subtraction here
	movq 16(%rbx), %rax 	# variable x
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
	ret

main:
	movq %rsp, %rbx
	subq $56, %rsp 	# Allocate space for local variables
	movq $512, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable x
	movq $55, %rax 	# evaluate number
	movq %rax, y 	# assign to variable y
	movq $55, %rax 	# evaluate number
	movq %rax, -16(%rbx) 	# assign to variable w
	movq $3, %rax 	# evaluate number
	movq %rax, -56(%rbx) 	# assign to variable A
	movq -8(%rbx), %rax 	# variable x
	neg %eax 	# negation
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq y, %rax 	# variable y
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq z, %rax 	# variable z
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movq y, %rax 	# variable y
	push %rax 	# int argument
	movq -8(%rbx), %rax 	# variable x
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $16, %rsp 	# remove args
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq -16(%rbx), %rax 	# variable w
	push %rax 	# int argument
	movq -8(%rbx), %rax 	# variable x
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $16, %rsp 	# remove args
	movl %eax, %esi 	# write int
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
	movl %eax, %esi 	# write int
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
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	addq $56, %rsp 	# Deallocate space for local variables
	ret
