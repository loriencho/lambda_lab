import java.text.ParseException;

public class EqualTesting {


    public static void main(String[] args) throws ParseException{
        Parser parser = new Parser();
        Lexer lexer = new Lexer();
		Expression exp = parser.parse(lexer.tokenize("(a b)"));
		Expression exp2 = parser.parse(lexer.tokenize("(A B)"));
        System.out.println(exp);
        System.out.println(exp2);
        System.out.println(exp.equals(exp2));
 
    }
}