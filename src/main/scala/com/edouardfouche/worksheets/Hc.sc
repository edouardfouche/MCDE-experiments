import com.edouardfouche.stats.mcde.MWP
import io.github.edouardfouche.generators.{Hypercube, HypercubeGraph}

val n = 10000
val dim = 5
val noise0 = 0
val noise1 = 0.03

val data0 = Hypercube(dim, noise0, "gaussian", 0).getPoints(n)

(1 to 100).map(x => MWP(M=100).contrast(data0, (0 until dim).toSet)).sum/100

val data1 = Hypercube(dim, noise1, "gaussian", 0).getPoints(n)

(1 to 100).map(x => MWP(M=100).contrast(data1, (0 until dim).toSet)).sum/100
