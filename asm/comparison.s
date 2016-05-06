.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.text
.globl main

main: 
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
	cmpl %eax, 0(%rsp)
	jle Label0
	movl $0, %eax
	jmp Label1
	Label0:
	movl $1, %eax
	Label1:
	addq $8, %rsp

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
	cmpl %eax, 0(%rsp)
	jge Label2
	movl $0, %eax
	jmp Label3
	Label2:
	movl $1, %eax
	Label3:
	addq $8, %rsp

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
	cmpl %eax, 0(%rsp)
	jl Label4
	movl $0, %eax
	jmp Label5
	Label4:
	movl $1, %eax
	Label5:
	addq $8, %rsp

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
	cmpl %eax, 0(%rsp)
	je Label6
	movl $0, %eax
	jmp Label7
	Label6:
	movl $1, %eax
	Label7:
	addq $8, %rsp

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
	cmpl %eax, 0(%rsp)
	jne Label8
	movl $0, %eax
	jmp Label9
	Label8:
	movl $1, %eax
	Label9:
	addq $8, %rsp

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	movl $5, %eax
	push %rax 	# comparison
	movl $4, %eax
	cmpl %eax, 0(%rsp)
	jg Label10
	movl $0, %eax
	jmp Label11
	Label10:
	movl $1, %eax
	Label11:
	addq $8, %rsp

	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
	ret
