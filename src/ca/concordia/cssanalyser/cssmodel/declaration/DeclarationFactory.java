package ca.concordia.cssanalyser.cssmodel.declaration;

import java.util.List;

import org.slf4j.Logger;

import ca.concordia.cssanalyser.app.FileLogger;
import ca.concordia.cssanalyser.cssmodel.declaration.value.DeclarationValue;
import ca.concordia.cssanalyser.cssmodel.declaration.value.ValueType;
import ca.concordia.cssanalyser.cssmodel.selectors.Selector;


/**
 * A factory class to return {@link Declaration} or {@link ShorthandDeclaration}
 * based on the property name
 * 
 * @author Davood Mazinanian
 *	
 */
public class DeclarationFactory {
	
	private static final Logger LOGGER = FileLogger.getLogger(DeclarationFactory.class);

	/**
	 * Returns {@link Declaration} or {@link ShorthandDeclaration}, based on the 
	 * property name.
	 * 
	 * @param propertyName
	 * @param values
	 * @param belongsTo
	 * @param offset
	 * @param fileColNumber
	 * @param important
	 * @return
	 */
	public static Declaration getDeclaration(String propertyName, List<DeclarationValue> values, Selector belongsTo, int offset, int length, boolean important, boolean addMissingValues) {
		if (MultiValuedDeclaration.isMultiValuedProperty(propertyName)) 
			return new MultiValuedDeclaration(propertyName, values, belongsTo, offset, length, important, addMissingValues);
		if (ShorthandDeclaration.isShorthandProperty(propertyName))
			return new ShorthandDeclaration(propertyName, values, belongsTo, offset, length, important, addMissingValues);
		else {
			DeclarationValue declarationValue = values.get(0);
			if (values.size() > 1) {
				String concatanated = "";
				for (DeclarationValue dv : values)
					concatanated += dv.getValue();
				declarationValue = new DeclarationValue(concatanated, ValueType.OTHER);
				LOGGER.warn(String.format("Multiple values for single-valued property '%s' are given. All the values are concatanated to make a single value '%s'. Values are %s",
						propertyName, concatanated, values.toString()));
			}
			return new SingleValuedDeclaration(propertyName, declarationValue, belongsTo, offset, length, important);
		}
	}
	
}
