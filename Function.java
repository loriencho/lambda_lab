import java.time.Year;
import java.util.ArrayList;

public class Function implements Expression {
    private ParameterVariable var;
    private Expression exp;

    public Function(ParameterVariable var, Expression exp){
        this.var = var;
        this.exp = exp;
    }
    
    public static boolean isFunction(String str){
		return str.equals("\\");
	}

    public String toString(){
        return "(\\" + var + "." + exp + ")";
    }

    public Expression getExpression(){
        return exp;
    }

    public ParameterVariable getVariable(){
        return var;
    }

    public Boolean equals(Expression exp){

        if (!exp.getClass().getName().equals("Function")) return false;

        Function func = (Function) exp;
        if((func.getVariable().equals(this.getVariable()))){
            return (func.getExpression().equals(this.getExpression()));
        }

        ArrayList<Variable> pv1 = new ArrayList<Variable>();
        ArrayList<Variable> pv2 = new ArrayList<Variable>();
        pv1.add(this.getVariable());
        pv2.add(func.getVariable());
        
        return equals(pv1, pv2, this.getExpression(), func.getExpression());     

    }

     // to account for eta equivalency
    public Boolean equals(ArrayList<Variable> pv1, ArrayList<Variable> pv2, Expression exp1, Expression exp2){
        if((exp1 instanceof Variable) && (exp2 instanceof Variable)){
            Variable v1 = (Variable)exp1;
            Variable v2 = (Variable)exp2;

            // variables are bound 
            if((v1 instanceof BoundVariable) && (v2 instanceof BoundVariable)){
                int index1 = -1;
                int index2 = -1;

                //pv1 and pv2 are the same size
                for(int i = 0; i < pv1.size(); i++){
                    if(pv1.get(i).name.equals(v1.name)){
                        index1 = i;
                    }
                    if(pv2.get(i).name.equals(v2.name)){
                        index2 = i;
                    }
                } // find where in pv1 and pv2 v1 and v2 are
                return index1 == index2;
            }
            // free variables within the lambda function
            else if(v1.equals(v2)){
                return true;
            }
            return false;
        }
        if((exp1 instanceof Function) && (exp2 instanceof Function)){
            Function f1 = (Function)exp1;
            Function f2 = (Function)exp2;

            pv1.add(f1.getVariable());
            pv2.add(f2.getVariable());
            return equals(pv1, pv2, f1.getExpression(), f2.getExpression());
        }

        if((exp1 instanceof Application) && (exp2 instanceof Application)){
            Application a1 = (Application)exp1;
            Application a2 = (Application)exp2;
            return (equals(pv1, pv2, a1.getLeft(), a2.getLeft()) && equals(pv1, pv2, a1.getRight(), a2.getRight()));
        }
        // not the same type of expression
        return false;

    }


    public Expression setExpression(Expression exp){
        this.exp = exp;
        return exp;
    }
}

/*
questions to ask:
1. Can you explain where the variable and where the expression in function comes rom
2. in λa.a, what does the tree structure look like? like what does the application look like and what is the function's expression and variable?
3. In the console should we able to type both / and λ and it will read as lambda?
4. And how do we get the lambda to print and not the question marks?
*/