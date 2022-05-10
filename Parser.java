
import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		
		// This is nonsense code, just to show you how to thrown an Exception.
		// To throw it, type "error" at the console.'
		// if (var.toString().equals("error")) {
		// 	throw new ParseException("User typed \"Error\" as the input!", 0);
		// }

		System.out.print(tokens.toString());
		if (tokens.size() == 0){
			return new Variable("");
		}
		if (tokens.size() == 1){
			return new Variable(tokens.get(0));
		}
		else{
			


		}


	}
}
