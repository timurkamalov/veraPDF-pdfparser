/**
 * This file is part of veraPDF Validation, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Validation is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Validation as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Validation as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.tools;

import org.verapdf.as.ASAtom;

import java.util.*;

/**
 * @author Maksim Bezrukov
 */
public class TaggedPDFRoleMapHelper {

	private static Set<String> PDF_1_4_STANDARD_ROLE_TYPES;
	private static Set<String> PDF_1_7_STANDARD_ROLE_TYPES;

	static {
		Set<String> tempSet = new HashSet<>();
		// Standard structure types for grouping elements PDF 1.4 and PDF 1.7
		tempSet.add("Document");
		tempSet.add("Part");
		tempSet.add("Art");
		tempSet.add("Sect");
		tempSet.add("Div");
		tempSet.add("BlockQuote");
		tempSet.add("Caption");
		tempSet.add("TOC");
		tempSet.add("TOCI");
		tempSet.add("Index");
		tempSet.add("NonStruct");
		tempSet.add("Private");

		// Standard structure types for paragraphlike elements PDF 1.4 and PDF 1.7
		tempSet.add("H");
		tempSet.add("H1");
		tempSet.add("H2");
		tempSet.add("H3");
		tempSet.add("H4");
		tempSet.add("H5");
		tempSet.add("H6");
		tempSet.add("P");

		//Standard structure types for list elements PDF 1.4 and PDF 1.7
		tempSet.add("L");
		tempSet.add("LI");
		tempSet.add("Lbl");
		tempSet.add("LBody");

		//Standard structure types for table elements PDF 1.4 and PDF 1.7
		tempSet.add("Table");
		tempSet.add("TR");
		tempSet.add("TH");
		tempSet.add("TD");

		// Standard structure types for inline-level structure elements PDF 1.4 and PDF 1.7
		tempSet.add("Span");
		tempSet.add("Quote");
		tempSet.add("Note");
		tempSet.add("Reference");
		tempSet.add("BibEntry");
		tempSet.add("Code");
		tempSet.add("Link");

		// Standard structure types for illustration elements PDF 1.4 and PDF 1.7
		tempSet.add("Figure");
		tempSet.add("Formula");
		tempSet.add("Form");

		PDF_1_4_STANDARD_ROLE_TYPES = new HashSet<>(tempSet);

		//Standard structure types for table elements PDF 1.7
		tempSet.add("THead");
		tempSet.add("TBody");
		tempSet.add("TFoot");

		// Standard structure types for inline-level structure elements PDF 1.7
		tempSet.add("Annot");
		tempSet.add("Ruby");
		tempSet.add("Warichu");

		// Standard structure types for Ruby and Warichu elements PDF 1.7
		// Elements "Ruby" and "Warichu" are removed here, because they are already in set
		tempSet.add("RB");
		tempSet.add("RT");
		tempSet.add("RP");
		tempSet.add("WT");
		tempSet.add("WP");

		PDF_1_7_STANDARD_ROLE_TYPES = new HashSet<>(tempSet);
	}

	private Map<ASAtom, ASAtom> roleMap;

	/**
	 * Creates new TaggedPDFRoleMapHelper
	 * @param roleMap role map from PDF
	 */
	public TaggedPDFRoleMapHelper(Map<ASAtom, ASAtom> roleMap) {
		this.roleMap = roleMap == null ? Collections.<ASAtom, ASAtom>emptyMap() : new HashMap<>(roleMap);
	}

	/**
	 * Obtains standard type for the given one
	 * @param type the type for obtaining the standard
	 * @return standard type for the given one or null in cases when there is
	 * no standard for the given or there is a cycle of the custom types
	 */
	public String getStandardType(ASAtom type, boolean isPDFA1, boolean isWCAG) {
		if (type == null) {
			return null;
		}
		Set<String> currentStandardTypes;
		boolean isFastStop;
		if (isPDFA1) {
			currentStandardTypes = PDF_1_4_STANDARD_ROLE_TYPES;
			isFastStop = true;
		} else {
			currentStandardTypes = PDF_1_7_STANDARD_ROLE_TYPES;
			if (isWCAG) {
				currentStandardTypes.add("Artifact");
			}
			isFastStop = false;
		}
		return getStandardType(type, currentStandardTypes, isFastStop);
	}

	private String getStandardType(ASAtom type, Set<String> currentStandardTypes, boolean isFastStop) {
		Set<ASAtom> visitedTypes = new HashSet<>();
		visitedTypes.add(type);
		ASAtom res = roleMap.get(type);
		String typeValue = type.getValue();
		if ((isFastStop || res == null || visitedTypes.contains(res)) && currentStandardTypes.contains(typeValue)) {
			return typeValue;
		}
		while (res != null && !visitedTypes.contains(res)) {
			String resValue = res.getValue();
			if (currentStandardTypes.contains(resValue)) {
				return resValue;
			}
			visitedTypes.add(res);
			res = roleMap.get(res);
		}
		return null;
	}

	public Boolean circularMappingExist(ASAtom type) {
		if (type == null) {
			return null;
		}
		Set<ASAtom> visitedTypes = new HashSet<>();
		visitedTypes.add(type);
		ASAtom res = roleMap.get(type);
		while (res != null) {
			if (visitedTypes.contains(res)) {
				return true;
			}
			visitedTypes.add(res);
			res = roleMap.get(res);
		}
		return false;
	}
}
