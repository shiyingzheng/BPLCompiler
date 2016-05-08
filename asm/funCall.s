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
	movl $5, %eax 	# evaluate number
	push %rax 	# comparison
	movl $4, %eax 	# evaluate number
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
	push %rax 	# addition/subtraction here
	movl $7, %eax 	# evaluate number
	push %rax 	# addition/subtraction here
	movl $5, %eax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq %rbx, %rsp 	# return statement
	movq 16(%rbx), %rax 	# variable x
	push %rax 	# addition/subtraction here
	movl $7, %eax 	# evaluate number
	push %rax 	# addition/subtraction here
	movl $5, %eax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	ret
	ret

main:
	movq %rsp, %rbx
	subq $48, %rsp 	# Allocate space for local variables
	movl $512, %eax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable x
	movl $55, %eax 	# evaluate number
	movq %rax, y 	# assign to variable y
	movl $3, %eax 	# evaluate number
	movq %rax, -48(%rbx) 	# assign to variable A
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
	movq -8(%rbx), %rax 	# variable x
	push %rax 	# int argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $512, %eax 	# evaluate number
	push %rax 	# addition/subtraction here
	movl $7, %eax 	# evaluate number
	push %rax 	# addition/subtraction here
	movl $5, %eax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	addq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $9, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $8, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $3, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	addq $48, %rsp 	# Deallocate space for local variables
	ret
