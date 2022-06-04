
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Console {
	private static Scanner in;
	public static HashMap<String, ArrayList<String>> declaredVariables = new HashMap<String, ArrayList<String>>();
	public static HashMap<Expression, String> expressionToVariables = new HashMap<Expression,String>();
	public static ArrayList<Expression> variableExpressions = new ArrayList<Expression>();
	public static ArrayList<String> variableNames = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception{
		//PrintStream fileOut = new PrintStream("./out.txt");
		//System.setOut(fileOut);

		in = new Scanner (System.in);
		
		Lexer lexer = new Lexer();
		Parser parser = new Parser();
		
		String input = cleanConsoleInput();
		
		while (! input.equalsIgnoreCase("exit")) {
			int len = declaredVariables.size();
			
			ArrayList<String> tokens = lexer.tokenize(input);

			String output = "";
			
			try {

				variableNames = new ArrayList<String>();
				// setting a variable
				if (tokens.size() < 1){
				}
				else if (tokens.size() > 1 && tokens.get(1).equals("=")){
					if(!declaredVariables.containsKey(tokens.get(0))){
						Expression exp;
						ArrayList<String> newTokens;
						if(tokens.get(2).equals("run")){
							newTokens = new ArrayList<String>(tokens.subList(3, tokens.size()));
							exp = parser.parse(newTokens);
							getVariables(exp);
							exp = substitute(exp);
							declaredVariables.put(tokens.get(0), lexer.tokenize(exp.toString()));
						}
						else{
							newTokens = new ArrayList<String>(tokens.subList(2, tokens.size()));
							exp = parser.parse(newTokens);
							declaredVariables.put(tokens.get(0), newTokens);
						}
						variableExpressions.add(exp);
						expressionToVariables.put(exp, tokens.get(0));
						System.out.println("Added " + exp +" as " + tokens.get(0));
					}
					else {
						System.out.println(tokens.get(0) + " is already defined.");
					}
				}
				
				//run!
				else if (tokens.size() > 1 && tokens.get(0).equals("run")){
					ArrayList<String> newTokens = new ArrayList<String>(tokens.subList(1, tokens.size()));

					Expression exp = parser.parse(newTokens);

					// later - it does not need to be in a variable but it still needs to be called
					getVariables(exp);
					Expression subbed = substitute(exp);

					Expression replaced = insertVariables(deepCopy(subbed));
			
					System.out.println(replaced);

				}
				else {
					Expression exp = parser.parse(tokens);
					System.out.println(exp.toString());

				}
			 } catch (Exception e) {
				 throw(e);
			 }
						
			input = cleanConsoleInput();
		}
		System.out.println("Goodbye!");
	}

	private static Expression insertVariables(Expression exp){

		for(int i=0; i < variableExpressions.size(); i++){
			Expression varExp = variableExpressions.get(i);
			if (varExp.equals(exp)){
				return new FreeVariable(expressionToVariables.get(varExp));
			}
		}
		
		if (exp instanceof Variable){
			return exp;
		}
		
		else if(exp instanceof Function){
			Function f = (Function)exp;
			return new Function(f.getVariable(), insertVariables(f.getExpression()));
		}
	
		else { // application case 
			Application a = (Application)exp;
			return new Application(insertVariables(a.getLeft()), insertVariables(a.getRight()));			
		}

	}

	private static Expression substitute(Expression original){

		ArrayList<String> redexPath = findRedexPath(original);

		if(redexPath == null){
			return original;
		}
		
		while (!(redexPath == null)){
			Application redex = getRedex(new ArrayList<String>(redexPath), original); 
			redex = alphaReduce(redex.getLeft(), redex.getRight());

			Function f = ((Function) (redex.getLeft()));

			original = replace(new ArrayList<String>(redexPath), substituteRunner(f, redex.getRight()), original);
			redexPath = findRedexPath(original);

		}
		return original;

	}
	
	private static ArrayList<String> findRedexPath(Expression exp){
		return findRedexPath(exp, new ArrayList<String>(), exp);
	}
	

	private static ArrayList<String> findRedexPath(Expression exp, ArrayList<String> path, Expression original){
		path = new ArrayList<String>(path);
		if(exp instanceof Variable){
			return null;
		}
		else if (exp instanceof Function){
			path.add("right");
			return findRedexPath(((Function)exp).getExpression(), path, original);
		}
		else{ // Application
			Application a = (Application)exp;

			// Found redex!!
			if (a.getLeft() instanceof Function){
				return path;
			}
				
			path.add("left");
			ArrayList<String> left = findRedexPath(a.getLeft(), path, original);
			
			// Successfully found redex
			if (!(left == null)){
				return left;}
			else {
				//remove the 'left', replace with 'right'

				String remove = path.remove(path.size()-1);
				if(remove.equals("left")){
					path.add("right");
					return findRedexPath(a.getRight(), path, original);

				}
				else{
					while((path.size() >= 1) && (path.get(path.size() - 1).equals("right"))){
						path.remove(path.size() - 1);
					}
					if(path.size() == 0){
						return null; // we only took rights and found no redexes
					}
					else{ //we hit a left
						path.remove(path.size() - 1);
						path.add("right"); // replace the previous left with a right
						return findRedexPath(getRedex(path, original), path, original);
					}
				}
				

				

			}
			
		}
		
	}

	private static Application getRedex(ArrayList<String> path, Expression current){
		//get redex given the path 
		if(path.size() == 0){
			return (Application)(current);
		}

		else if (path.get(0).equals("right")){
			path.remove(0);
			if (current instanceof Function){
				return getRedex(path,((Function)current).getExpression());
			}
			else // Application
				return getRedex(path, ((Application)current).getRight());
		}
		else{ 
			path.remove(0);
			return getRedex(path, ((Application)current).getLeft());
		}

	}

	private static Expression replace(ArrayList<String> path, Expression newExpression, Expression current){
		if (path.size() == 0){
			return newExpression;

		}
		else if(path.size() == 1){

			if (current instanceof Application){
				Application a = (Application) current;

				if (path.get(0).equals("right")){
					return new Application(a.getLeft(), newExpression);
				}
				else{
					return new Application(newExpression, a.getRight());
				}
			}
			else { // current is a function
				if (current instanceof Function){
					Function f = (Function) current;
					return new Function(f.getVariable(), newExpression);					
				}

				// variable
				else{
					
					return newExpression;
				}
			}

		}
		else if(path.get(0).equals("right")){
			path.remove(0);
			if(current instanceof Function){
				Function f = (Function) current;
				return new Function(f.getVariable(), replace(path, newExpression, f.getExpression()));
				
			}
			else {
				Application a = (Application) current;
				return new Application(a.getLeft(), replace(path, newExpression, a.getRight()));
			}
		}
		else { // left - has to be an application
			path.remove(0);
			Application a = (Application) current;
			return new Application(replace(path, newExpression, a.getLeft()), a.getRight());
		}
	}

	private static Expression substituteRunner(Function exp, Expression sub){
		return substituteRunner(exp.getExpression(), deepCopy(sub), exp.getVariable());
	}

	// only substitutes for one function at a time
	private static Expression substituteRunner(Expression exp, Expression sub, Variable bound){
		if (exp instanceof Application){
			Application app = (Application)exp;
			return new Application(substituteRunner(app.getLeft(), sub, bound), substituteRunner(app.getRight(), sub, bound));
		}
		else if (exp instanceof Function){
			Function f  = (Function)exp;

			if (!((f.getVariable().name).equals(bound.name))){
				Function ret = new Function(f.getVariable(), substituteRunner(f.getExpression(), sub, bound));
				return ret;
			}
			else 
				return f;
		}
		else{
			// Variable case
			Variable var = (Variable)exp;
			if ((var.name).equals(bound.name))
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
		Application ret= new Application(left, right);
		
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
			Set<BoundVariable> boundVars = param.getBoundVars();
			for(BoundVariable b : boundVars){
				// changes name to match param variable
				b.setName(param.name);
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



	}

	private static Expression deepCopy(Expression exp){
		Expression new_exp = deepCopy(exp, new ArrayList<Variable>());
		Parser.setBoundVariables(new_exp, new ArrayList<>());
		return new_exp;

	}
		
	  
	private static Expression deepCopy(Expression exp, ArrayList<Variable> paramVariables){
		if(exp instanceof ParameterVariable){

			ParameterVariable param =new ParameterVariable(exp.toString(), new HashSet<BoundVariable>()); 
			paramVariables.add(param);
			return param;

		}
		else if(exp instanceof FreeVariable){

			return new FreeVariable(exp.toString());

		}
		else if(exp instanceof BoundVariable){
			BoundVariable var = new BoundVariable(exp.toString());
		
			return var;

		}
		else if(exp instanceof Function){
			Function f = (Function)exp;
			return new Function((ParameterVariable)deepCopy(f.getVariable(), paramVariables), deepCopy(f.getExpression(), paramVariables));
		}
		else{ // is an application
			Application app = (Application)exp;
			return new Application(deepCopy(app.getLeft(), paramVariables), deepCopy(app.getRight(), paramVariables));
		}

	}

	
	/*
	 * Collects user input, and ...
	 * ... does a bit of raw string processing to (1) strip away comments,  
	 * (2) remove the BOM character that appears in unicode strings in Windows,
	 * (3) turn all weird whitespace characters into spaces,
	 * and (4) replace all backslashes with λ.
	 */

	 
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