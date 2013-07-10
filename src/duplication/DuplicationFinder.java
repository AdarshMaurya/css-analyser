package duplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import CSSModel.AtomicSelector;
import CSSModel.Declaration;
import CSSModel.Selector;
import CSSModel.StyleSheet;

/**
 * This class is responsible for finding various types of duplications in a
 * style sheet, kept in a <{@link StyleSheet} object in the memory.
 * 
 * @author Davood Mazinanian
 */
public class DuplicationFinder {

	private StyleSheet stylesheet;

	public DuplicationFinder(StyleSheet stylesheet) {
		this.stylesheet = stylesheet;
	}

	/**
	 * This method finds the selectors which are repetitive in the list of all
	 * selectors. Although the duplicated selectors are not necessary because we
	 * have grouping in CSS.
	 * 
	 * @return An object of {@link DuplicationsList}
	 */
	public DuplicationsList findIdenticalSelectors() {

		DuplicationsList duplicationList = new DuplicationsList();

		/*
		 * This is the list of the indices of already visited selectors I have
		 * used the indices so we won't rely on the equals() method of the
		 * selectors. Also, I am using a HashSet to get a constant time
		 * search.
		 */
		Set<Integer> visited = new HashSet<>();

		List<AtomicSelector> allAtomicSelectors = stylesheet.getAllAtomicSelectors();

		// So start from the first selector
		int currentSelectorIndex = -1;
		while (++currentSelectorIndex < allAtomicSelectors.size()) {

			AtomicSelector currentSelector = allAtomicSelectors.get(currentSelectorIndex);

			if (visited.contains(currentSelectorIndex))
				continue;
			/*
			 * Start checking from next index in the temporary list. First we
			 * assume that a duplication has just happened. so we create the
			 * appropriate object for it (considering that we only can have
			 * one duplication object of every selector).
			 */
			IdenticalSelectors duplication = new IdenticalSelectors(currentSelector);

			int checkingSelectorIndex =  currentSelectorIndex;
			
			while (++checkingSelectorIndex < allAtomicSelectors.size()) {
				
				AtomicSelector checkingSelector = allAtomicSelectors.get(checkingSelectorIndex);

				if (currentSelector.equals(checkingSelector)) {
					// So it seems that we have found a duplication in selectors
					duplication.addSelector(checkingSelector);
					visited.add(checkingSelectorIndex);
				}

			}
			// If we have more than one occurrence of one selector in the list:
			if (duplication.getNumberOfOccurrences() > 1) {
				duplicationList.addDuplication(duplication);
			}

		}

		return duplicationList;
	}

	/**
	 * This method finds the cases in which the property and value
	 * (i.e. the declarations) are the same across different selectors.
	 * @return An object of {@link DuplicationsList}
	 */
	public DuplicationsList findIdenticalDeclarations() {
		
		DuplicationsList duplicationsList = new DuplicationsList();
		
		// Lets get all the declarations
		List<Declaration> allDeclarations = stylesheet.getAllDeclarations();
		
		// We don't want to repeat.
		Set<Integer> visitedDeclarations = new HashSet<>();
		
		IdenticalDeclarations duplication = new IdenticalDeclarations();
		
		int currentDecIndex = -1;		
		while (++currentDecIndex < allDeclarations.size()) {
			
			Declaration currentDeclaration = allDeclarations.get(currentDecIndex);

			// Don't repeat
			if (visitedDeclarations.contains(currentDecIndex))
				continue;
			
			/* Only when add the current duplication to the duplications list
			 * that we have really found a duplication
			 */
			boolean mustAdd = false;
			
			int checkingDecIndex = currentDecIndex;
		
			List<Declaration> currentEqualDeclarations = new ArrayList<Declaration>();
			currentEqualDeclarations.add(currentDeclaration);
			
			while (++checkingDecIndex < allDeclarations.size()) {

				Declaration checkingDeclaration = allDeclarations.get(checkingDecIndex);

				if (currentDeclaration.equals(checkingDeclaration)) {
					//We add the checkingDeclaration, it will add the Selector itself.
					currentEqualDeclarations.add(checkingDeclaration);
					visitedDeclarations.add(checkingDecIndex);
					mustAdd = true;
				}
				
			}
			
			if (duplication.hasAllSelectorsForADuplication(currentEqualDeclarations)) {
				duplication.addAllDeclarations(currentEqualDeclarations);
				mustAdd = false;
			} else {
				duplication = new IdenticalDeclarations();
				duplication.addAllDeclarations(currentEqualDeclarations);
			}

			// Only if we have at least one declaration in the list (at list one duplication)
			if (mustAdd)
				duplicationsList.addDuplication(duplication);
			
		}
		return duplicationsList;
	}

	/**
	 * Finds all the duplications, where only values for different 
	 * properties across different selectors are exactly the same.
	 * @return An object of {@link DuplicationsList}
	 */
	// TODO: Consider the cases such as red and #F00 and #FF0000
	// Consider very carefully about different variations in the values.
	public DuplicationsList findIdenticalValues() {
		
		DuplicationsList duplicationList = new DuplicationsList();

		// Get a list of all declarations
		List<Declaration> allDeclarations = stylesheet.getAllDeclarations();
		
		// Yeah don't repeat for different declaration
		Set<Integer> visitedDeclarations = new HashSet<>();
		
		int currentDecIndex = -1;
		
		while (++currentDecIndex < allDeclarations.size()) {
			
			Declaration currentDeclaration = allDeclarations.get(currentDecIndex);
			
			String currentValue = currentDeclaration.getValue();

			if (visitedDeclarations.contains(currentDecIndex))
				continue;
			
			IdenticalValues duplication = new IdenticalValues();
			
			duplication.addDeclaration(currentDeclaration);

			// Start from the next index
			int checkingDecIndex = currentDecIndex;
			while (++checkingDecIndex < allDeclarations.size()) {

				Declaration checkingDeclaration = allDeclarations.get(checkingDecIndex);
				String checkingValue = checkingDeclaration.getValue();
				
				if (currentValue.equals(checkingValue)) {
					// Found the desirable duplication
					duplication.addDeclaration(checkingDeclaration);
					visitedDeclarations.add(checkingDecIndex);
				}
			}
			
			if (duplication.getNumberOfDeclarations() > 1) 
				duplicationList.addDuplication(duplication);

		}

		return duplicationList;
	}

	/**
	 * Finds the overriden values.
	 * @return
	 */
	public DuplicationsList findOverridenValues(DuplicationsList identicalSelectorsDuplication) {
		return findIdenticalSelectorAndDeclaration(true, identicalSelectorsDuplication);
	}

	/**
	 * This method finds the cases in which the selector, property and values
	 * are all the same.
	 * @return
	 */
	public DuplicationsList findIdenticalEffects(DuplicationsList identicalSelectorsDuplication) {
		return findIdenticalSelectorAndDeclaration(false, identicalSelectorsDuplication);
	}
	
	/**
	 * Checks for identical selectors, and based on the onlyCheckProperties
	 * value, checks to see whether there is an equal declaration (false value for onlyCheckProperties)
	 * or an equal property (true value for onlyCheckProperties).
	 * @param onlyCheckProperties Only check for properties to be the same
	 * @param duplicatedSelectors The result of {@link #findIdenticalSelectors()} method
	 * @return An object of DuplicationList
	 */
	public DuplicationsList findIdenticalSelectorAndDeclaration(boolean onlyCheckProperties, DuplicationsList duplicatedSelectors) {
		
		DuplicationsList duplicationList = new DuplicationsList();

		if (duplicatedSelectors == null)
			duplicatedSelectors = findIdenticalSelectors();
		
		for (Duplication selectorDuplication : duplicatedSelectors) {
			
			// This is a list of identical selectors
			List<Selector> identicalSelectors = ((IdenticalSelectors)selectorDuplication).getListOfSelectors();
			
			Duplication duplication;
			
			if (onlyCheckProperties) {
				duplication = new OverriddenProperties(identicalSelectors.get(0));
			} else {
				duplication = new IdenticalEffects(identicalSelectors.get(0));
			}
			boolean mustAdd = false;

			int currentSelectorIndex = -1;
			while (++currentSelectorIndex < identicalSelectors.size()) {
				
				Selector currentSelector = identicalSelectors.get(currentSelectorIndex);
				List<Declaration> currentDeclarations = currentSelector.getAllDeclarations();
				
				/* For each declaration for the current selector, 
				 * check all selectors to see whther they have the same declarations or not
				 */
				for (Declaration currentDeclaration : currentDeclarations) {
					
					List<Declaration> currentEqualDeclarations = new ArrayList<>();
					currentEqualDeclarations.add(currentDeclaration);
				
					int checkingSelectorIndex = currentSelectorIndex;
					while (++checkingSelectorIndex < identicalSelectors.size()) {
						Selector checkingSelector = identicalSelectors.get(checkingSelectorIndex);
						List<Declaration> checkingDeclarations = checkingSelector.getAllDeclarations();
	
						if (onlyCheckProperties) {
							for (Declaration checkingDeclaration : checkingDeclarations) {
								if (currentDeclaration.getProperty().equals(checkingDeclaration.getProperty())
										&& currentDeclaration.getValue().compareTo(checkingDeclaration.getValue()) != 0) {
									currentEqualDeclarations.add(checkingDeclaration);
								}
							}

						} else {

							int index = checkingDeclarations.indexOf(currentDeclaration);

							if (index >= 0) {

								Declaration checkingDeclaration = checkingDeclarations.get(index); 
								currentEqualDeclarations.add(checkingDeclaration);
							}
						}

					}
					
					// If we have found some duplications, add the declarations.
					if (currentEqualDeclarations.size() > 1) {
						if (onlyCheckProperties) {
							((OverriddenProperties)duplication).addAllDeclarations(currentEqualDeclarations);
						}
						else {
							((IdenticalEffects)duplication).addAllDeclarations(currentEqualDeclarations);
						}
						mustAdd = true;
					}
				}
			}
			
			
			if (mustAdd)
				duplicationList.addDuplication(duplication);
			
			
		}

		return duplicationList;
	}

	/*private boolean isIn(Selector selectorToBeCheckedIn, Selector selectorToFind) {
		if (selectorToBeCheckedIn instanceof GroupedSelectors) {
			GroupedSelectors group2 = (GroupedSelectors) selectorToBeCheckedIn;
			if (group2.contains(selectorToFind))
				return true;
		} else if (selectorToBeCheckedIn instanceof AtomicSelector) {
			return selectorToFind.equals(selectorToBeCheckedIn);
		}
		return false;
	}*/

}