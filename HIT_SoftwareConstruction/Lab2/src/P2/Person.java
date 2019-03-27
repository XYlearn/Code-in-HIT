/**
 * Person class
 * represent a person
 * @author 
 * Immutable
 */
public class Person {
	private final String name;
	
	/**
     * Abstraction function:
     * 	represents a Person
     * 
     * Representation invariant:
     * 	name is non-empty string
     * 
     * Safety from rep exposure:
     * 	all fields are private and final, name type {@code String} is immutable
     */
	
	/**
	 * constructor
	 * @param name person's name
	 */
	public Person(String name) {
		this.name = name;
		checkRep();
	}
	
	private void checkRep() {
		assert !name.isEmpty();
    }
	
	/**
	 * get person's name
	 * @return person's name
	 */
	public String getName() {
		return name;
	}
	
	@Override 
	public String toString() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override 
	public boolean equals(Object obj) {
		if(obj instanceof Person)
			return ((Person) obj).name.equals(this.name);
		else
			return false;
	}

}
