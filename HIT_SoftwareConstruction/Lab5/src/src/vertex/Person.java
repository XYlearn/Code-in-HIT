package vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a Person in SocialNetwork. It's an Mutable type extends Vertex
 */
public class Person extends Vertex {

	/**
	 * AF: label:String represent Person's name; sex:String represent Person's
	 * sex; age:int represent Person's age.
	 * 
	 * RI: age >= 0; sex == "M" or sex == "F"
	 * 
	 * safety from rep exposure: label is unmodifiable, the sex can't be set
	 * again after wrap
	 */

	protected String sex = null;
	protected int age;

	/**
	 * Construction
	 * 
	 * @param name
	 *            name of person
	 */
	protected Person(String name) {
		super(name);
	}

	protected void checkRep() {
		// super.checkRep();
		// assert sex.equals("M") || sex.equals("F");
		// assert age >= 0;
	}

	/**
	 * Check the Person's sex
	 * 
	 * @return return true if the Person is male
	 */
	public boolean isMale() {
		return this.sex.equals("M");
	}

	/**
	 * Get age of Person
	 * 
	 * @return return the Person's age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Set age of Person
	 * 
	 * @param age
	 *            Person's age
	 * @return return true if age set successfully. if age is invalid return
	 *         false
	 */
	public boolean setAge(int age) {
		if (age < 0)
			return false;
		else {
			this.age = age;
			checkRep();
			return true;
		}
	}

	/**
	 * Fill detail information of person. Information include the sex and age of
	 * the Person. If the sex has been set, it will not be modified again and be
	 * ignored.
	 * 
	 * @param args
	 *            args[0] represents sex, either 'M'(for male) or 'F'(for
	 *            female). args[1] represents age, should be string of a
	 *            non-negative number
	 * @exception IllegalArgumentException
	 *                raise if the argument is illegal, and don't modify the
	 *                information
	 */
	@Override
	public void fillVertexInfo(String[] args) throws IllegalArgumentException {
		// set sex if hasn't been set
		if (null == this.sex) {
			if (!args[0].equals("M") && !args[0].equals("F"))
				throw new IllegalArgumentException("Illegal sex");
		}
		// parse age: args[1]
		int age;
		try {
			age = Integer.valueOf(args[1]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal age");
		}

		if (age < 0)
			throw new IllegalArgumentException("Illegal age");
		// all check passed, the information will be updated
		else {
			this.age = age;
			if (null == this.sex)
				this.sex = args[0];
		}
		checkRep();
	}

	@Override
	public String toString() {
		return label + "(" + sex + " , " + age + ")";
	}

	@Override
	public Person clone() {
		// return wrap(getLabel(), isMale(), this.age);
		return this;
	}

	/**
	 * Generate a Person Instance from given arguments
	 * 
	 * @param name
	 *            Person's name
	 * @param isMale
	 *            if the Person is Male, pass true; else pass false
	 * @param age
	 *            age of the person
	 * @return return generated Person Instance if age >= 0. else return null.
	 */
	public static Person wrap(String name, boolean isMale, int age) {
		return wrap(Person.class, name, isMale, age);
	}

	/**
	 * Generate a Person Instance from given arguments
	 * 
	 * @param label
	 *            name of Person
	 * @param args
	 *            argument to fill in see
	 *            {@link Person#fillVertexInfo(String[])}
	 * @return return generated Person Instance if args are valid, else return
	 *         null.
	 */
	public static Person wrap(String label, String[] args) {
		return wrap(Person.class, label, args);
	}

	/**
	 * Generate a Person Instance from given arguments
	 * 
	 * @param type
	 *            Class of Instance to generate
	 * @param label
	 *            name of Person
	 * @param args
	 *            argument to fill in see
	 *            {@link Person#fillVertexInfo(String[])}
	 * @return return generated Person Instance if args are valid, else return
	 *         null.
	 */
	protected static Person wrap(Class<?> type, String label, String[] args) {
		Person person;
		try {
			person = (Person) type.getDeclaredConstructor(String.class)
					.newInstance(label);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		try {
			person.fillVertexInfo(args);
		} catch (IllegalArgumentException e) {
			return null;
		}
		person.checkRep();
		return (Person) type.cast(person);
	}

	/**
	 * Generate a Person Instance from given arguments
	 * 
	 * @param type
	 *            Class of Instance to generate
	 * @param name
	 *            Person's name
	 * @param isMale
	 *            if the Person is Male, pass true; else pass false
	 * @param age
	 *            age of the person
	 * @return return generated Person Instance if age >= 0. else return null.
	 */
	protected static Person wrap(Class<?> type, String name, boolean isMale,
			int age) {
		String[] infos = new String[2];
		if (isMale)
			infos[0] = "M";
		else
			infos[0] = "F";
		infos[1] = String.valueOf(age);
		return wrap(type, name, infos);
	}

	@Override
	public List<String> getVertexInfo() {
		List<String> res = new ArrayList<>();
		if (isMale())
			res.add("M");
		else
			res.add("F");
		res.add(String.valueOf(getAge()));
		return res;
	}
}
