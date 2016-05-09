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
	ret

main:
	movq %rsp, %rbx
	subq $56, %rsp 	# Allocate space for local variables
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
	movq $.String0, %rax 	# evaluate string
	movq %rax, -16(%rbx) 	# assign to variable s
	movq -8(%rbx), %rax 	# variable i
	push %rax
	movq $2, %rax 	# evaluate number
	imul $8, %eax
	addq %rbx, %rax
	addq $-56, %rax
	movq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq -16(%rbx), %rax 	# variable s
	push %rax
	movq $1, %rax 	# evaluate number
	imul $8, %eax
	addq %rbx, %rax
	addq $-48, %rax
	movq %rax, %rdx
	pop %rax
	movq %rax, 0(%rdx) 	# assign to array elmt
	movq $5, %rax 	# evaluate number
	movq %rax, -40(%rbx) 	# assign to variable meow
	movq $.String1, %rax 	# evaluate string
	movq %rax, -48(%rbx) 	# assign to variable str
	movq -56(%rbx), %rax 	# variable A
	push %rbx 	# Push frame pointer
	call f 	# Call function
	pop %rbx 	# Retrieve frame pointer
	addq $8, %rsp 	# remove args
	addq $56, %rsp 	# Deallocate space for local variables
	ret
