import java.io.File

import io.github.edouardfouche.generators._

//import com.edouardfouche.generators_deprecated._
import com.edouardfouche.experiments._
import com.edouardfouche.stats.Stats
import com.edouardfouche.stats.external._
import com.edouardfouche.stats.mcde._
import com.edouardfouche.index._
import com.edouardfouche.preprocess._


// Checking Generators

val rows = 50
val dims = 2

val arr = Independent(dims, 0.0, "gaussian", 0).generate(rows)
val arr2 = Cross(dims, 0.0, "gaussian", 0).generate(rows)
arr.transpose

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


val all_gens = all_generators.map(x => x.generate(rows))

def get_dim[T](arr: Array[Array[T]]): (Int, Int) = {
  (arr.length, arr(0).length)
}


def test_dim (arr_l: List[Array[Array[Double]]], size: Int = 0, tru: Int = 0): Boolean = {
  if(arr_l == Nil) size == tru
  else if (get_dim(arr_l.head) == (rows, dims)) test_dim(arr_l.tail, size + 1 , tru + 1)
  else test_dim(arr_l.tail, size +1, tru)
}

test_dim(all_gens)

/**
  * Generated Data is row oriented (row x cols)
  */


// Checking External Tests

val all_ex_stats = List(CMI(), HICS(), II(), MAC(), MS(), TC(), UDS())
val all_mcde_stats = List(KS(), MWB(), MWP(), MWPi(), MWPr(), MWPs(), MWPu(), MWZ(), S())

def which_row_orient(stats: List[Stats]): List[Boolean] = {
  {for{
    stat <- stats
    data = stat.preprocess(arr)
  } yield get_dim(data.index)}.map(x => x == (rows, dims))
}

which_row_orient(all_ex_stats)
which_row_orient(all_mcde_stats)

/**
  * After preprocess() --> PreprocessedData = all are row oriented
  * However val index is col oriented --> See Index Class
  */

// Also check for all indexstructres --> To be sure that they are all implemented the same

val all_indecies = List(new AdjustedRankIndex(arr), new CorrectedRankIndex(arr), new ExternalRankIndex(arr),
  new NonIndex(arr), new RankIndex(arr))

def which_row_orient_index(ind: List[Index]):List[Boolean] = {
    {for {
      index <- ind
    } yield get_dim(index.index)}.map(x => x == (dims, rows))

}


which_row_orient_index(all_indecies).map(x => !x)

// Apply Method calls the index at dim n (see Index)
val exRank = new ExternalRankIndex(arr)
val noIndex = new NonIndex(arr)
get_dim(exRank.index)

exRank(0).length
noIndex(0).length
noIndex(1).length

////// Check for Saved Data

// Check own generated file
Independent(dims,0.0,"gaussian",0).save(1000)
val path = s"${System.getProperty("user.home")}/datagenerator/Independent-2-0.0.csv"
val data = Preprocess.open(path, header = 1, separator = ",", excludeIndex = false, dropClass = true)
get_dim(data) // row oriented as it should

val dataclass2 = DataRef("Independent-2-0.0", path, 1, ",", "Test")
val data5 = dataclass2.open()
get_dim(data5)

val data6 = dataclass2.openAndPreprocess(MWP()).index
get_dim(data6) // here was the bug --> openAndPreprocess applied an transpose


// Check in getClass.getResource("/data/Independent-2-0.0.csv").getPath
val data2 = Preprocess.open(getClass.getResource("/data/Independent-2-0.0.csv").getPath, header = 1, separator = ",", excludeIndex = false, dropClass = true)
get_dim(data2) // row oriented as should

val dataclass = DataRef("Independent-2-0.0", getClass.getResource("/data/Independent-2-0.0.csv").getPath, 1, ",", "Test")
val data3 = dataclass.open()
get_dim(data3) // works as expected -> row oriented

val data4 = dataclass.openAndPreprocess(CMI()).index
get_dim(data4) // this was incorrect (see above) !!!
get_dim(exRank.index) // To compare, col oriented -> as it should


// Check if Data in getClass.getResource("/data/").getPath has correct shape

def getListOfFiles(dir: String):List[File] = {
  val d = new File(dir)
  if (d.exists && d.isDirectory) {
    d.listFiles.filter(_.isFile).toList
  } else {
    List[File]()
  }
}

val lst_of_f = getListOfFiles(getClass.getResource("/data/").getPath).map(x => x.getAbsolutePath)

{for{
  f <- lst_of_f
  data = Preprocess.open(f, header = 1, separator = ",", excludeIndex = false, dropClass = true)
  if get_dim(data)._1 == 1000
} yield true}.size == lst_of_f.size

// --> Data is correct

val indecies = List(new ExternalRankIndex(arr))
get_dim(indecies.head.index)
which_row_orient_index(indecies)








