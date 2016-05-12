.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.String0: .string "meow"
.String1: .string "haha"
.text
.globl main
.comm meow, 8, 32
.comm mm, 8, 32

f:
	movq %rsp, %rbx
	movq $0, %rax 	# evaluate number
	push %rax 	# get array A element
	movq 16(%rbx), %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	ret

main:
	movq %rsp, %rbx
	subq $104, %rsp 	# Allocate space for local variables
	movq $98, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
	movq $.String0, %rax 	# evaluate string
	movq %rax, -16(%rbx) 	# assign to variable s
	movq -8(%rbx), %rax 	# local variable i
	push %rax
	movq $2, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-56, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq -16(%rbx), %rax 	# local variable s
	push %rax
	movq $1, %rax 	# evaluate number
	push %rax
	movq %rbx, %rax 	# array factor
	addq $-80, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $2, %rax 	# evaluate number
	push %rax 	# get array A element
	movq %rbx, %rax 	# array factor
	addq $-56, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rax, %rsi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movq $1, %rax 	# evaluate number
	push %rax 	# get array B element
	movq %rbx, %rax 	# array factor
	addq $-80, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movq $5, %rax 	# evaluate number
	movq -88(%rbx), %rdx
	movq %rax, 0(%rdx) 	# assign to pointer variable meow
	movq $.String1, %rax 	# evaluate string
	movq -96(%rbx), %rdx
	movq %rax, 0(%rdx) 	# assign to pointer variable str
	movq $0, %rax 	# evaluate number
	push %rax 	# get array A element
	movq %rbx, %rax 	# array factor
	addq $-56, %rax
	movq %rax, %rdx
	pop %rax
	imul $8, %eax
	addq %rdx, %rax
	movq 0(%rax), %rax
	movq %rbx, %rax 	# array factor
	addq $-56, %rax
	push %rax 	# array argument
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	addq $104, %rsp 	# Deallocate space for local variables
	ret
