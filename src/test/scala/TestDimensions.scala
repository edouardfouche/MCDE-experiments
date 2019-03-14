//import com.edouardfouche.generators_deprecated._
import io.github.edouardfouche.generators._
import com.edouardfouche.index._
import com.edouardfouche.preprocess._
import com.edouardfouche.stats.Stats
import org.scalatest.FunSuite
import com.edouardfouche.stats.mcde._
import com.edouardfouche.stats.external._
import java.io.File
import java.nio.file.{Paths, Files}
import org.apache.commons.io.FileUtils
import com.edouardfouche.stats.external.bivariate._
import com.edouardfouche.utils

import scala.language.existentials // Fixes a curious warning.



class TestDimensions extends FunSuite {

  val rows = 50
  val dims = 4
  val arr: Array[Array[Double]] = Independent(dims, 0.0, "gaussian", 0).generate(rows)
  val bivar_arr: Array[Array[Double]] = Independent(2, 0.0, "gaussian", 0).generate(rows)

  // TODO: What if new Tests / Generators?
  val all_ex_stats: List[Stats] = List(CMI(), HICS(), II(), MAC(), MS(), TC(), UDS())
  val all_mcde_stats:List[Stats] = List(KS(), MWB(), MWP(), MWPi(), MWPr(), MWPs(), MWPu(), MWZ(), S())
  val all_bivar: List[Stats] =  List(Correlation(), DistanceCorrelation(), HoeffdingsD(), HSM(), JSEquity(),
    MCE(), MutualInformation(), Slope(), SlopeInversion(), SpearmanCorrelation(), KendallsTau())


  val all_indecies = List(new AdjustedRankIndex(arr), new CorrectedRankIndex(arr), new ExternalRankIndex(arr),
    new NonIndex(arr), new RankIndex(arr))

  val all_bivar_indecies = List(new AdjustedRankIndex(bivar_arr), new CorrectedRankIndex(bivar_arr), new ExternalRankIndex(bivar_arr),
    new NonIndex(bivar_arr), new RankIndex(bivar_arr))

  val all_generators = List(
    Cross(dims,0.0,"gaussian",0),
    DoubleLinear(dims,0.0,"gaussian",0)(coef=Some(0.25)),
    DoubleLinear(dims,0.0,"gaussian",0)(coef=Some(0.5)),
    DoubleLinear(dims,0.0,"gaussian",0)(coef=Some(0.75)),
    Parabola(dims,0.0,"gaussian",0)(scale=Some(1)),
    Parabola(dims,0.0,"gaussian",0)(scale=Some(2)),
    Parabola(dims,0.0,"gaussian",0)(scale=Some(3)),
    Hourglass(dims,0.0,"gaussian",0), Hypercube(dims,0.0,"gaussian",0), HypercubeGraph(dims,0.0,"gaussian",0),
    Independent(dims,0.0,"gaussian",0),
    Linear(dims,0.0,"gaussian",0),
    LinearPeriodic(dims,0.0,"gaussian",0)(period = Some(2)),
    LinearPeriodic(dims,0.0,"gaussian",0)(period = Some(5)),
    LinearPeriodic(dims,0.0,"gaussian",0)(period = Some(10)),
    LinearPeriodic(dims,0.0,"gaussian",0)(period = Some(20)),
    LinearStairs(dims,0.0,"gaussian",0)(Some(2)),
    LinearStairs(dims,0.0,"gaussian",0)(Some(5)),
    LinearStairs(dims,0.0,"gaussian",0)(Some(10)),
    LinearStairs(dims,0.0,"gaussian",0)(Some(20)),
    LinearSteps(dims,0.0,"gaussian",0)(Some(2)),
    LinearSteps(dims,0.0,"gaussian",0)(Some(5)),
    LinearSteps(dims,0.0,"gaussian",0)(Some(10)),
    LinearSteps(dims,0.0,"gaussian",0)(Some(20)),
    LinearThenDummy(dims,0.0,"gaussian",0),
    LinearThenNoise(dims,0.0,"gaussian",0),
    NonCoexistence(dims,0.0,"gaussian",0),
    Cubic(dims,0.0,"gaussian",0)(Some(2)),Cubic(dims,0.0,"gaussian",0)(Some(3)),
    RandomSteps(dims,0.0,"gaussian",0)(Some(2)), RandomSteps(dims,0.0,"gaussian",0)(Some(5)),
    RandomSteps(dims,0.0,"gaussian",0)(Some(10)), RandomSteps(dims,0.0,"gaussian",0)(Some(20)),
    Sine(dims,0.0,"gaussian",0)(Some(2)), Sine(dims,0.0,"gaussian",0)(Some(5)),
    Sine(dims,0.0,"gaussian",0)(Some(10)), Sine(dims,0.0,"gaussian",0)(Some(20)),
    HyperSphere(dims,0.0,"gaussian",0),
    Root(dims,0.0,"gaussian",0)(Some(1)), Root(dims,0.0,"gaussian",0)(Some(2)),
    Root(dims,0.0,"gaussian",0)(Some(3)),
    Star(dims,0.0,"gaussian",0),
    StraightLines(dims,0.0,"gaussian",0),
    Z(dims,0.0,"gaussian",0),
    Zinv(dims,0.0,"gaussian",0)
  )

  val all_gens: List[Array[Array[Double]]] = all_generators.map(x => x.generate(rows))

  val path: String = getClass.getResource("/data/").getPath + "generated/"
  val directory = new File(path) // create if not existing
  if (!directory.exists()) {
    directory.mkdir()
  } //val path = s"${System.getProperty("user.home")}/datagenerator_for_scalatest/"
  val indi = Independent(dims, 0.0,"gaussian",0)
  indi.save(1000, path) // saveSample is final on Base Class, dir gets destructed after test
  val data: Array[Array[Double]] = Preprocess.open(path + indi.id + ".csv", header = 1, separator = ",", excludeIndex = false, dropClass = true)
  val dataclass = DataRef("Independent-2-0.0", path + indi.id + ".csv", 1, ",", "Test")


  def get_dim[T](arr: Array[Array[T]]): (Int, Int) = {
    (arr.length, arr(0).length)
  }

  def which_row_orient_stats(stats: List[Stats]): List[Boolean] = {
      {for{
        stat <- stats
        data = stat.preprocess(arr)
      } yield get_dim(data.index)}.map(x => x == (dims, rows))
  }

  def which_row_orient_bivar_stats(stats: List[Stats]): List[Boolean] = {
    {for{
      stat <- stats
      data = stat.preprocess(bivar_arr)
    } yield get_dim(data.index)}.map(x => x == (2, rows))
  }

  def which_row_orient_index(ind: List[Index]):List[Boolean] = {
      {for {
        index <- ind
      } yield get_dim(index.index)}.map(x => x == (dims, rows))

  }


  test("Checking if generated data is row oriented"){

    def test_dim (arr_l: List[Array[Array[Double]]], size: Int = 0, tru: Int = 0): Boolean = {
      if(arr_l == Nil) size == tru
      else if (get_dim(arr_l.head) == (rows, dims)) test_dim(arr_l.tail, size + 1 , tru + 1)
      else test_dim(arr_l.tail, size +1, tru)
    }

    assert(test_dim(all_gens))
  }

  test("Checking if val index is col oriented for all Stats"){
    which_row_orient_stats(all_ex_stats).map(x => assert(x))
    which_row_orient_stats(all_mcde_stats).map(x => assert(x))
    which_row_orient_bivar_stats(all_bivar).map(x => assert(x))
  }

  // To be sure we may be testing twice the same stuff

  test("Checking if val index is col oriented for all Indexstructures"){
    which_row_orient_index(all_indecies).map(x => assert(x))
  }

  test("Checking if no of rows in saved data by saveSample != dims"){
    assert(get_dim(data)._1 != dims)
  }

  test("Checking if saved data using DataGenerator.saveSample loads row oriented using Preprocess.open() and DataRef(...).open()"){
    val dataclassData = dataclass.open()
    assert(get_dim(data)._2 == dims)
    assert(get_dim(dataclassData)._2 == dims)
  }

  test("Checking if DataRef(...).openAndPreprocess() loads col oriented data"){

    for{
      stat <- all_ex_stats ::: all_mcde_stats
      data = dataclass.openAndPreprocess(stat).index
    } assert(get_dim(data)._1 == dims)
  }

  test("Dir Destructor"){
    FileUtils.deleteDirectory(new File(path))
    assert(Files.notExists(Paths.get(path)))
  }


}
