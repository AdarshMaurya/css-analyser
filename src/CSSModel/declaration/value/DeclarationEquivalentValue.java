/**
 * 
 */
package CSSModel.declaration.value;


/**
 * This class is used for values which have equivalent common representation.
 * This include: <br />
 * <ul>
 * 	<li>color values: black, #000, #000000, rgb(0, 0, 0), hsl(0, 0, 0), hsla(0, 0, 0, 1) are all equivalent with rgba(0, 0, 0, 1)</li>
	<li>1pc = 12pt</li>
	<li>1KHz = 1000Hz</li>
	<li>...</li>
 * </ul>
 * The {@link parser.CSSDocumentHandler <code>CSSDocumentHandler</code>} is responsible for converting these
 * values ant putting them in appropriate objects (either {@link DeclarationEquivalentValue} or {@link DeclarationEquivalentValue}
 * 
 * The main objective of having this class is for finding type II duplications
 * @see DeclarationValue, CSSDocumentHandler
 * @author Davood Mazinanian
 *
 */
public class DeclarationEquivalentValue extends DeclarationValue {

	private final String equivalentValue;
	
	/**
	 * Values that have other representations would be represented using common representation
	 * @param realValue The real value
	 * @param equivalentValue The equivalentValue in the form of common representation
	 * @see DeclarationValue
	 */
	public DeclarationEquivalentValue(String realValue, String equivalentValue, ValueType type) {
		this(realValue, equivalentValue, false, type);
	}
	
	/**
	 * Values that have other representations would be represented using common representation
	 * @param realValue The real value
	 * @param equivalentValue The equivalentValue in the form of common representation
	 * @param isMissing Must be true if the value is missing and we are adding it later.
	 * @see DeclarationValue
	 */	
	public DeclarationEquivalentValue(String realValue, String equivalentValue, boolean isMissing, ValueType type) {
		super(realValue, isMissing, type);
		this.equivalentValue = equivalentValue;
	}
	
	/**
	 * Checks the equivalency between two equivalent values.
	 * Two equivalent values are equivalent if their equivalentValue's are equal :)
	 * @param otherValue
	 * @return
	 */
	public boolean equivalent(DeclarationEquivalentValue otherValue) {
		String valueToCheck = otherValue.equivalentValue;
		if (equivalentValue == null) {
			if (valueToCheck == null)
				return true;
			else
				return false;
		}
		if ( ("left".equals(realInFileValue) && "top".equals(otherValue.realInFileValue)) ||
			 ("top".equals(realInFileValue) && "left".equals(otherValue.realInFileValue))	||
			 ("right".equals(realInFileValue) && "bottom".equals(otherValue.realInFileValue)) ||
			 ("bottom".equals(realInFileValue) && "right".equals(otherValue.realInFileValue))
			)
			return false;
		return equivalentValue.equals(valueToCheck);
	}
	
	/**
	 * Returns the equivalent value
	 * @return
	 */
	public String getEquivalentValue() {
		return equivalentValue;
	}
	
	/**
	 * Clones this object, giving a new DeclarationEquivalentValue.
	 */
	@Override
	public DeclarationEquivalentValue clone() {
		return new DeclarationEquivalentValue(realInFileValue, equivalentValue, isAMissingValue, valueType);
	}

}
