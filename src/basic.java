import java.util.ArrayList;

public class basic{

    private static ArrayList<String> tokens;
    private static String error;

    // RUN
    public static void run(String fn, String text) {

        Lexer lexer = new Lexer(fn, text);
        tokens = lexer.make_tokens();

    }

    public static ArrayList<String> getTokens() {
        return tokens;
    }

    public static String getError() {
        return error;
    }

    public static void setTokens(ArrayList<String> tokens) {
        basic.tokens = tokens;
    }

    public static void setError(String error) {
        basic.error = error;
    }

}

// LEXER
class Lexer implements Constants {
    String fn;
    String text;
    char current_char;
    Position pos;

    public Lexer(String fn, String text) {
        this.fn = fn;
        this.text = text;
        this.pos = new Position(-1, 0, -1, fn, text);
        this.current_char = '\0';
        this.advance();
    }

    void advance() {
        this.pos.advance(this.current_char);
        this.current_char = (this.pos.idx < text.length()) ? text.charAt(pos.idx) : '\0';
    }

    ArrayList<String> make_tokens() {
        ArrayList<String> token_list = new ArrayList<>();

        while (this.current_char != '\0') {
            if (current_char == ' ' || current_char == '\t') {
                this.advance();
            } else if (DIGITS.contains(String.valueOf(current_char))) {
                token_list.add(this.make_number());
            } else if (current_char == '+') {
                Token tk = new Token(TT_PLUS);
                token_list.add(tk.get());
                this.advance();
            } else if (current_char == '-') {
                Token tk = new Token(TT_MINUS);
                token_list.add(tk.get());
                this.advance();
            } else if (current_char == '*') {
                Token tk = new Token(TT_MUL);
                token_list.add(tk.get());
                this.advance();
            } else if (current_char == '/') {
                Token tk = new Token(TT_DIV);
                token_list.add(tk.get());
                this.advance();
            } else if (current_char == '(') {
                Token tk = new Token(TT_LPAREN);
                token_list.add(tk.get());
                this.advance();
            } else if (current_char == ')') {
                Token tk = new Token(TT_RPAREN);
                token_list.add(tk.get());
                this.advance();
            } else {
                Position pos_start = this.pos.copy();
                char ch = this.current_char;
                this.advance();

                IllegalCharError throw_error = new IllegalCharError(pos_start, this.pos, "'" + ch + "'");
                basic.setError(throw_error.as_string());
                basic.setTokens(new ArrayList<>());

                return null;
            }
        }

        basic.setError("");
        return token_list;
    }

    String make_number(){
        String num_str = "";
        String result;
        int dot_count = 0;

        while (current_char == '.' || current_char != '\0' && DIGITS.contains(String.valueOf(current_char))) {
            if (current_char == '.') {
                if (dot_count == 1) {
                    break;
                }

                dot_count += 1;
                num_str += '.';
            } else {
                num_str += current_char;
            }

            this.advance();
        }

        if (dot_count == 0) {
            int i = Integer.parseInt(num_str);
            Token tk = new Token(TT_INT, String.valueOf(i));
            result = tk.get();
        } else {
            float f = Float.parseFloat(num_str);
            Token tk = new Token(TT_FLOAT, String.valueOf(f));
            result = tk.get();
        }

        return result;
    }
}

// ERRORS
class Error {
    Position pos_start;
    Position pos_end;
    String error_name;
    String details;

    public Error(Position pos_start, Position pos_end, String error_name, String details) {
        this.pos_start = pos_start;
        this.pos_end = pos_end;
        this.error_name = error_name;
        this.details = details;
    }

    String as_string() {
        String result = String.format("%s: %s\n", this.error_name, this.details);
        result += String.format("File %s, line %d", pos_start.fn, pos_start.ln + 1);

        return result;
    }
}

class IllegalCharError extends Error {

    public IllegalCharError(Position pos_start, Position pos_end, String details) {
        super(pos_start, pos_end, "Illegal Character", details);
    }

}

// TOKENS
class Token implements Constants{
    String type;
    String value = "\0";

    public Token(String type) {
        this.type = type;
    }

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    String get() {
        if (!value.equals("\0")) {
            return String.format("%s: %s", this.type, this.value);
        }

        return this.type;
    }
}

// POSITION
class Position {
    int idx;
    int ln;
    int col;
    String fn;
    String ftxt;

    public Position(int idx, int ln, int col, String fn, String ftxt) {
        this.idx = idx;
        this.ln = ln;
        this.col = col;
        this.fn = fn;
        this.ftxt = ftxt;
    }

    Position advance(char current_char) {
        this.idx += 1;
        this.col += 1;

        if (current_char == '\n') {
            this.ln += 1;
            this.col = 0;
        }

        return this;
    }

    Position copy() {
        return new Position(this.idx, this.ln, this.col, this.fn, this.ftxt);
    }
}

