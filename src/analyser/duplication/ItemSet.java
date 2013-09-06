package analyser.duplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import CSSModel.declaration.Declaration;
import CSSModel.selectors.Selector;

/**
 * This class keeps the data of a itemset, in addition to its support 
 * In our definition, every itemset is a set of declarations and
 * support means the number of selectors that have all these declarations.
 * In fact, instead of keeping the support as a pure percentage or number of supports,
 * we keep the selectors for further uses. 
 * 
 * @author Davood Mazinanian
 *
 */
public class ItemSet implements Iterable<Declaration>, Cloneable {
	
	private final Set<Declaration> itemsetField;
	private final List<Selector> supports;
	
	public ItemSet(Set<Declaration> declarations, List<Selector> selectorsList) {
		itemsetField = declarations;
		supports = selectorsList;
	}
	
	public int getSupport() {
		return supports.size();
	}
	
	
	public Collection<Declaration> getItemSet() {
		return itemsetField;
	}
	
	public Collection<Selector> getSelectors() {
		return supports;
	}

	@Override
	public Iterator<Declaration> iterator() {
		return itemsetField.iterator();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;

		ItemSet otherObj = (ItemSet)obj; 
		return itemsetField.equals(otherObj.itemsetField);
	}
	
	@Override
	public int hashCode() {
		return itemsetField.hashCode();
	}

	public boolean itemsEqual(Collection<Declaration> set) {
		return itemsetField.size() == set.size() && itemsetField.containsAll(set);
	}
	
	@Override
	protected ItemSet clone() {
		return new ItemSet(new HashSet<Declaration>(itemsetField), new ArrayList<Selector>(supports));
	}
}
