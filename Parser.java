
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */

	public Expression parse(ArrayList<String> tokens) throws ParseException{
		return parseRunner(tokens, 0, tokens.size());

	}
	public Expression parseRunner(ArrayList<String> tokens, int start, int end) throws ParseException {
		//end is the size of the sub array
		
		// This is nonsense code, just to show you how to thrown an Exception.
		// To throw it, type "error" at the console.'
		// if (var.toString().equals("error")) {
		// 	throw new ParseException("User typed \"Error\" as the input!", 0);
		// }

		for(int i = start; i < end; i++){
			System.out.print(tokens.get(i) + " ");
		}
		System.out.println();


		if ((end - start) <= 0){
			return new Variable("");
		}
		if ((end-start) == 1){
			return new Variable(tokens.get(start));
		}
		else if((end-start) == 2){
			return new Application(new Variable(tokens.get(start)), new Variable(tokens.get(start+1)));

		} //both are variables
		
		// not equal to close paren
		else if (!(")").equals(tokens.get(end - 1))){  // last item in tokens is a variable
			System.out.println("Start: " + start);
			System.out.println("End: " + end);

			return new Application(parseRunner(tokens, start, end-1), parseRunner(tokens, end-1, end));
		}

		else {  // last item in tokens is a parenthesis
			// find the opening parenthesis
			// call parse on that range and return!!!

			// find opening parenthesis NEED TO REWRITE TO DEAL WITH CASES LIKE a b c ((d e))
			System.out.println("end is open paren");
			int pos = end-1;
			int openParen = pos;
			while(tokens.get(pos) != "("){
				pos--;
			}

			System.out.println(openParen);

			return new Application(parseRunner(tokens, start, openParen), parseRunner(tokens, openParen+1, end-1));

			}
		
		}
		

		}


	
