
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Console {
	private static Scanner in;
	public static HashMap<String, Expression> variables = new HashMap<String, Expression>();
	
	public static void main(String[] args) {
		in = new Scanner (System.in);
		
		Lexer lexer = new Lexer();
		Parser parser = new Parser();
		
		String input = cleanConsoleInput();
		
		while (! input.equalsIgnoreCase("exit")) {
			int len = variables.size();
			
			ArrayList<String> tokens = lexer.tokenize(input);

			String output = "";
			
			try {

				// setting a variable
				if (tokens.size() > 1 && tokens.get(1).equals("=")){
					if(!variables.containsKey(tokens.get(0))){
						if(tokens.get(2).equals("run")){
							ArrayList<String> newTokens = new ArrayList<String>(tokens.subList(3, tokens.size()));
							Expression exp = parser.parse(newTokens);
							Expression subbed = substitute(exp);
							variables.put(tokens.get(0), subbed);
						}
						else{
							ArrayList<String> newTokens = new ArrayList<String>(tokens.subList(2, tokens.size()));
							variables.put(tokens.get(0), parser.parse(newTokens));
						}
						System.out.println("Added " + variables.get(tokens.get(0)) +" as " + tokens.get(0));
					}
					else {
						System.out.println(tokens.get(0) + " is already defined.");
					}
				}
				// run!!
				else if (tokens.size() > 1 && tokens.get(0).equals("run")){
					ArrayList<String> newTokens = new ArrayList<String>(tokens.subList(1, tokens.size()));
					Expression exp = parser.parse(newTokens);
					HashMap<String, ArrayList<Variable>> satwikalist = getVariables(exp);
					Expression subbed = substitute(exp);
					System.out.println(subbed);

					System.out.println("Parameters: " + satwikalist.get("parameter").toString());
					System.out.println("Bound: " + satwikalist.get("bound").toString());
					System.out.println("Free: " + satwikalist.get("free").toString());

				}
				else {
					Expression exp = parser.parse(tokens);
					System.out.println(exp.toString());

				}
			} catch (Exception e) {
				System.out.println("Unparsable expression, input was: \"" + input + "\"");
				input = cleanConsoleInput();
				continue;
			}
						
			input = cleanConsoleInput();
		}
		System.out.println("Goodbye!");
	}
	private static Expression substitute(Expression original){


		if (original instanceof Application){
			Expression left  = ((Application)original).getLeft();
			Expression right = ((Application)original).getRight();
			if (((Application)original).getLeft() instanceof Function){
				System.out.println("Subbing");
				Function f = (Function)left;
				left = substituteRunner(f.getExpression(), right, f.getVariable());
				return substitute(left);
			}
			else {
				return new Application(substitute(left), substitute(right));
			}
		}
		else {
			return original;
		}
	}

	private static Expression substituteRunner(Expression exp, Expression sub, Variable bound){
		if (exp instanceof Application){
			Application app = (Application)exp;
			return new Application(substituteRunner(app.getLeft(), sub, bound), substituteRunner(app.getRight(), sub, bound));
		}
		else if (exp instanceof Function){
			Function f  = (Function)exp;
			if (!f.getVariable().equals(bound)){
				return new Function(f.getVariable(), substituteRunner(f.getExpression(), sub, bound));
			}
			else 
				return f;
		}
		else{
			// Variable case
			Variable var = (Variable)exp;
			if (var.equals(bound))
				return deepCopy(sub);
			else 
				return var;

		}
	}

	private static HashMap<String, ArrayList<Variable>> getVariables(Expression exp){
		return getVariables(exp, new ArrayList<Variable>(), new ArrayList<Variable>(), new ArrayList<Variable>());
	}

	private static HashMap<String, ArrayList<Variable>> getVariables(Expression exp, ArrayList<Variable> fVariables, ArrayList<Variable> pVariables, ArrayList<Variable> bVariables){
		HashMap<String, ArrayList<Variable>> a = new HashMap<String, ArrayList<Variable>>();

		if (exp instanceof Variable){
			System.out.println("HELLLOOOOOOOOOOOO");
			if (exp instanceof FreeVariable){
				fVariables.add((FreeVariable)exp);
			}
			else if(exp instanceof ParameterVariable){
				pVariables.add((ParameterVariable)exp);
			}
			else{
				bVariables.add((BoundVariable)exp);
			}
			a.put("free", fVariables);
			a.put("parameter", pVariables);
			a.put("bound", bVariables);
			return a;
		}

		else if (exp instanceof Function){
			Function f = (Function)exp;
			pVariables.add(f.getVariable());
			return getVariables(f.getExpression(), fVariables, pVariables, bVariables);

		}

		//else
		Application app = (Application)exp;
		HashMap<String, ArrayList<Variable>> left = getVariables(app.getLeft(), fVariables, pVariables, bVariables);

		return getVariables(app.getRight(), fVariables, pVariables, bVariables); // the arraylists should be updated by left because of pass by reference??


		/*
		POTENTIALLY
		take result of getVariables on left and right expression 
		and take the resulting lists from the hashmaps
		and combine them
		and return them in a new hashmap
		*/
		
		/*
		take result of getVariables on left and right expression 
		and take the resulting lists from the hashmaps

		compare similarities of the param and free and
			do something.

			
		*/
		
	}

	
	/*
	 * Collects user input, and ...
	 * ... does a bit of raw string processing to (1) strip away comments,  
	 * (2) remove the BOM character that appears in unicode strings in Windows,
	 * (3) turn all weird whitespace characters into spaces,
	 * and (4) replace all backslashes with λ.
	 */
	
	private static Expression deepCopy(Expression exp){
		if(exp instanceof Variable){
			return new Variable(exp.toString());
		}
		else if(exp instanceof Function){
			Function f = (Function)exp;
			return new Function((Variable) deepCopy(f.getVariable()), deepCopy(f.getExpression()));
		}
		else{ // is an application
			Application app = (Application)exp;
			return new Application(deepCopy(app.getLeft()), deepCopy(app.getRight()));
		}

	}
	  
	private static String cleanConsoleInput() {
		System.out.print("> ");
		String raw = in.nextLine();
		String deBOMified = raw.replaceAll("\uFEFF", ""); // remove Byte Order Marker from UTF

		String clean = removeWeirdWhitespace(deBOMified);
		
		if (deBOMified.contains(";")) {
			clean = deBOMified.substring(0, deBOMified.indexOf(";"));
		}
		
		return clean.replaceAll("λ", "\\\\");
	}
	
	
	public static String removeWeirdWhitespace(String input) {
		String whitespace_chars =  ""       /* dummy empty string for homogeneity */
				+ "\\u0009" // CHARACTER TABULATION
				+ "\\u000A" // LINE FEED (LF)
				+ "\\u000B" // LINE TABULATION
				+ "\\u000C" // FORM FEED (FF)
				+ "\\u000D" // CARRIAGE RETURN (CR)
				+ "\\u0020" // SPACE
				+ "\\u0085" // NEXT LINE (NEL) 
				+ "\\u00A0" // NO-BREAK SPACE
				+ "\\u1680" // OGHAM SPACE MARK
				+ "\\u180E" // MONGOLIAN VOWEL SEPARATOR
				+ "\\u2000" // EN QUAD 
				+ "\\u2001" // EM QUAD 
				+ "\\u2002" // EN SPACE
				+ "\\u2003" // EM SPACE
				+ "\\u2004" // THREE-PER-EM SPACE
				+ "\\u2005" // FOUR-PER-EM SPACE
				+ "\\u2006" // SIX-PER-EM SPACE
				+ "\\u2007" // FIGURE SPACE
				+ "\\u2008" // PUNCTUATION SPACE
				+ "\\u2009" // THIN SPACE
				+ "\\u200A" // HAIR SPACE
				+ "\\u2028" // LINE SEPARATOR
				+ "\\u2029" // PARAGRAPH SEPARATOR
				+ "\\u202F" // NARROW NO-BREAK SPACE
				+ "\\u205F" // MEDIUM MATHEMATICAL SPACE
				+ "\\u3000"; // IDEOGRAPHIC SPACE 
		Pattern whitespace = Pattern.compile(whitespace_chars);
		Matcher matcher = whitespace.matcher(input);
		String result = input;
		if (matcher.find()) {
			result = matcher.replaceAll(" ");
		}

		return result;
	}

}