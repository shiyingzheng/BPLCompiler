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
	movl $12, %eax 	# placeholder for assignment eval
	movl $12, %eax 	# placeholder for assignment eval
	movl $12, %eax 	# placeholder for assignment eval
	movl $12, %eax 	# placeholder for assignment eval
	movl $12, %eax 	# placeholder for assignment eval
	movl $12, %eax 	# placeholder for assignment eval
	ret
