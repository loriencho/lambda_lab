
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Console {
	private static Scanner in;
	public static HashMap<String, ArrayList<String>> variables = new HashMap<String, ArrayList<String>>();
	public static ArrayList<String> variableNames = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception{
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
				if (tokens.size() < 1){
				}
				else if (tokens.size() > 1 && tokens.get(1).equals("=")){
					if(!variables.containsKey(tokens.get(0))){
						Expression exp;
						ArrayList<String> newTokens;
						if(tokens.get(2).equals("run")){
							newTokens = new ArrayList<String>(tokens.subList(3, tokens.size()));
							variables.put(tokens.get(0), newTokens);
							exp = parser.parse(newTokens);
							getVariables(exp);
							exp = substitute(exp);

						}
						else{
							newTokens = new ArrayList<String>(tokens.subList(2, tokens.size()));
							exp = parser.parse(newTokens);
							variables.put(tokens.get(0), newTokens);
						}

						System.out.println("Added " + exp +" as " + tokens.get(0));
					}
					else {
						System.out.println(tokens.get(0) + " is already defined.");
					}
				}
				// run!!
				else if (tokens.size() > 1 && tokens.get(0).equals("run")){
					ArrayList<String> newTokens = new ArrayList<String>(tokens.subList(1, tokens.size()));

					System.out.println(newTokens.toString());
					Expression exp = parser.parse(newTokens);

					// System.out.println("Returned from parsing in run case. Exp: " + exp + "Exp class: " + exp.getClass().getName() );
					// if (exp instanceof Application){
					// 	System.out.println("Casts to application. Left: " + ((Application)exp).getLeft() + "Left class: " + ((Application)exp).getLeft().getClass().getName());
					// 	System.out.println("Casts to application. Right: " + ((Application)exp).getRight() + "RIght class: " + ((Application)exp).getRight().getClass().getName());

					// }


					// later - it does not need to be in a variable but it still needs to be called
					getVariables(exp);
					Expression subbed = substitute(exp);
				
			
					System.out.println(subbed);

					// System.out.println("Parameters: " + satwikalist.get("parameter").toString());
					// System.out.println("Bound: " + satwikalist.get("bound").toString());
					// System.out.println("Free: " + satwikalist.get("free").toString());

				}
				else {
					Expression exp = parser.parse(tokens);
					System.out.println(exp.toString());

				}
			 } catch (Exception e) {
				 throw(e);
			 	// System.out.println("Unparsable expression, input was: \"" + input + "\"");
			 	// input = cleanConsoleInput();
			 	// continue;
			 }
						
			input = cleanConsoleInput();
		}
		System.out.println("Goodbye!");
	}
	/*
	private static Expression substitute(Expression original){
		
		if (original instanceof Application){
			Expression left  = ((Application)original).getLeft();
			Expression right = ((Application)original).getRight();
			original = alphaReduce(left, right);			

			if (left instanceof Function){
				Function f = (Function)left;
				left = substituteRunner(f.getExpression(), right, f.getVariable());
				return substitute(left);
			}
			else {
				return new Application(substitute(left), substitute(right));
			}
		}
		else if (original instanceof Function){
			System.out.println("Function within substitute: parameter is " + ((Function)original).getVariable());
			Function f = (Function) original;
			return new Function(f.getVariable(), substitute(f.getExpression())); // for now
		}
		else {
			return original;
		}
		
	}
	*/

	private static Expression substitute(Expression exp){
		Expression ret;
		if(exp instanceof Application){
			Application a = (Application) exp;
			a = alphaReduce(a.getLeft(), a.getRight());
			if (a.getLeft() instanceof Function){
				Function f = (Function)(a.getLeft());
				ret =  substituteRunner(f.getExpression(), a.getRight(), f.getVariable());
				return substitute(ret);
			}
			else{
				Expression newExp = new Application(substitute(a.getLeft()), substitute(a.getRight()));

				while(newExp instanceof Application
				&& ((Application)newExp).getLeft() instanceof Function 
				&& ((Application)newExp).getRight() instanceof Variable){
					newExp = substitute(newExp);

				}

				return newExp;
			}	
		}
		else if (exp instanceof Function){
			Function f = (Function)exp;
			ret = new Function(f.getVariable(), substitute(f.getExpression()));
			return ret;
		}
		else{
			return exp;
		}
	}



	

	// only substitutes for one function at a time
	private static Expression substituteRunner(Expression exp, Expression sub, Variable bound){
		if (exp instanceof Application){
			Application app = (Application)exp;
			return new Application(substituteRunner(app.getLeft(), sub, bound), substituteRunner(app.getRight(), sub, bound));
		}
		else if (exp instanceof Function){
			Function f  = (Function)exp;
			System.out.println("Sub runner function"+  f);;

			System.out.println("Sub runner bound "+  bound);;

			System.out.println("Sub runner f var "+  f.getVariable());;
			if (!((f.getVariable().name).equals(bound.name))){
				System.out.println("Subbing in a fucntion");
				Function ret = new Function(f.getVariable(), substituteRunner(f.getExpression(), sub, bound));
				System.out.println(ret);
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

	
	public static Application alphaReduce(Expression left, Expression right){
		ArrayList<Variable> leftParams = getVariables(left).get("parameter");
		ArrayList<Variable> rightVariables = getVariables(right).get("free");
		rightVariables.addAll(getVariables(right).get("parameter"));
				
		for (int i = 0; i < leftParams.size(); i++){
			Variable leftParam = leftParams.get(i);
			ParameterVariable param = ((ParameterVariable)leftParam);


			for(int j = 0; j < rightVariables.size(); j++){
				if(rightVariables.get(j).name.equals(param.name)){
					rename(param);
					break;
				}
			}
		}

		return new Application(left, right);
	}

	public static boolean addToNames(ArrayList<Variable> vars){
		for(int i = 0; i < vars.size(); i++){
			if (!(variableNames.contains(vars.get(i).name))){
				variableNames.add(vars.get(i).name);
				return true;
			}
		}
		return false;
	}

	public static String rename(Variable var){
		if(!(variableNames.contains(var.name))){
			return var.name;
		}
		
		int count = 1; 
		while(variableNames.contains(var.name + String.valueOf(count))){
			count++;

		}

		String name = var + String.valueOf(count);
		variableNames.add(name);
		var.setName(name);	

		if (var instanceof ParameterVariable){
			ParameterVariable param = (ParameterVariable)var;
			// goes through all bound variables
			ArrayList<BoundVariable> boundVars = param.getBoundVars();
			for(int j = 0; j < boundVars.size(); j++){
				// changes name to match param variable
				boundVars.get(j).setName(param.name);
			}	
		}	
		return name;

	}
	

	private static HashMap<String, ArrayList<Variable>> getVariables(Expression exp){
		return getVariables(exp, new ArrayList<Variable>(), new ArrayList<Variable>(), new ArrayList<Variable>());
	}

	private static HashMap<String, ArrayList<Variable>> getVariables(Expression exp, ArrayList<Variable> fVariables, ArrayList<Variable> pVariables, ArrayList<Variable> bVariables){
		HashMap<String, ArrayList<Variable>> a = new HashMap<String, ArrayList<Variable>>();
		if (exp instanceof Variable){
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
			addToNames(fVariables);
			addToNames(pVariables);
			
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
		if(exp instanceof ParameterVariable){

			ArrayList<BoundVariable> bv = ((ParameterVariable) exp).getBoundVars();
			ArrayList<BoundVariable> copy = new ArrayList<BoundVariable>();


			for(int i = 0; i < bv.size(); i++){
				copy.add(bv.get(i));
			}
			return new ParameterVariable(exp.toString(), copy);

		}
		else if(exp instanceof FreeVariable){

			return new FreeVariable(exp.toString());

		}
		else if(exp instanceof BoundVariable){
			return new BoundVariable(exp.toString());

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