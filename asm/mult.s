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
	movl $1, %eax
	push %rax 	# multiplication here
	movl $5, %eax
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# multiplication here
	movl $5, %eax
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# multiplication here
	movl $4, %eax
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# multiplication here
	movl $4, %eax
	imul 0(%rsp) , %eax
	addq $8, %rsp
	push %rax 	# multiplication here
	movl $5, %eax
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax
	movl %eax, %ebp 	# division here
	movl $6, %eax
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax
	movl %eax, %ebp 	# division here
	movl $5, %eax
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax
	movl %eax, %ebp 	# division here
	movl $5, %eax
	cltq
	cqto
	idivl %ebp
	movl %edx, %eax
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax
	push %rax 	# multiplication here
	movl $2, %eax
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %ebp 	# division here
	movl $16, %eax
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax
	movl %eax, %ebp 	# division here
	movl $5, %eax
	cltq
	cqto
	idivl %ebp
	movl %edx, %eax
	movl %eax, %ebp 	# division here
	movl $16, %eax
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
