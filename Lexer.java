
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	
	/*
	 * A lexer (or "tokenizer") converts an input into tokens that
	 * eventually need to be interpreted.
	 * 
	 * Given the input 
	 *    (\bat  .bat flies)cat  λg.joy! )
	 * you should output the ArrayList of strings
	 *    [(, \, bat, ., bat, flies, ), cat, \, g, ., joy!, )]
	 *
	 */
	private char[] individual_chars = {'(', ')', '/', ',', '.', '='};

	public ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();
		char[] in = input.getCharArray();
		for (int i =0; i < in.length; i++){
			if (indivual_chars.contains(in[i])){
				
			}
		
		}


		

		// This next line is definitely incorrect!
		tokens.add(input);

		return tokens;
	}



}
