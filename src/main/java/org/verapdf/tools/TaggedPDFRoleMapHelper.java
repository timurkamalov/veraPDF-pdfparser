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
import org.verapdf.parser.PDFFlavour;

import java.util.*;

/**
 * @author Maksim Bezrukov
 */
public class TaggedPDFRoleMapHelper {

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
	public String getStandardType(ASAtom type) {
		if (type == null) {
			return null;
		}
		Set<String> currentStandardTypes;
		boolean isFastStop;
		if (StaticResources.getFlavour() == PDFFlavour.PDFA_1_A || StaticResources.getFlavour() == PDFFlavour.PDFA_1_B) {
			currentStandardTypes = TaggedPDFHelper.getPdf14StandardRoleTypes();
			isFastStop = true;
		} else {
			if (StaticResources.getFlavour() == PDFFlavour.WCAG2_1) {
				currentStandardTypes = TaggedPDFHelper.getWcagStandardRoleTypes();
			} else {
				currentStandardTypes = TaggedPDFHelper.getPdf17StandardRoleTypes();
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
