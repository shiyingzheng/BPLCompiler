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
	movq $2, %rax 	# evaluate number
	movq %rax, -56(%rbx) 	# assign to variable A
	movq $5, %rax 	# evaluate number
	movq %rax, -40(%rbx) 	# assign to variable meow
	addq $56, %rsp 	# Deallocate space for local variables
	ret
