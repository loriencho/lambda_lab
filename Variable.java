
public class Variable implements Expression {
	protected String name;
	
	public Variable(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

	
	public boolean equals(Variable variable){
		return variable.toString().equals(this.toString());

	}
}
