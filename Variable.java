//Satwika Vemuri and Lorien Cho

public abstract class Variable implements Expression {
	protected String name;
	
	public Variable(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

	public void setName(String newName){
        this.name = newName;
    }
 
	public Boolean equals(Expression exp){
		return (this.getClass().equals(exp.getClass())) && (exp.toString()).equals(this.toString());
	}

}
