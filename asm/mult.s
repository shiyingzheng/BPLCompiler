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
	movq $1, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $5, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $5, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $4, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $4, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	push %rax 	# multiplication here
	movq $5, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $3, %rax 	# evaluate number
	movl %eax, %ebp 	# division here
	movq $6, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $3, %rax 	# evaluate number
	movl %eax, %ebp 	# division here
	movq $5, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $3, %rax 	# evaluate number
	movl %eax, %ebp 	# division here
	movq $5, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %edx, %eax
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# multiplication here
	movq $2, %rax 	# evaluate number
	imul 0(%rsp) , %eax
	addq $8, %rsp
	movl %eax, %ebp 	# division here
	movq $16, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $3, %rax 	# evaluate number
	movl %eax, %ebp 	# division here
	movq $5, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %edx, %eax
	movl %eax, %ebp 	# division here
	movq $16, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	movl %eax, %ebp 	# division here
	movq $2, %rax 	# evaluate number
	movl %eax, %ebp 	# division here
	movq $16, %rax 	# evaluate number
	cltq
	cqto
	idivl %ebp
	cltq
	cqto
	idivl %ebp
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
