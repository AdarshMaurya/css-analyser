package CSSModel.selectors;

import java.util.ArrayList;
import java.util.List;

import CSSModel.declaration.Declaration;
import CSSModel.media.AtomicMedia;
import CSSModel.media.Media;

public abstract class Selector {
	
	protected int lineNumber;
	protected int columnNumber;
	protected Media parentMedia;
	protected List<Declaration> declarations;
	protected int specificityOfSelector;
	
	public int getSpecificity() {
		return specificityOfSelector;
	}

	public void setSpecificity(int specificity) {
		specificityOfSelector = specificity;
	}

	public Selector() {
		this(-1, -1);
	}

	public Selector(int fileLineNumber, int fileColNumber) {
		lineNumber = fileLineNumber;
		columnNumber = fileColNumber;
		declarations = new ArrayList<>();
		
	}
	public void addCSSRule(Declaration rule) {
		declarations.add(rule);
	}

	public List<Declaration> getDeclarations() {
		return declarations;
	}
	
//	public Collection<Declaration> getAllDeclarationsHS() {
//		return declarationsHashSet;
//	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int linNumber) {
		lineNumber = linNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int fileColumnNumber) {
		columnNumber = fileColumnNumber;
	}

	public Media getMedia() {
		return parentMedia;
	}

	public void setMedia(Media media) {
		parentMedia = media;
	}

	public void setMedia(String name) {
		setMedia(new AtomicMedia(name));
	}
}