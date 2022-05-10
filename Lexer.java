
import java.util.ArrayList;
import java.util.Arrays;
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
	private ArrayList<String> individual_chars = new ArrayList<>(Arrays.asList("(", ")", "/", "," ".", "=", "λ"));

	public ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();

		char[] in = (input.trim()).toCharArray();
		String token = "";
		for (int i =0; i < in.length; i++){ 
			if (individual_chars.contains(in[i])){ // if special char just add 
				tokens.add(token);
				tokens.add(String.valueOf(in[i]));
				token =  "";
			}
			else if(in[i] == ' ' && !token.equals("")){ // add old token, start new token
				tokens.add(token);
				token = "";
			}
			else{ // continue building the token in str
				token += in[i];
			}
		}

		if (!token.equals("")) // add last token
			tokens.add(token);

		return tokens;
	}



}
