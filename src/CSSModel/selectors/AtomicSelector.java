package CSSModel.selectors;

/**
 * Represents the selectors which are not grouped
 * 
 * @author Davood Mazinanian
 * 
 */
public abstract class AtomicSelector extends Selector {

	private GroupedSelectors parentGroupSelector;

	public AtomicSelector() {
		this(null, -1, -1);
	}

	public AtomicSelector(GroupedSelectors parent) {
		this(parent, -1, -1);
	}

	public AtomicSelector(int line, int coloumn) {
		this(null, line, coloumn);
	}

	public AtomicSelector(GroupedSelectors parent, int line, int coloumn) {
		super(line, coloumn);
		parentGroupSelector = parent;
	}

	public void setParentGroupSelector(GroupedSelectors newGroup) {
		parentGroupSelector = newGroup;
	}

	public GroupedSelectors getParentGroupSelector() {
		return parentGroupSelector;
	}

	/*
	 * http://www.w3.org/TR/CSS21/cascade.html#specificity
	 * 
	 * 6.4.3 Calculating a selector's specificity
	 * 
	 * A selector's specificity is calculated as follows:
	 * 
	 * 1)	Count 1 if the declaration is from is a 'style' attribute rather than
	 * 		a rule with a selector, 0 otherwise (= a) (In HTML, values of an
	 * 		element's "style" attribute are style sheet rules. These rules have
	 * 		no selectors, so a=1, b=0, c=0, and d=0.)
	 * 
	 * 2) 	Count the number of ID attributes in the selector (= b)
	 * 3) 	Count the number of other attributes and pseudo-classes in the selector (= c)
	 * 4) 	Count the number of element names and pseudo-elements in the selector (= d)
	 *	
	 * The specificity is based only on the form of the selector. In particular, a selector of
	 * the form "[id=p33]" is counted as an attribute selector (a=0, b=0, c=1, d=0), even if 
	 * the id attribute is defined as an "ID" in the source document's DTD.
	 * 
	 * Concatenating the four numbers a-b-c-d (in a number system with a large base) gives the specificity.
	 */

	//public abstract int getSpecificity(); 

}