package ca.concordia.cssanalyser.cssmodel.selectors;
/**
 * Represents CSS3 pseudo elements like ::selector.
 * Have a look at {@link PseudoClass <code>PseudoClass</code>} for 
 * more information
 * 
 * @author Davood Mazinanian
 * 
 */
public class PseudoElement {
	
	private String name;

	public PseudoElement(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PseudoElement other = (PseudoElement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public PseudoElement clone() {
		return new PseudoElement(name);
	}

}
