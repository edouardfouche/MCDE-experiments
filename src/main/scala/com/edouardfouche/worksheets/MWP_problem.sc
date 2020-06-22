import com.edouardfouche.stats.mcde.MWP

val len = 1000 // if set of 1000, then we have 0.0, if set to 100000, we have 1.0
val bound = 1000
val random = scala.util.Random
val a1: Array[Double] = (for (i <- 0 until len) yield 1.0).toArray // random.nextInt(bound).toDouble).toArray
val a2: Array[Double] = (for (i <- 0 until len) yield 1.0).toArray // random.nextInt(bound).toDouble).toArray
val data: Array[Array[Double]] = Array(a1, a2).transpose
val alpha = 0.5 // 0.1 + 0.9 * random.nextDouble();
val beta = 0.5 // 0.1 + 0.9 * random.nextDouble();
val mwp = MWP(M = 1000, alpha = alpha, beta = beta)
mwp.contrast(data, Set(0, 1))
