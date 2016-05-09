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

main:
	movq %rsp, %rbx
	subq $56, %rsp 	# Allocate space for local variables
	movq $0, %rax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
	movq $.String0, %rax 	# evaluate string
	movq %rax, -16(%rbx) 	# assign to variable s
	movq $2, %rax 	# evaluate number
	movq %rax, -56(%rbx) 	# assign to variable A
	movq -16(%rbx), %rax 	# variable s
	movq %rax, -48(%rbx) 	# assign to variable B
	movq $5, %rax 	# evaluate number
	movq %rax, -40(%rbx) 	# assign to variable meow
	movq $.String1, %rax 	# evaluate string
	movq %rax, -48(%rbx) 	# assign to variable str
	addq $56, %rsp 	# Deallocate space for local variables
	ret
