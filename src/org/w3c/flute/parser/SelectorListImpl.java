/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id: SelectorListImpl.java,v 1.1 2000/08/07 01:16:21 plehegar Exp $
 */
package org.w3c.flute.parser;

import org.w3c.css.sac.Locator;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.Selector;

/**
 * @version $Revision: 1.1 $
 * @author  Philippe Le Hegaret
 */
public class SelectorListImpl implements SelectorList {

    Selector[] selectors = new Selector[5];
    int      current;

    public Selector item(int index) {
	if ((index < 0) || (index >= current)) {
	    return null;
	}
	return selectors[index];
    }

    public Selector itemSelector(int index) {
	if ((index < 0) || (index >= current)) {
	    return null;
	}
	return selectors[index];
    }

    public int getLength() {
	return current;
    }

    void addSelector(Selector selector) {
	if (current == selectors.length) {
	    Selector[] old = selectors;
	    selectors = new Selector[old.length + old.length];
	    System.arraycopy(old, 0, selectors, 0, old.length);
	}
	selectors[current++] = selector;
    }
    
    Locator locator;
    public void setLocator(Locator locator) {
    	this.locator = locator;
    }
    
    public Locator getLocator() {
    	return this.locator;
    }
}