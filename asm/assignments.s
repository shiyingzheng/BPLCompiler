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
	movl $0, %eax 	# evaluate number
	movq %rax, -8(%rbx) 	# assign to variable i
	movl $2, %eax 	# evaluate number
	movq %rax, -56(%rbx) 	# assign to variable A
	movl $5, %eax 	# evaluate number
	movq %rax, -40(%rbx) 	# assign to variable meow
	addq $56, %rsp 	# Deallocate space for local variables
	ret
