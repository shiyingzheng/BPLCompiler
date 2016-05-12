.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"

.text
.global main

Print:
    movq %rsp, %rbx             # setup fp
    subq $8, %rsp               # allocate variable i
    movl $0, -8(%rbx)           # i = 0
.L0:
    movl 24(%rbx), %eax         # put n into %eax
    cmpl %eax, -8(%rbx)             # compare n, i
    jge .L1                 # if i >= n goto L1
    movl -8(%rbx), %eax         # put i into %eax
    imul $8, %eax               # compute offset in bytes
    addq 16(%rbx), %rax         # Add A to get address of A[i]
    movl 0(%rax), %esi          # second arg for call to printf
    movq $.WriteIntString, %rdi     # first arg for call to printf
    movl $0, %eax
    call printf
    movl $0, %eax
    movq $.WritelnString, %rdi  # arg for call to printf
    call printf
    movl -8(%rbx), %eax         # i into eax
    addl $1, %eax               # i+1
    movl %eax, -8(%rbx)         # i = i+1
    jmp .L0
.L1:
    addq $8, %rsp               # deallocate local variables
    ret

Sort:
    movq %rsp, %rbx             # setup fp
    subq $32, %rsp              # allocate variables i, small, j, t
    movl $0, -8(%rbx)           # i = 0
.L2:
    movl 24(%rbx), %eax         # n into %eax
    subl $1, %eax               # n-1 into %eax
    cmpl -8(%rbx), %eax         # compare i to n-1
    jle .L3                     # if n-1 <= i, goto .L3

    movl -8(%rbx), %eax         # put i into %eax
    movl %eax, -16(%rbx)            # small=i
    movl -8(%rbx), %eax         # put i into %eax
    addl $1, %eax               # put i+1 into %eax
    movl %eax, -24(%rbx)        # j = i+1
.L4:
    movl 24(%rbx), %eax         # put n into %eax
    cmpl -24(%rbx), %eax        # compare j to n
    jle .L5                     # if n <= j goto .L5
    movl -24(%rbx), %eax        # j into %eax
    imul $8, %eax               # offset in bytes
    addq 16(%rbx), %rax         # address of A[j]
    movl 0(%rax), %esi          # put A[j] into %esi
    movl -16(%rbx), %eax        # put small into %eax
    imul $8, %eax               # convert small to bytes
    addq 16(%rbx), %rax         # address of A[small]
    movl 0(%rax), %eax          # put A[small] into %eax
    cmpl %esi, %eax             # compare A[j] and A[small]
    jle .L6                 # if A[small] <= A[j] goto .L6
    movl -24(%rbx), %eax            # put j into %eax
    movl %eax, -16(%rbx)            # small = j
.L6:
    movl -24(%rbx), %eax        # put j into %eax
    addl $1, %eax               # j+1 into %eax
    movl %eax, -24(%rbx)        # j = j+1
    jmp .L4
.L5:
    movl -8(%rbx), %eax         # put i into %eax
    imul $8, %eax               # convert i to bytes
    addq 16(%rbx), %rax         # address of A[i]
    movl 0(%rax), %eax          # put A[i] into %eax
    movl %eax, -32(%rbx)            # t = A[i]
    movl -16(%rbx), %eax        # put small into %eax
    imul $8, %eax               # convert small to bytes
    addq 16(%rbx), %rax         # address of A[small]
    movl 0(%rax), %esi          # put A[small] into %esi
    movl -8(%rbx), %eax         # put i into %eax
    imul $8, %eax               # convert i to bytes
    addq 16(%rbx), %rax         # address of A[i]
    movl %esi, 0(%rax)          # A[i] = A[small]
    movl -16(%rbx), %eax        # put small into %eax
    imul $8, %eax               # convert small to bytes
    addq 16(%rbx), %rax         # address of A[small]
    movl -32(%rbx), %esi            # put t into %esi
    movl %esi, 0(%rax)          # A[small] = t
    movl -8(%rbx), %eax         # put i into %eax
    addl $1, %eax               # put i+1 into %eax
    movl %eax, -8(%rbx)         # i = i+1
    jmp .L2
.L3:
    addq $32, %rsp              # deallocate local variables
    ret

main:
    movq %rsp, %rbx             # setup fp
    subq $800, %rsp             # allocate array of 100 ints
    movq %rbx, %rax             # fp into $rax
    addq $800, %rax             # address of A into %rax
    movl $23, 0(%rax)           # A[0] = 23
    movl $14, 8(%rax)           # A[1] = 14
    movl $7, 16(%rax)           # A[2] = 7
    movl $19, 24(%rax)          # A[3] = 19
    movl $29, 32(%rax)          # A[4] = 29
    movl $12, 40(%rax)          # A[5] = 12
    push $6                     # 2nd arg for call
    push %rax                   # A: the 1st arg for call
    push %rbx                   # save fp
    call Sort
    pop %rbx                    # restore fp
    addq $16, %rsp              # pop args from stack
    push $6                     # 2nd arg for call
    movq %rbx, %rax             # fp into %rax
    addq $800, %rax             # %rax has the address of A
    push %rax                   # A: the 1st arg for call
    push %rbx                   # save fp
    call Print
    pop %rbx                    # restore fp
    addq $16, %rsp              # pop args from stack

    addq $800, %rsp             # pop local variables off stack
    ret
