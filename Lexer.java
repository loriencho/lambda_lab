
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
	 
	 */
	private ArrayList<String> individual_chars = new ArrayList<>(Arrays.asList("\\", "(", ")", "/", ".", "="));

	public ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();

		String token = "";
		input = input.trim();
		
		for (int i =0; i< input.length(); i++){
			String current = input.substring(i, i+1);

			if (current.equals(";")){
				if (!token.equals("")){ //if we're building something in token currently
					tokens.add(token);
					token = "";			
				}
				break;
			}
			
			else if (individual_chars.contains(current)){
				if(!(token.equals(""))){
					tokens.add(token);}
				tokens.add(String.valueOf(current));
				token = "";
			}
			
			/*
			else if( i<input.length()-1 && (input.substring(i, i+2)).equals("\\")){
				if(!(token.equals(""))){
					tokens.add(token);
				}
				tokens.add("\\");
				token = "";
				
			}
			*/
			
			else if (current.equals(" ")){ //if we get a space

				if (!token.equals("")){ //if we're building something in token currently, add it because space means end of var name
					tokens.add(token);
					token = "";			
				}
				else{							// nothing in token - just whitespace
					continue;
				}
			}
			else {
				token += current;
			}

			
		}
		
		if (!token.equals("")) // add last token
			tokens.add(token);

		return tokens;
	}



}
