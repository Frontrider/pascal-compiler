import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public final class Parser {
    // TODO: Generate p_code

    enum TYPE {
        I, R, B, C
    }

    private static Token currentToken;
    private static ArrayList<Token> tokenArrayList;
    private static Iterator<Token> it;
    private static Stack<Token> tokenStack;

    private static ArrayList<Byte> p_code = new ArrayList<>();
    private static Byte ip = 0;

    public static void parse() {
        getToken(); // Get initial token

        match("TK_PROGRAM");
        match("TK_IDENTIFIER");
        match("TK_SEMI_COLON");

        program();
    }

    /*
    <pascal program> ->
	    [<program stat>]
	    <declarations>
	    <begin-statement>.
    <program stat> -> E
     */
    public static void program() {
        declarations();
        begin();
    }

    /*
    <declarations> ->
	    <var decl><declarations>
	    <label ______,,______>
	    <type ______,,______>
	    <const ______,,______>
	    <procedure ______,,______>
	    <function ______,,______>
	-> E
     */
    public static void declarations() {
        var_declarations();
        // TODO
    }


    /*
    <var decl> ->
        var[<namelist>: <type>;]^+
     */
    public static void var_declarations() {
        while(true) {
            if ("TK_VAR".equals(currentToken.getTokenType())) {
                match("TK_VAR");
            } else {
                // currentToken is not "TK_VAR"
                break;
            }

            // Store identifiers in a list
            ArrayList<Token> identifierArrayList = new ArrayList<>();

            while ("TK_IDENTIFIER".equals(currentToken.getTokenType())) {

                identifierArrayList.add(currentToken);

                match("TK_IDENTIFIER");

                if ("TK_COMMA".equals(currentToken.getTokenType())) {
                    match("TK_COMMA");
                }
            }

            match("TK_COLON");
            String dataType = currentToken.getTokenType();
            match(dataType);

            // Add the correct datatype for each identifier and insert into symbol table
            for (Token identifier : identifierArrayList) {
                identifier.setTokenType("TK_A_VAR");
                identifier.setDataType(dataType);
                if (!SymbolTable.lookup(identifier)) {
                    SymbolTable.insert(identifier);
                }
            }

            match("TK_SEMI_COLON");
        }
    }

    /*
    <begin_statement> ->
        begin <stats> end
     */
    public static void begin(){
        match("TK_BEGIN");
        statements();
        match("TK_END");
    }

    /*
    <stats> ->
	    <while stat>; <stats>
	    <repeat ...
	    <goto ...
	    <case ...
	    <if ...
	    <for ...
	    <assignment> TK_A_VAR
	    <labelling> TK_A_LABEL
	    <procedure call> TK_A_PROC
	    <write_stat>
     */
    public static void statements(){
        // TODO finish statements
        switch (currentToken.getTokenType()){
            case "TK_WRITELN":
                write_stat();
                break;
        }
    }

    public static void write_stat(){
        match("TK_WRITELN");
        match("TK_OPEN_PARENTHESIS");

//        tokenStack = new Stack<>();
//
//        while(true) {
//            tokenStack.push(currentToken);
//            // TODO call built in print
//
//            getToken();
//            if ("TK_COMMA".equals(currentToken.getTokenType())) {
//                match("TK_COMMA");
//            } else {
//                break;
//            }
//        }
//
//        // TODO pop stack??
//
//
//        match("TK_CLOSE_PARENTHESIS");
//        match("TK_SEMI_COLON");

        while (true) {
		TYPE t = E();
		switch(t){
			case I:
				generate print_int;
				break;
			case C:
				generate print_char;
				break;
			.
			.
			.

		}

		if (currToken == TK_COMMA)
			getToken();
		else if (currToken == TK_RPAREN)
			getToken(); break;
		else error();
        }
    }

    public static void void condition(){
        TYPE t= E();
        if (t != B)
            error();
    }

    public static void assignment_stat() {
        //save type, address into LHS_type, LHS_addr
        getToken();
        match("TK_ASSIGNMENT");

        TYPE RHS_type = E();

        compare LHS_type and RHS_type

        generate pop of LHS_addr
    }

    public static TYPE E(){
        TYPE t1 = T();
        while(currentToken.getTokenType().equals("TK_PLUS") || currentToken.getTokenType().equals("TK_MINUS")) {
            op = currentToken;
            match(op);
            t2 = T();

            t1 = emit(op, t1, t2);
        }

    }

    public static void gen_address(int a){
        *(int*)(code+ip) = a;
        ip+=sizeof(int);
    }

    public static void gen_opcode(char b){
        p_code.get(ip++)=b;
    }

    public static void getToken() {
        if (it.hasNext()) {
            currentToken =  it.next();
        }
    }

    public static void match(String tokenType) {
        if (!tokenType.equals(currentToken.getTokenType())) {
            throw new Error("The token does not match the current token");
        } else {
            System.out.println(String.format("matched: %s", currentToken.getTokenType()));
            getToken();
        }

    }

    public static void setTokenArrayList(ArrayList<Token> tokenArrayList) {
        Parser.tokenArrayList = tokenArrayList;
        it = tokenArrayList.iterator();
    }
}
