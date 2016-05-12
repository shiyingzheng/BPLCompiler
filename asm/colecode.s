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
	subq $40, %rsp 	# Allocate space for local variables
	movq $12, %rax 	# evaluate number
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $1, %rax 	# evaluate number
	push %rax 	# get array x element
	movq %rbx, %rax 	# array factor
	addq $-40, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	addq $40, %rsp 	# Deallocate space for local variables
	ret
