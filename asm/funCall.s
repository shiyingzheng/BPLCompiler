.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.String0: .string "bye"
.text
.globl main
.comm y, 8, 32

h:
	movq %rsp, %rbx
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
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
	movq %rbx, %rsp
	ret
Label2:
	movq $.String0, %rax
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
	movq %rbx, %rsp
	movl $7, %eax
	push %rax 	# addition/subtraction here
	movl $5, %eax
	addq 0(%rsp), %rax
	addq $8, %rsp
	ret
	ret

main:
	movq %rsp, %rbx
	subq $8, %rsp 	# Allocate space for local variables
	movl $512, %eax
	movq %rax, -8(%rbx) 	# assign to variable x
	movl $55, %eax
	movq %rax, y 	# assign to variable y
	movq -8(%rbx), %rax 	# variable x
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq y, %rax 	# variable y
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	addq $8, %rsp 	# Deallocate space for local variables
	ret
