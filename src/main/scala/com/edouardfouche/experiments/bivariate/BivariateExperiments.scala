package com.edouardfouche.experiments.bivariate

import com.edouardfouche.experiments.Experiment
import com.edouardfouche.stats.external.bivariate._
import com.edouardfouche.stats.mcde._
import com.edouardfouche.stats.Stats


trait BivariateExperiments extends Experiment {
  val M_range: Vector[Int] = Vector(50)
  val m: Int = M_range(0)

  val tests: Vector[Stats] = Vector(MWP(m, 0.5),  KS(m, 0.1), MWPr(m, 0.5), MWPu(m, 0.5), Correlation(), DistanceCorrelation(), HoeffdingsD(), HSM(), JSEquity(),
    MCE(), MutualInformation(), Slope(), SlopeInversion(), SpearmanCorrelation(), KendallsTau()) //When adding or removing a measure here, do also in BivariatePowerM!!!

  val dims: Vector[Int] = Vector(2)
}
