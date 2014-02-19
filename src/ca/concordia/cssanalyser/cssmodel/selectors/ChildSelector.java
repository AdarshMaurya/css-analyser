package ca.concordia.cssanalyser.cssmodel.selectors;

/**
 * Selector1 > Selector2
 * @author Davood Mazinanian
 */
public class ChildSelector extends DescendantSelector {

	public ChildSelector(BaseSelector parent, SimpleSelector child) {
		super(parent, child);
	}
	
	@Override
	public String toString() {
		return parentSelector + " > " + childSelector;
	}

	@Override
	public ChildSelector clone() {
		ChildSelector newOne = new ChildSelector(getParentSelector().clone(), getChildSelector().clone());
		newOne.setLineNumber(lineNumber);
		newOne.setColumnNumber(columnNumber);
		newOne.addMediaQueryLists(mediaQueryLists);
		return newOne;
	}
	
}
