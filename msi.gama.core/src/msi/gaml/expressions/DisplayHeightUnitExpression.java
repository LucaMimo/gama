/*******************************************************************************************************
 *
 * msi.gaml.expressions.DisplayHeightUnitExpression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;

public class DisplayHeightUnitExpression extends UnitConstantExpression<Double> {

	public DisplayHeightUnitExpression(final String doc) {
		super(0.0, Types.FLOAT, "display_height", doc, null);
	}

	@Override
	public Double _value(final IScope scope) {
		final IGraphics g = scope.getGraphics();
		if (g == null)
			return 0d;
		return (double) g.getDisplayHeight();
		// return (double) g.getEnvironmentHeight();
	}

	@Override
	public boolean isConst() {
		return false;

	}

	@Override
	public boolean isContextIndependant() {
		return false;
	}

}
