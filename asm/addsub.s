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
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	subq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $4, %rax 	# evaluate number
	subq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $4, %rax 	# evaluate number
	subq 0(%rsp), %rax
	addq $8, %rsp
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	subq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $2, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq $4, %rax 	# evaluate number
	addq 0(%rsp), %rax
	addq $8, %rsp
	push %rax 	# addition/subtraction here
	movq $5, %rax 	# evaluate number
	subq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
