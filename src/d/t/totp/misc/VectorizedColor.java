package d.t.totp.misc;

import de.tinycodecrank.math.linalgebra.vector.IVecI;

record VectorizedColor<Color extends NamedColor, Vec extends IVecI<Vec>>(Vec vec, Color color)
{}