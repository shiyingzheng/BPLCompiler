.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main
.comm b, 8, 32

f:
	movq %rsp, %rbx
	movq 16(%rbx), %rax 	# param x
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 24(%rbx), %rax 	# param y
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $23, %rax 	# evaluate number
	movq 16(%rbx), %rdx
	movq %rax, 0(%rdx) 	# assign to pointer variable x
	movq $55, %rax 	# evaluate number
	movq 24(%rbx), %rdx
	movq %rax, 0(%rdx) 	# assign to pointer variable y
	movq 16(%rbx), %rax 	# param x
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq 24(%rbx), %rax 	# param y
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret

main:
	movq %rsp, %rbx
	subq $32, %rsp 	# Allocate space for local variables
	movq $1, %rax 	# evaluate number
	push %rax 	# get array A element
	movq %rbx, %rax 	# array factor
	addq $-24, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq %rax, -32(%rbx) 	# assign to variable c
	movq %rbx, %rax 	# pointer reference local var a
	addq $-8, %rax
	movq %rax, b 	# assign to global variable b
	movq $5, %rax 	# evaluate number
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-24, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $4, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable a
	movq $5, %rax 	# evaluate number
	movq b, %rax 	# global variable b
	movq 0(%rax), %rax 	# pointer dereference
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq -32(%rbx), %rax 	# local variable c
	push %rax 	# pointer argument
	movq b, %rax 	# global variable b
	push %rax 	# pointer argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $16, %rsp 	# remove args
	movq -8(%rbx), %rax 	# local variable a
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $1, %rax 	# evaluate number
	push %rax 	# get array A element
	movq %rbx, %rax 	# array factor
	addq $-24, %rax
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
	addq $32, %rsp 	# Deallocate space for local variables
	ret
