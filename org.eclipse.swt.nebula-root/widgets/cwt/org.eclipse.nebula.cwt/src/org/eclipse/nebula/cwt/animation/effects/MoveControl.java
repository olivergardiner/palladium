/*******************************************************************************
 * Copyright (c) 2006-2009 Nicolas Richeton.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.cwt.animation.effects;

import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.movement.IMovement;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public class MoveControl extends AbstractEffect {

	/**
	 * @deprecated
	 * @param w
	 * @param x
	 * @param y
	 * @param duration
	 * @param movement
	 * @param onStop
	 * @param onCancel
	 */
	public static void move(AnimationRunner runner, Control w, int x, int y,
			int duration, IMovement movement, Runnable onStop, Runnable onCancel) {
		Point oldSize = w.getLocation();
		IEffect effect = new MoveControl(w, oldSize.x, x, oldSize.y, y,
				duration, movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	int startX, endX, startY, endY, stepX, stepY;

	Control control = null;

	public MoveControl(Control control, int startX, int endX, int startY,
			int endY, long lengthMilli, IMovement movement, Runnable onStop,
			Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);

		this.startX = startX;
		this.endX = endX;
		stepX = endX - startX;

		this.startY = startY;
		this.endY = endY;
		stepY = endY - startY;

		easingFunction.init(0, 1, (int) lengthMilli);

		this.control = control;
	}

	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			control.setLocation(((int) (startX + stepX
					* easingFunction.getValue((int) currentTime))),
					((int) (startY + stepY
							* easingFunction.getValue((int) currentTime))));
		}
	}

	public int getStartX() {
		return startX;
	}

	public int getEnd() {
		return endY;
	}

}