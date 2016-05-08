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
	movl $1, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $5, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $5, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $4, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $4, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	push %rax 	# multiplication here
	movl $5, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax 	# evaluate number
	movl %eax, %ebp 	# division here
	movl $6, %eax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax 	# evaluate number
	movl %eax, %ebp 	# division here
	movl $5, %eax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax 	# evaluate number
	movl %eax, %ebp 	# division here
	movl $5, %eax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %edx, %eax
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $2, %eax 	# evaluate number
	push %rax 	# multiplication here
	movl $2, %eax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %ebp 	# division here
	movl $16, %eax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $3, %eax 	# evaluate number
	movl %eax, %ebp 	# division here
	movl $5, %eax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %edx, %eax
	movl %eax, %ebp 	# division here
	movl $16, %eax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
