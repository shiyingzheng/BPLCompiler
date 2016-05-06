.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

main:
	movl $1, %eax
	push %rax 	# addition/subtraction here
	movl $5, %eax
	addq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $1, %eax
	push %rax 	# addition/subtraction here
	movl $5, %eax
	subq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# addition/subtraction here
	movl $4, %eax
	subq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# addition/subtraction here
	movl $4, %eax
	subq 0(%rsp), %rax
	addq $8, %rsp
	push %rax 	# addition/subtraction here
	movl $5, %eax
	subq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# addition/subtraction here
	movl $4, %eax
	addq 0(%rsp), %rax
	addq $8, %rsp
	push %rax 	# addition/subtraction here
	movl $5, %eax
	subq 0(%rsp), %rax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
