.section   .rodata
.WriteIntString: .string "%d"
.WriteStrString: .string "%s"
.WritelnString: .string "\n"
.ReadIntString: .string "%d"
.WriteHiBobString: .string "Hi Bob!"
.String0: .string "meow"
.String1: .string "ruff"
.String2: .string "huh"
.String3: .string "pewpew"
.text
.globl main

main:
	movq %rsp, %rbx
	movl $5, %eax 	# evaluate number
	push %rax 	# comparison
	movl $4, %eax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jge Label0
	movl $0, %eax
	jmp Label1
Label0:
	movl $1, %eax
Label1:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je Label2
	movl $5, %eax 	# evaluate number
	movl %eax, %esi 	# write int
	movq $.WriteIntString, %rdi
	movl $0, %eax
	call printf
Label2:
	movl $4, %eax 	# evaluate number
	push %rax 	# comparison
	movl $5, %eax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jge Label3
	movl $0, %eax
	jmp Label4
Label3:
	movl $1, %eax
Label4:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je Label5
	movq $.String0, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
Label5:
	movq $.String1, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
	movl $5, %eax 	# evaluate number
	push %rax 	# comparison
	movl $4, %eax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jge Label6
	movl $0, %eax
	jmp Label7
Label6:
	movl $1, %eax
Label7:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je Label8
	movl $5, %eax 	# evaluate number
	push %rax 	# comparison
	movl $5, %eax 	# evaluate number
	cmpl %eax, 0(%rsp)
	jne Label9
	movl $0, %eax
	jmp Label10
Label9:
	movl $1, %eax
Label10:
	addq $8, %rsp
	cmpl $0, %eax 	# if statement
	je Label11
	movq $.String2, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
Label11:
	movq $.String3, %rax 	# evaluate string
	movq %rax, %rsi 	# write string
	movq $.WriteStrString, %rdi
	movl $0, %eax
	call printf
Label8:
	ret
