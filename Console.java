
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
						ArrayList<String> newTokens = new ArrayList<String>(tokens.subList(2, tokens.size()));
						variables.put(tokens.get(0), parser.parse(newTokens));
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
					Expression subbed = substitute(exp);


					if(subbed instanceof Variable){
						System.out.println("var");
						}
						else if(subbed instanceof Function){
						System.out.println("func");
						}
						else{
								System.out.println("app");
							if(((Application)subbed).getLeft() instanceof Application){
								System.out.println("yes");
							}
							else{
								System.out.println("no");
							}
						}
					System.out.println(subbed);


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
		Expression left; 
		Expression right; 

		if ((original instanceof Application) && (((Application)original).getRight() instanceof Application)){
			System.out.println("right is app");
			left = ((Application)original).getLeft();
			right = ((Application)original).getRight();
			return new Application(left, substitute(right));
		}
		else{
			left = ((Application)original).getLeft();
			right = ((Application)original).getRight();
			System.out.println("Subbing");
			Expression exp = left;
			Expression sub = right;
			Function f = (Function)exp;
			exp = substituteRunner(f.getExpression(), sub, f.getVariable());
		
			if (exp instanceof Application && ((Application)exp).getLeft() instanceof Function){
				return substitute(exp);
			}
			return exp;
		}
	}

	private static Expression substituteRunner(Expression exp, Expression sub, Variable bound){
		if (exp instanceof Application){
			Application app = (Application)exp;
			return new Application(substituteRunner(app.getLeft(), sub, bound), substituteRunner(app.getRight(), sub, bound));
		}
		else if (exp instanceof Function){
			Function f  = (Function)exp;
			if (f.getVariable().equals(bound)){
				return f;
			}
			else 
				return substituteRunner(f.getExpression(), sub, bound);
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