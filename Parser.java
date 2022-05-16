
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

		//* ERROR CHECKING CODE
		for(int i = start; i < end; i++){
			System.out.print(tokens.get(i) + " ");
		}
		System.out.println();
		//*/


		// No tokens left
		if ((end - start) <= 0)
			return new Variable("");

		// One token left
		else if ((end-start) == 1)
			return new Variable(tokens.get(start));
		
		// Two tokens left
		else if((end-start) == 2)
			return new Application(new Variable(tokens.get(start)), new Variable(tokens.get(start+1)));
		
		// Lambda expression!
		else if(tokens.get(start).equals("\\")){
			int pos = start+1;
			while(!tokens.get(pos).equals(".")){
				pos++;
				if (pos >= end)
					throw new ParseException("No '.' found after lambda", 0);
			}

			if(pos == start + 2){ // only one bound variable
				Variable var = new Variable(tokens.get(pos-1));
				Expression ex = parseRunner(tokens, pos + 1, end);
				return(new Function(var, ex));
			}
		
			throw new ParseException("WIP! NOT CODED YET", 0);
		}  
		
		// last item is not a close parentheses
		else if (!(")").equals(tokens.get(end - 1))){  // last item in tokens is a variable
			//System.out.println("Start: " + start);
			// System.out.println("End: " + end);

			return new Application(parseRunner(tokens, start, end-1), parseRunner(tokens, end-1, end));
		}

		else {  // last item in tokens is a parenthesis
			// find the opening parenthesis
			// call parse on that range and return!!!
			int openParenPos = getOpenParenPos(tokens, end);
			if(stripParens(openParenPos, start)){
				return parseRunner(tokens, start+1, end-1);
			}
			
			/* ERROR CHECKING CODE
			System.out.println("Running new application with");
			System.out.println(String.valueOf(start) + " " + String.valueOf(openParenPos));
			System.out.println(String.valueOf(openParenPos + 1) + " " + String.valueOf(end - 1));
			*/

			return new Application(parseRunner(tokens, start, openParenPos), parseRunner(tokens, openParenPos+1, end-1));

		}
		
	}
	public int getOpenParenPos(ArrayList<String> tokens, int end) throws ParseException{
		int openParenStack = 0;
		int closeParenStack = 1; // we are currently at a close paren
		int currentPos = end-1;
		int openParenPos = currentPos;
		while(openParenStack != closeParenStack){
			currentPos--;
			if (tokens.get(currentPos).equals("(")){
				openParenPos = currentPos;
				openParenStack++;
			}
			if(tokens.get(currentPos).equals(")")){
				closeParenStack++;
			}
		}

		if(openParenPos == end-1){ //unbalanced parentheses
			throw new ParseException("Parentheses are not balanced.", 0);
		}
		return openParenPos;
	}

	public boolean stripParens(int openParenPos, int start){
		if(start == openParenPos){
			return true;
		}
		return false;
	}
		

}


	
