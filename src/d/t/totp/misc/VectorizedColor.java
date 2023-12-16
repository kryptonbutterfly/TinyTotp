package d.t.totp.misc;

import de.tinycodecrank.math.vector._int.IVecI;

record VectorizedColor<Color extends NamedColor, Vec extends IVecI<Vec>>(Vec vec, Color color)
{}