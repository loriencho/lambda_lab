
public class Function implements Expression {
    Variable var;
    Expression exp;

    public Function(Variable var, Expression exp){
        this.var = var;
        this.exp = exp;
    }
    
    public static boolean isFunction(String str){
		return str.equals("λ");
	}

    public String toString(){
        return "λ" + var + "." + exp;
    }
}

/*
questions to ask:
1. Can you explain where the variable and where the expression in function comes rom
2. in λa.a, what does the tree structure look like? like what does the application look like and what is the function's expression and variable?
3. In the console should we able to type both / and λ and it will read as lambda?
4. And how do we get the lambda to print and not the question marks?
*/