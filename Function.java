
public class Function implements Expression {
    Variable operator;

    public Function(Variable operator){
        this.operator = operator;
    }
    
    public static boolean isFunction(String str){
		return str.equals("λ");
	}

    public String toString(){
        return operator.toString();
    }
}
