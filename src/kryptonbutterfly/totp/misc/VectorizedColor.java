package kryptonbutterfly.totp.misc;

import kryptonbutterfly.math.vector._int.IVecI;

record VectorizedColor<Color extends NamedColor, Vec extends IVecI<Vec>>(Vec vec, Color color)
{}