
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Parser {
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */

	public Expression parse(ArrayList<String> tokens) throws ParseException{
		int openCounter = 0;
		int closeCounter = 0;
		for (int i = 0; i < tokens.size(); i++){
			// Variable code
			if (Console.declaredVariables.containsKey(tokens.get(i))){
				// insert substituted tokens inside existing tokens
				ArrayList<String> newTokens = new ArrayList<String>();
				int j = 0;

				// copy tokens before var
				while (j < i){
					newTokens.add(tokens.get(j));
					j++;
				}
				j+=1;

				// subsitute tokens that correspond to var
				newTokens.add("(");
				ArrayList<String> sub =  Console.declaredVariables.get(tokens.get(i));
				for(int k = 0; k < sub.size(); k++){
					newTokens.add(sub.get(k));
				}
				newTokens.add(")");

				// copy tokens after variable
				while(j < tokens.size()){
					newTokens.add(tokens.get(j));
					j++;
				}

				tokens = newTokens;
			}
			// Paren balancing
			if (tokens.get(i).equals(")"))
				closeCounter++;
			else if (tokens.get(i).equals("("))
				openCounter++;
		}
		if(openCounter != closeCounter)
			throw new ParseException("Paren not balanced", 0);
		return parseRunner(tokens, 0, tokens.size(), new ArrayList<ParameterVariable>());

	}
	private Expression parseRunner(ArrayList<String> tokens, int start, int end, ArrayList<ParameterVariable> paramVariables) throws ParseException {
		//end is the size of the sub array
		
		// This is nonsense code, just to show you how to thrown an Exception.
		// To throw it, type "error" at the console.'
		// if (var.toString().equals("error")) {
		// 	throw new ParseException("User typed \"Error\" as the input!", 0);
		// }

		// //* ERROR CHECKING CODE
		// System.out.println("Parserunner run");
		// for(int i = start; i < end; i++){
		// 	System.out.print(tokens.get(i) + " ");
		// }
		// System.out.println("Start: " + start);
		// System.out.println("End: " + end);
		// System.out.println();

		//*/

		// No tokens left
		//COME BACK TO THIS: we might be able to delete this 
		if ((end - start) <= 0)			
			return new FreeVariable(""); // ASK MR ISECKE!!!!!!!!!!!!!!!!!!!!!!!!!!!

		// One token left
		if ((end-start) == 1){ 
			return returnVariableType(paramVariables, tokens.get(start));
		}
		
		else {

			ArrayList<Expression> expressions = new ArrayList<Expression>();
			int pos = start;
			while (pos < end){
				
				// Parentheses case
				if (tokens.get(pos).equals("(")){

					// Locate closing parentheses
					int parens = 1;
					int parenPos = pos;
					while (parens != 0){
						parenPos++;
						if (tokens.get(parenPos).equals("("))
							parens++;
						else if (tokens.get(parenPos).equals(")"))
							parens--;
					}
					
					// Add expression inside parentheses
					expressions.add(parseRunner(tokens, pos+1, parenPos, paramVariables));
					pos = parenPos++;
				}

				// functions case
				else if (tokens.get(pos).equals("\\")){

					// adds all parameters to list
					ArrayList<String> params = new ArrayList<String>();
					pos++;
					while (!tokens.get(pos).equals(".")){
						params.add(tokens.get(pos));
						pos++;
					}
					
					if (params.size() == 1){
						Expression exp = parseRunner(tokens, pos+1, end, paramVariables);
						// CREATE A PARAMETER VARIABLE


						ParameterVariable param = new ParameterVariable(params.get(0));
						// add parameter variable to the list
						paramVariables.add(param);
						expressions.add(new Function(param, parseRunner(tokens, pos+1, end, paramVariables)));
						paramVariables.remove(param);
						// remove from the list
						pos = end;

					}

					// does not deal with more than one parameter
				}
			
				else{
					// Add variable to expression
					expressions.add(returnVariableType(paramVariables, tokens.get(pos)));
				}

				pos++;
			}

			// only one expression!
			if (expressions.size() == 1)
				return expressions.get(0);

			// build tree of expressions
			else{
				Application app = new Application(expressions.get(0), expressions.get(1));
				for(int i = 2; i < expressions.size(); i++){
					app = new Application(app, expressions.get(i));
				}
				
				return app;
			}
		}
	}

	public Variable returnVariableType(ArrayList<ParameterVariable> paramVariables, String variable){
		// parameter variables are instantiated from bound or free variables

		boolean contains = false;
		ParameterVariable param = new ParameterVariable(null); // blank

		// bound variable 
		for(int i = 0; i < paramVariables.size(); i++){
			if(variable.equals(paramVariables.get(i).name)){
				contains = true;
				param = paramVariables.get(i);
				break;
			}
		}
		if(contains){
			BoundVariable b = new BoundVariable(variable);
			param.getBoundVars().add(b);
			return b;
		} 

		// free variables
		return new FreeVariable(variable);

	}


}


	
