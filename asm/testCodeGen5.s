.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main
.comm potatoes, 40, 32
.comm k, 8, 32
.comm j, 8, 32

main:
	movq %rsp, %rbx
	subq $56, %rsp 	# Allocate space for local variables
	movq $5, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to pointer variable n
	movq $0, %rax 	# evaluate number
	movq %rax, -56(%rbx) 	# assign to variable potato
	movq $55, %rax 	# evaluate number
	movq %rax, k 	# assign to global variable k
.Label0:
	movq -56(%rbx), %rax 	# local variable potato
	push %rax 	# comparison
	movq $5, %rax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jl .Label2
	movl $0, %eax
	jmp .Label3
.Label2:
	movl $1, %eax
.Label3:
	addq $8, %rsp
	cmpl $0, %eax 	# while statement
	je .Label1
	movq -56(%rbx), %rax 	# local variable potato
	push %rax
	movq -56(%rbx), %rax 	# local variable potato
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-48, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq -56(%rbx), %rax 	# local variable potato
	push %rax 	# get array p element
	movq %rbx, %rax 	# array factor
	addq $-48, %rax
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
	movq -56(%rbx), %rax 	# local variable potato
	push %rax
	movq -56(%rbx), %rax 	# local variable potato
	push %rax
	movq $potatoes, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $1, %rax 	# evaluate number
	push %rax 	# addition/subtraction here
	movq -56(%rbx), %rax 	# local variable potato
	addq 0(%rsp), %rax
	addq $8, %rsp
	movq %rax, -56(%rbx) 	# assign to variable potato
	jmp .Label0
.Label1:
	movq $3, %rax 	# evaluate number
	push %rax 	# get array p element
	movq %rbx, %rax 	# array factor
	addq $-48, %rax
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
	movq -8(%rbx), %rax 	# local variable n
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $0, %eax 	# writeln
	movq $.WritelnString, %rdi
	call printf
	addq $56, %rsp 	# Deallocate space for local variables
	ret
