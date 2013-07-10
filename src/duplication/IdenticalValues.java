package duplication;

import java.util.ArrayList;
import java.util.List;

import CSSModel.Declaration;

public class IdenticalValues extends Duplication {

	private final List<Declaration> declarations;

	public IdenticalValues() {
		super(DuplicationType.IDENTICAL_VALUE);
		declarations = new ArrayList<>();
	}

	/**
	 * Returns that, for which value we are collecting
	 * the duplications
	 * @return The value for which we are collecting the duplications
	 */
	public String getForWhichValue() {
		if (declarations.get(0) != null)
			return declarations.get(0).getValue();
		return null;
	}
	
	/**
	 * Adds a declaration (so its line number and corresponding selector)
	 * to the list of declarations for current duplication.
	 * @param declaration
	 */
	public void addDeclaration(Declaration declaration) {
		declarations.add(declaration);
	}

	@Override
	public String toString() {
		String string = "Duplication for value: \n" + getForWhichValue()
				+ " in the following lines: \n";
		for (Declaration declaration : declarations)
			string += "\t" + declaration.getLineNumber() + " : " + declaration.getColumnNumber() + "\n";
		return string;
	}

	/**
	 * Returns the number of declarations
	 * @return Returns the number of declarations
	 */
	public int getNumberOfDeclarations() {
		return declarations.size();
	}
	
	// TODO Consider implementing equals() and hashCode()
}