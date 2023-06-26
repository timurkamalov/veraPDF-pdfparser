/**
 * This file is part of veraPDF Parser, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Parser is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Parser as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Parser as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.pd.actions;

import org.verapdf.as.ASAtom;
import org.verapdf.cos.COSArray;
import org.verapdf.cos.COSObjType;
import org.verapdf.cos.COSObject;
import org.verapdf.pd.PDObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxim Plushchov
 */
public class PDMediaClip extends PDObject {
	public PDMediaClip(COSObject obj) {
		super(obj);
	}

	public String getContentType() {
		return getObject().getStringKey(ASAtom.CT);
	}

	public List<String> getAlternateDescription() {
		COSObject object = getObject().getKey(ASAtom.ALT);
		if (object.getType() == COSObjType.COS_ARRAY) {
			List<String> list = new LinkedList<>();
			for (COSObject elem : (COSArray)object.getDirectBase()) {
				if (elem.getType() == COSObjType.COS_STRING) {
					list.add(elem.getString());
				}
			}
			return list;
		}
		return Collections.emptyList();
	}
}
