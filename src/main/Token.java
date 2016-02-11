class Token {
    public static final int T_ID = 0;
    public static final int T_NUM = 1;
    public static final int T_INT = 2; //keyword int
    public static final int T_VOID = 3; //keyword void
    public static final int T_KWSTRING = 4; //keyword string
    public static final int T_IF = 5; //keyword if
    public static final int T_ELSE = 6; //keyword else
    public static final int T_WHILE = 7; //keyword while
    public static final int T_RETURN = 8; //keyword return
    public static final int T_WRITE = 9; //keyword write
    public static final int T_WRITELN = 10; //keyword writeln
    public static final int T_READ = 11; //keyword read
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
    public static final int T_GREQ = 24; // >=
    public static final int T_GR = 25; // >
    public static final int T_PLUS = 26; // +
    public static final int T_MINUS = 27; // -
    public static final int T_MULT = 28; // *
    public static final int T_DIV = 29; // /
    public static final int T_PERCENT = 30; // %
    public static final int T_AMP = 31; // &
    public static final int T_EQ = 32; // =
    public static final int T_STRING = 33; //actual string

    private int kind;
    private String value;
    private int line_num;

    public Token(int kind, String value, int line_num){
        this.kind = kind;
        this.value = value;
        this.line_num = line_num;
    }

    public int getKind(){
        return kind;
    }

    public String getValue(){
        return value;
    }

    public int getLineNumber(){
        return line_num;
    }

    public String toString(){
        return "Token " + kind
               + ", string " + value
               + ", line number " + line_num;
    }
}
