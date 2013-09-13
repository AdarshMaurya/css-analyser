package CSSModel.declaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import CSSModel.declaration.value.DeclarationValue;
import CSSModel.declaration.value.ValueType;
import CSSModel.selectors.Selector;

/**
 * Representation of shorthand declarations
 * @author Davood Mazinanian
 *
 */
public class ShorthandDeclaration extends Declaration {

	private Map<String, Declaration> individualDeclarations;
	
	private static final Map<String, Set<String>> shorthandProperties = new HashMap<>();
	
	static {
		initializeShorthandsMap();
	}

	public ShorthandDeclaration(String propertyName, List<DeclarationValue> values, Selector belongsTo, int fileLineNumber, int fileColNumber, boolean important) {
		super(propertyName, values, belongsTo, fileLineNumber, fileColNumber, important);
		if (individualDeclarations == null)
			individualDeclarations =  new HashMap<>();
	}
	
	private static void initializeShorthandsMap() {
		
		//addShorthandProperty("animation", )
		
		addShorthandProperty("background", "background-image",
										   "background-repeat",
										   "background-attachement",
										   "background-origin",
										   "background-clip",
										   "background-position",
										   "background-size",
										   "background-color");
		
		addShorthandProperty("border", "border-color",
									   "border-width",
									   "border-style");
		
		addShorthandProperty("border-bottom", "border-bottom-color",
											  "border-bottom-width",
											  "border-bottom-style");
		
		addShorthandProperty("border-left", "border-left-color",
				  							"border-left-width",
				  							"border-left-style");
		
		addShorthandProperty("border-right", "border-right-color",
			  							     "border-right-width",
				  							 "border-right-style");
		
		addShorthandProperty("border-top", "border-top-color",
				  						   "border-top-width",
				  						   "border-top-style");
		
		addShorthandProperty("border-color", "border-left-color",
											 "border-right-color",
											 "border-top-color",
											 "border-bottom-color");
		
		addShorthandProperty("border-width", "border-left-width",
											 "border-right-width",
											 "border-top-width",
											 "border-bottom-width");
		
		addShorthandProperty("border-style", "border-left-style",
											 "border-right-style",
											 "border-top-style",
											 "border-bottom-style");
		
		addShorthandProperty("outline", "outline-color",
						     "outline-width",
						     "outline-style");
		
		//addShorthandProperty("border-image", );
		//addShorthandProperty("target", );
		
		addShorthandProperty("border-radius", "border-top-left-radius",
											  "border-top-right-radius",
											  "border-bottom-right-radius",
											  "border-bottom-left-radius");
		
		addShorthandProperty("list-style", "list-style-type",
										   "list-style-position",
										   "list-style-image");
		
		addShorthandProperty("margin", "margin-left",
									   "margin-right",
									   "margin-top",
									   "margin-bottom");
		
		addShorthandProperty("column-rule", "column-rule-style",
											"column-rule-color",
											"column-rule-width");
		
		addShorthandProperty("columns", "column-width",
										"column-count");
		
		addShorthandProperty("padding", "padding-left",
									    "padding-right",
									    "padding-top",
									    "padding-bottom");
		
		addShorthandProperty("transition", "transition-duration", 
										   "transition-timing-function",
										   "transition-delay", 
										   "transition-property");
		
		addShorthandProperty("font", "font-style",
									 "font-variant",
									 "font-weight",
									 "font-stretch",
									 "font-size",
									 "line-height",
									 "font-family");
	}
	
	private static void addShorthandProperty(String shorthandPropertyName, String... individualPropertyNames) {
		shorthandProperties.put(shorthandPropertyName, new HashSet<>(Arrays.asList(individualPropertyNames)));
	}

	/**
	 * Specifies whether a property is a shorthand or not.
	 * @param property
	 * @return
	 */
	public static boolean isShorthandProperty(String property) {
		property = getNonVendorProperty(property);
		return shorthandProperties.containsKey(property);
	}
	
	/**
	 * If a property could become a part of a shorthand property, this method returns
	 * those shorthand properties. For example, border-left-color could be a part of 
	 * border-color or border-left shorthand properties. So this method would return them.
	 * If not, the returned set is empty. 
	 * @param property
	 * @return
	 */
	// TODO: Maybe consider using a BiMap
	public static Set<String> getShorthandPropertyNames(String property) {
		String nonVendorproperty = getNonVendorProperty(property);
		String prefix = "";
		if (!property.equals(nonVendorproperty))
			prefix = property.substring(0, property.indexOf(nonVendorproperty));
		Set<String> toReturn = new HashSet<>();
		for (Entry<String, Set<String>> entry : shorthandProperties.entrySet())
			if (entry.getValue().contains(nonVendorproperty)) {
				toReturn.add(prefix + entry.getKey());
			}
		return toReturn;
	}
	
	public void addIndividualDeclaration(String propertyName, DeclarationValue... values) {
		if (individualDeclarations == null)
			individualDeclarations =  new HashMap<>();
		
		List<DeclarationValue> valuesList = new ArrayList<>(Arrays.asList(values));
		
		Declaration individual = individualDeclarations.get(propertyName);
		
		if (isCommaSeparatedListOfValues && individual != null) {
			individual.getRealValues().add(new DeclarationValue(",", ValueType.SEPARATOR));
			for (DeclarationValue v : valuesList) {
				individual.getRealValues().add(v);
			}
		} else {
			individual = DeclarationFactory.getDeclaration(propertyName, valuesList, parentSelector, lineNumber, colNumber, isImportant);
		}
			
		addIndividualDeclaration(individual);
	}
	
	public void addIndividualDeclaration(Declaration declaration) {
		
		if (individualDeclarations == null)
			individualDeclarations =  new HashMap<>();

		
		/*
		 * Copy, so if we are adding a real declaration, we don't want to
		 * modify it. 
		 * 
		 */
		declaration = declaration.clone();
		
		for (DeclarationValue v : declaration.declarationValues) {
			v.setIsAMissingValue(false);
		}
		
		individualDeclarations.put(declaration.getProperty(), declaration);
		
	}
	
	public Collection<Declaration> getIndividualDeclarations() {
		return individualDeclarations.values();
	}
	
	public boolean individualDeclarationsEquivalent(ShorthandDeclaration otherDeclaration) {
		if (individualDeclarations.size() != otherDeclaration.individualDeclarations.size())
			return false;
		for (Entry<String, Declaration> entry : individualDeclarations.entrySet()) {
			Declaration otherIndividualDeclaration = otherDeclaration.individualDeclarations.get(entry.getKey());
			if (otherIndividualDeclaration != null && entry.getValue().declarationIsEquivalent(otherIndividualDeclaration)) {
				;
			} else {
				return false;
			}
		}
			
		return true;
	}

}