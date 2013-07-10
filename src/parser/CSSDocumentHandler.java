package parser;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

import org.w3c.flute.parser.SelectorListImpl;
import org.w3c.flute.parser.selectors.AndConditionImpl;
import org.w3c.flute.parser.selectors.AttributeConditionImpl;
import org.w3c.flute.parser.selectors.BeginHyphenAttributeConditionImpl;
import org.w3c.flute.parser.selectors.ChildSelectorImpl;
import org.w3c.flute.parser.selectors.ClassConditionImpl;
import org.w3c.flute.parser.selectors.ConditionalSelectorImpl;
import org.w3c.flute.parser.selectors.DescendantSelectorImpl;
import org.w3c.flute.parser.selectors.DirectAdjacentSelectorImpl;
import org.w3c.flute.parser.selectors.ElementSelectorImpl;
import org.w3c.flute.parser.selectors.IdConditionImpl;
import org.w3c.flute.parser.selectors.LangConditionImpl;
import org.w3c.flute.parser.selectors.OneOfAttributeConditionImpl;
import org.w3c.flute.parser.selectors.PseudoClassConditionImpl;
import org.w3c.flute.parser.selectors.PseudoElementSelectorImpl;

import CSSModel.SelectorConditionType;
import CSSModel.DescendantSelector;
import CSSModel.DirectDescendantSelector;
import CSSModel.ImmediatelyAfterSelector;
import CSSModel.AtomicMedia;
import CSSModel.AtomicSelector;
import CSSModel.Declaration;
import CSSModel.AtomicElementSelector;
import CSSModel.GroupedMedia;
import CSSModel.PseudoClass;
import CSSModel.Selector;
import CSSModel.GroupedSelectors;
import CSSModel.SelectorCondition;
import CSSModel.StyleSheet;
import CSSModel.Media;

public class CSSDocumentHandler implements DocumentHandler {

	private Selector currentSelector;
	private Media currentMedia;
	private StyleSheet styleSheet;
	private int numberOfVisitedElements = 0;

	public CSSDocumentHandler(StyleSheet styleSheet) {
		this.styleSheet = styleSheet;
	}

	@Override
	public void startDocument(InputSource arg0) throws CSSException {
	}

	@Override
	public void endDocument(InputSource arg0) throws CSSException {
	}

	@Override
	public void comment(String arg0) throws CSSException {
	}

	@Override
	public void startFontFace() throws CSSException {
	}

	@Override
	public void endFontFace() throws CSSException {
	}

	@Override
	public void startPage(String arg0, String arg1) throws CSSException {
	}

	@Override
	public void endPage(String arg0, String arg1) throws CSSException {
	}

	@Override
	public void ignorableAtRule(String arg0) throws CSSException {
		// TODO the rules starting with @ which are ignorable
	}

	@Override
	public void importStyle(String arg0, SACMediaList arg1, String arg2)
			throws CSSException {
		// TODO We must load the style and parse it
	}

	@Override
	public void namespaceDeclaration(String arg0, String arg1)
			throws CSSException {
	}

	@Override
	public void startMedia(SACMediaList arg0) throws CSSException {
		if (arg0.getLength() == 1) {
			currentMedia = new AtomicMedia(arg0.item(0));
		} else {
			GroupedMedia groupedMedia = new GroupedMedia();
			for (int i = 0; i < arg0.getLength(); i++)
				groupedMedia.addMedia(new AtomicMedia(arg0.item(i)));
			currentMedia = groupedMedia;
		}
	}

	@Override
	public void endMedia(SACMediaList arg0) throws CSSException {
		currentMedia = null;
	}

	@Override
	public void startSelector(SelectorList arg0) throws CSSException {

		Locator loc = ((SelectorListImpl) arg0).getLocator();
		numberOfVisitedElements++;
		if (arg0.getLength() > 1) {
			GroupedSelectors groupedSelectors = new GroupedSelectors(
					loc.getLineNumber(), loc.getColumnNumber());
			for (int i = 0; i < arg0.getLength(); i++) {
				try {
					AtomicSelector newAtomicSelector = getSelector(arg0.item(i));
					newAtomicSelector.setLineNumber(loc.getLineNumber());
					newAtomicSelector.setColumnNumber(loc.getColumnNumber());

					if (currentMedia != null)
						newAtomicSelector.setMedia(currentMedia);
					groupedSelectors.add(newAtomicSelector);
				} catch (Exception ex) {
					// TODO: logger.severe
					System.out.println(ex);
				}
			}
			currentSelector = groupedSelectors;
			styleSheet.addSelector(groupedSelectors);
		} else {
			try {
				currentSelector = getSelector(arg0.item(0));
				currentSelector.setLineNumber(loc.getLineNumber());
				currentSelector.setColumnNumber(loc.getColumnNumber());
				if (currentMedia != null)
					currentSelector.setMedia(currentMedia);
				styleSheet.addSelector(currentSelector);
			} catch (Exception ex) {
				// TODO: logger.severe..
				System.out.println(ex);
			}
		}
		//System.out.println(currentSelector);

	}

	@Override
	public void endSelector(SelectorList arg0) throws CSSException {
		currentSelector = null;
	}

	@Override
	public void property(String arg0, LexicalUnit arg1, boolean arg2)
			throws CSSException {
		
		property(arg0, arg1, arg2, null);	
	}
	
	public void property(String arg0, LexicalUnit arg1, boolean arg2, Locator locator)
			throws CSSException {

		Declaration newDeclaration = new Declaration(arg0, getValue(arg1),
				currentSelector, locator.getLineNumber(), locator.getColumnNumber(), arg2);

		if (currentSelector != null) 
				currentSelector.addCSSRule(newDeclaration);
		
	}
	

	// Adapted from GWT
	// http://google-web-toolkit.googlecode.com/svn-history/r7441/trunk/user/src/com/google/gwt/resources/css/GenerateCssAst.java
	private AtomicSelector getSelector(org.w3c.css.sac.Selector selector)
			throws Exception {
		// if (selector instanceof CharacterDataSelector) {
		// Unimplemented in flute?
		// }
		if (selector instanceof ElementSelectorImpl) {

			ElementSelectorImpl sacElementSelector = (ElementSelectorImpl) selector;
			AtomicElementSelector atomicElementSelector = new AtomicElementSelector();
			String elementName;
			if (sacElementSelector.getLocalName() == null) {
				elementName = "*";
			} else {
				elementName = sacElementSelector.getLocalName();
			}
			atomicElementSelector.setSelectedElementName(elementName);

			return atomicElementSelector;

		} else if (selector instanceof DescendantSelectorImpl) {

			DescendantSelectorImpl sacDescendantSelector = (DescendantSelectorImpl) selector;
			AtomicSelector parentAtomicSelector = getSelector(sacDescendantSelector
					.getAncestorSelector());
			AtomicSelector childAtomicSelector = getSelector(sacDescendantSelector
					.getSimpleSelector());
			DescendantSelector s = new DescendantSelector(parentAtomicSelector,
					childAtomicSelector);

			return s;

		} else if (selector instanceof ChildSelectorImpl) {
			/*
			 * In fact we have three different occasions wherein this happens: A
			 * > B :first-letter :first-line
			 */

			ChildSelectorImpl sacChildSelectorImpl = (ChildSelectorImpl) selector;

			AtomicSelector parentAtomicSelector = getSelector(sacChildSelectorImpl
					.getAncestorSelector());
			AtomicSelector childAtomicSelector = getSelector(sacChildSelectorImpl
					.getSimpleSelector());

			AtomicSelector selectorToReturn;

			if (sacChildSelectorImpl.getSimpleSelector() instanceof PseudoElementSelectorImpl) {
				selectorToReturn = parentAtomicSelector;
				PseudoElementSelectorImpl pseudoClass = (PseudoElementSelectorImpl) sacChildSelectorImpl
						.getSimpleSelector();

				((AtomicElementSelector) selectorToReturn)
						.addPseudoClass(new PseudoClass(pseudoClass
								.getLocalName()));

			} else {
				selectorToReturn = new DirectDescendantSelector(
						parentAtomicSelector, childAtomicSelector);
			}

			return selectorToReturn;

		} else if (selector instanceof PseudoElementSelectorImpl) {
			PseudoElementSelectorImpl pseudoElementSelector = (PseudoElementSelectorImpl) selector;
			AtomicElementSelector atomicElementSelector = new AtomicElementSelector();
			atomicElementSelector.addPseudoClass(new PseudoClass(
					pseudoElementSelector.getLocalName()));
			return atomicElementSelector;
		} else if (selector instanceof DirectAdjacentSelectorImpl) {

			DirectAdjacentSelectorImpl sacDirectAdjacentSelector = (DirectAdjacentSelectorImpl) selector;
			AtomicSelector parentSelector = getSelector(sacDirectAdjacentSelector
					.getSelector());
			AtomicSelector childSelector = getSelector(sacDirectAdjacentSelector
					.getSiblingSelector());
			return new ImmediatelyAfterSelector(parentSelector, childSelector);

		} else if (selector instanceof ConditionalSelectorImpl) {

			ConditionalSelector sacConditionalSelector = (ConditionalSelectorImpl) selector;
			AtomicElementSelector atomicElementSelector = (AtomicElementSelector) getSelector(sacConditionalSelector
					.getSimpleSelector());
			getConditions(sacConditionalSelector.getCondition(),
					atomicElementSelector);
			return atomicElementSelector;
		} else {
			System.out.println(selector);
			throw new Exception("Selector not supported");
		}
	}

	private void getConditions(Condition sacCondition,
			AtomicElementSelector atomicElementSelector) throws Exception {
		if (sacCondition instanceof AndConditionImpl) {
			AndConditionImpl andCondition = (AndConditionImpl) sacCondition;
			getConditions(andCondition.getFirstCondition(),
					atomicElementSelector);
			getConditions(andCondition.getSecondCondition(),
					atomicElementSelector);

		} else if (sacCondition instanceof ClassConditionImpl) {
			ClassConditionImpl classCond = (ClassConditionImpl) sacCondition;
			atomicElementSelector.addClassName(classCond.getValue());
		} else if (sacCondition instanceof PseudoClassConditionImpl) {
			PseudoClassConditionImpl pseudoCond = (PseudoClassConditionImpl) sacCondition;
			atomicElementSelector.addPseudoClass(new PseudoClass(pseudoCond
					.getValue()));
		} else if (sacCondition instanceof IdConditionImpl) {
			IdConditionImpl c = (IdConditionImpl) sacCondition;
			atomicElementSelector.setIDName(c.getValue());
		} else if (sacCondition instanceof LangConditionImpl) {
			LangConditionImpl langCondition = (LangConditionImpl) sacCondition;
			atomicElementSelector.addPseudoClass(new PseudoClass("lang",
					langCondition.getLang()));
		} else if (sacCondition instanceof AttributeConditionImpl) {
			AttributeConditionImpl attributeConditionImpl = (AttributeConditionImpl) sacCondition;
			SelectorCondition selectorCondition = new SelectorCondition(
					attributeConditionImpl.getLocalName());
			String value = attributeConditionImpl.getValue();
			if (value != null) {
				selectorCondition
						.setConditionType(SelectorConditionType.VALUE_EQUALS_EXACTLY);
				selectorCondition.setConditionValue(attributeConditionImpl
						.getValue());
			}
			atomicElementSelector.addCondition(selectorCondition);
		} else if (sacCondition instanceof OneOfAttributeConditionImpl) {
			OneOfAttributeConditionImpl oneOfAttrCondition = (OneOfAttributeConditionImpl) sacCondition;
			SelectorCondition selectorCondition = new SelectorCondition(
					oneOfAttrCondition.getLocalName(),
					oneOfAttrCondition.getValue(),
					SelectorConditionType.VALUE_CONTAINS_WORD);
			atomicElementSelector.addCondition(selectorCondition);
		} else if (sacCondition instanceof BeginHyphenAttributeConditionImpl) {
			BeginHyphenAttributeConditionImpl oneOfAttrCondition = (BeginHyphenAttributeConditionImpl) sacCondition;
			SelectorCondition selectorCondition = new SelectorCondition(
					oneOfAttrCondition.getLocalName(),
					oneOfAttrCondition.getValue(),
					SelectorConditionType.VALUE_STARTING_WITH);
			atomicElementSelector.addCondition(selectorCondition);
		} else {
			System.out.println(sacCondition);
			throw new Exception("Condition not supported");
		}
	}

	public int getNumberOfVisitedSelectors() {
		return numberOfVisitedElements;
	}

	private int getRgbComponentValue(LexicalUnit color) {
		switch (color.getLexicalUnitType()) {
		case LexicalUnit.SAC_INTEGER:
			return Math.min(color.getIntegerValue(), 255);
		case LexicalUnit.SAC_PERCENTAGE:
			return (int) Math.min(color.getFloatValue() * 255, 255);
		default:
			throw new CSSException(CSSException.SAC_SYNTAX_ERR,
					"RGB component value must be integer or percentage, was "
							+ color, null);
		}
	}

	private String colorValue(LexicalUnit colors) {
		LexicalUnit red = colors;
		int r = getRgbComponentValue(red);
		LexicalUnit green = red.getNextLexicalUnit().getNextLexicalUnit();
		int g = getRgbComponentValue(green);
		LexicalUnit blue = green.getNextLexicalUnit().getNextLexicalUnit();
		int b = getRgbComponentValue(blue);

		String sr = Integer.toHexString(r);
		if (sr.length() == 1) {
			sr = "0" + sr;
		}

		String sg = Integer.toHexString(g);
		if (sg.length() == 1) {
			sg = "0" + sg;
		}

		String sb = Integer.toHexString(b);
		if (sb.length() == 1) {
			sb = "0" + sb;
		}

		// #AABBCC --> #ABC
		if (sr.charAt(0) == sr.charAt(1) && sg.charAt(0) == sg.charAt(1)
				&& sb.charAt(0) == sb.charAt(1)) {
			sr = sr.substring(1);
			sg = sg.substring(1);
			sb = sb.substring(1);
		}

		return "#" + String.valueOf(sr) + String.valueOf(sg)
				+ String.valueOf(sb);
	}

	private String extractValueOf(LexicalUnit value) {
		String accumulator = "";
		do {
			accumulator += getValue(value) + ", ";
			value = value.getNextLexicalUnit();
		} while (value != null);
		return accumulator.substring(0, accumulator.length() - 3);
	}

	private String getValue(LexicalUnit value) {
		switch (value.getLexicalUnitType()) {
		case LexicalUnit.SAC_ATTR:
			return "attr(" + value.getStringValue() + ")";
		case LexicalUnit.SAC_IDENT:
			return /* escapeIdent */(value.getStringValue());
		case LexicalUnit.SAC_STRING_VALUE:
			return value.getStringValue();
		case LexicalUnit.SAC_RGBCOLOR:
			// flute models the commas as operators so no separator needed
			return colorValue(value.getParameters());
		case LexicalUnit.SAC_INTEGER:
			return String.valueOf(value.getIntegerValue());
		case LexicalUnit.SAC_REAL:
			return String.valueOf(value.getFloatValue());
		case LexicalUnit.SAC_CENTIMETER:
		case LexicalUnit.SAC_DEGREE:
		case LexicalUnit.SAC_DIMENSION:
		case LexicalUnit.SAC_EM:
		case LexicalUnit.SAC_EX:
		case LexicalUnit.SAC_GRADIAN:
		case LexicalUnit.SAC_HERTZ:
		case LexicalUnit.SAC_KILOHERTZ:
		case LexicalUnit.SAC_MILLIMETER:
		case LexicalUnit.SAC_MILLISECOND:
		case LexicalUnit.SAC_PERCENTAGE:
		case LexicalUnit.SAC_PICA:
		case LexicalUnit.SAC_PIXEL:
		case LexicalUnit.SAC_POINT:
		case LexicalUnit.SAC_RADIAN:
		case LexicalUnit.SAC_SECOND:
			return String.valueOf(value.getFloatValue()
					+ value.getDimensionUnitText());
		case LexicalUnit.SAC_URI:
			return "url(" + value.getStringValue() + ")";
		case LexicalUnit.SAC_OPERATOR_COMMA:
			return ",";
		case LexicalUnit.SAC_COUNTER_FUNCTION:
		case LexicalUnit.SAC_COUNTERS_FUNCTION:
		case LexicalUnit.SAC_FUNCTION: 
			return value.getFunctionName() + "("
					+ extractValueOf(value.getParameters()) + ")";
		case LexicalUnit.SAC_INHERIT:
			return "inherit";
		case LexicalUnit.SAC_OPERATOR_EXP:
			return "^";
		case LexicalUnit.SAC_OPERATOR_GE:
			return ">=";
		case LexicalUnit.SAC_OPERATOR_GT:
			return (">");
		case LexicalUnit.SAC_OPERATOR_LE:
			return ("<=");
		case LexicalUnit.SAC_OPERATOR_LT:
			return ("<");
		case LexicalUnit.SAC_OPERATOR_MINUS:
			return ("-");
		case LexicalUnit.SAC_OPERATOR_MOD:
			return ("%");
		case LexicalUnit.SAC_OPERATOR_MULTIPLY:
			return ("*");
		case LexicalUnit.SAC_OPERATOR_PLUS:
			return ("+");
		case LexicalUnit.SAC_OPERATOR_SLASH:
			return ("/");
		case LexicalUnit.SAC_OPERATOR_TILDE:
			return ("~");
		case LexicalUnit.SAC_RECT_FUNCTION: {
			// Just return this as a String
			return "rect(" + extractValueOf(value.getParameters()) + ")";
		}
		case LexicalUnit.SAC_SUB_EXPRESSION:
			// Should have been taken care of by our own traversal
		case LexicalUnit.SAC_UNICODERANGE:
			// Cannot be expressed in CSS2
		}
		throw new RuntimeException("Unhandled LexicalUnit type "
				+ value.getLexicalUnitType());
	}
}