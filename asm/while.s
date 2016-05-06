.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

main:
Label0:
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
	cmpl $0, %eax 	# while statement
	je Label1
	movl $5, %eax
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	jmp Label0
Label1:
	ret
