.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.String0: .string "haha"
.text
.globl main

main: 
	movl $12, %eax 	# placeholder for expression eval

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $.String0, %rax

	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf

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

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
