class Token {
    public static final int T_ID = 0;
    public static final int T_NUM = 1;
    public static final int T_INT = 2;
    public static final int T_VOID = 3;
    public static final int T_KWSTRING = 4;
    public static final int T_IF = 5;
    public static final int T_ELSE = 6;
    public static final int T_WHILE = 7;
    public static final int T_RETURN = 8;
    public static final int T_WRITE = 9;
    public static final int T_WRITELN = 10;
    public static final int T_READ = 11;
    public static final int T_SEMI = 12; // ;
    public static final int T_COMMA = 13; // ,
    public static final int T_LBRAC = 14; // [
    public static final int T_RBRAC = 15; // ]
    public static final int T_LCURL = 16; // {
    public static final int T_RCURL = 17; // }
    public static final int T_LPAREN = 18; // (
    public static final int T_RPAREN = 19; // )
    public static final int T_LESS = 20; // <
    public static final int T_LESSEQ = 21; // <=
    public static final int T_EQEQ = 22; // ==
    public static final int T_NE = 23; // !=
    public static final int T_GREATEREQ = 24; // >=
    public static final int T_GREATER = 25; // >
    public static final int T_PLUS = 26; // +
    public static final int T_MINUS = 27; // -
    public static final int T_MULT = 28; // *
    public static final int T_DIV = 29; // /
    public static final int T_PERCENT = 30; // %
    public static final int T_AMP = 31; // &
    public static final int T_EQ = 32; // =
    public static final int T_EOF = 33;
    public static final int T_STRING = 34;

    public int kind;
    public String value;
    public int line_num;

    public Token(int kind, String value, int line_num){
        this.kind = kind;
        this.value = value;
        this.line_num = line_num;
    }

    public String toString(){
        return "Token " + kind
               + ", string " + value
               + ", line number " + line_num;
    }
}
