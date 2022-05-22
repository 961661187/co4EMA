package com.lasat.ga.handler

import com.lasat.ga.bean.{Chromosome, GAResult, PopulationMessage}
import com.lasat.ga.encoder.encodertrait.Encoder
import com.lasat.ga.problem.problemtrait.Problem
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Random

/**
 * general parallel genetic algorithm handler
 *
 * @param encoder encoder of variables
 * @param problem optimization problem need to be handled
 * @tparam T variable type
 */
class PGAHandler[T: ClassTag](val encoder: Encoder[T], val problem: Problem[T]) {
  //generation iterator
  var genIterNum: Int = 30
  //island number
  var islandNum: Int = 3
  //population size of each island
  var islandPopSize: Int = 50 * problem.getVar.length
  //generation iterator number of each island
  var islandGenIterNum: Int = 500
  //spark context
  var sparkContext: SparkContext = _
  //the length of gene
  var geneSize: Int = encoder.getGeneLength(problem.getVar)
  //migrate gate
  var migrateRate: Double = 0.3
  //best result
  var bestResult: GAResult[T] = new GAResult[T]

  /**
   * get calculate result of parallel genetic algorithm
   *
   * @return
   */
  def getResult: GAResult[T] = {
    //environment prepare
    val masterName: String = "local[" + islandNum + "]"
    val conf = new SparkConf().setMaster(masterName).setAppName(problem.getProblemName)
    sparkContext = new SparkContext(conf)

    //initialize population, get the population information of islands
    var islandPop = init()
    bestResult = getResultFromIsland(islandPop)

    //calculate loop
    for (_ <- 1 until genIterNum) {
      //individual migrate between each island
      migrate(islandPop)

      //parallel genetic algorithm
      val islandPopRDD: RDD[PopulationMessage[T]] = sparkContext.makeRDD(islandPop)
      val islandHandler = new GAHandler[T](encoder, problem)
      islandHandler.setAccuracy(islandPopSize, islandGenIterNum)
      islandPop = islandPopRDD.map(
        population => islandHandler.getEvolvedPopulation(population.population)
      ).collect()
      val tempResult: GAResult[T] = getResultFromIsland(islandPop)
      if (tempResult.score > bestResult.score)
        bestResult = tempResult
      //println(bestResult.score)
    }

    //environment free
    sparkContext.stop()

    //return result
    bestResult
  }

  /**
   * set accuracy of calculate
   *
   * @param genIterNum       maximum generation iterator number
   * @param islandNum        the number of island, that is also the number of local spark thread
   * @param islandPopSize    population size of each
   * @param islandGenIterNum generation iterator of island evolution
   */
  def setAccuracy(genIterNum: Int = 20,
                  islandNum: Int = 3,
                  islandPopSize: Int = 100,
                  islandGenIterNum: Int = 500,
                  migrateRate: Double = 0.3): Unit = {
    this.genIterNum = genIterNum
    this.islandNum = islandNum
    this.islandPopSize = islandPopSize
    this.islandGenIterNum = islandGenIterNum
    this.migrateRate = migrateRate
  }

  /**
   * initialize population by map-operator
   */
  private def init(): Array[PopulationMessage[T]] = {
    //initialize rdd
    val popSize = islandPopSize * islandNum
    val nullPopulation = new Array[Chromosome](popSize)
    val nullPopulationRDD: RDD[Chromosome] = sparkContext.makeRDD(nullPopulation)
    //task and its parameters should be serialized
    //as a result, all variables are assigned to constant
    val serializedGeneSize = geneSize
    val serializedEncoder = encoder
    val serializedProblem = problem
    //parallel initialize
    val populationRDD: RDD[Chromosome] = nullPopulationRDD.map {
      _ => {
        var chromosome = new Chromosome(serializedGeneSize)
        while (!serializedProblem.isSuitable(serializedEncoder.decode(chromosome.gene))) {
          chromosome = new Chromosome(serializedGeneSize)
        }
        chromosome
      }
    }
    //population fresh
    islandPopCalculate(populationRDD.collect())
  }

  /**
   * island population initial calculate
   *
   * @return island population message
   */
  private def islandPopCalculate(population: Array[Chromosome]): Array[PopulationMessage[T]] = {
    //individuals distribution
    val distributedPopulation: Array[ArrayBuffer[Chromosome]] = individualDistribution(population)
    //island population score calculate
    val distributionRDD: RDD[ArrayBuffer[Chromosome]] = sparkContext.makeRDD(distributedPopulation)
    val islandHandler = new GAHandler[T](encoder, problem)
    islandHandler.setAccuracy(islandPopSize, islandGenIterNum)
    val islandInfoRDD: RDD[PopulationMessage[T]] = distributionRDD.map(
      islandPopulation => {
        islandHandler.getEvolvedPopulation(islandPopulation)
      }
    )
    val islandPop: Array[PopulationMessage[T]] = islandInfoRDD.collect()
    islandPop
  }

  /**
   * individual distribution
   *
   * @return the population of each island
   */
  private def individualDistribution(population: Array[Chromosome]): Array[ArrayBuffer[Chromosome]] = {
    val distributedPopulation = new Array[ArrayBuffer[Chromosome]](islandNum)
    var popIndex = 0
    for (i <- distributedPopulation.indices) {
      distributedPopulation(i) = new ArrayBuffer[Chromosome]()
      for (_ <- 0 until islandPopSize) {
        distributedPopulation(i) += population(popIndex)
        popIndex += 1
      }
    }
    distributedPopulation
  }

  /**
   * get eventual result from all islands
   *
   * @param eventualIslandPop island populations after evolution
   * @return the best variables and score of all island populations
   */
  private def getResultFromIsland(eventualIslandPop: Array[PopulationMessage[T]]): GAResult[T] = {
    val result: GAResult[T] = new GAResult[T]()
    for (islandInfo <- eventualIslandPop) {
      if (islandInfo.bestScore > result.score) {
        result.variables = problem.normalization(islandInfo.bestVar)
        result.score = islandInfo.bestScore
      }
    }
    result
  }

  /**
   * individual migrate of each islands
   *
   * @param islandPop island information including population of each islands
   */
  private def migrate(islandPop: Array[PopulationMessage[T]]): Unit = {
    val migrateCount: Int = (islandPopSize * migrateRate).toInt
    val maxStartIndex = islandPopSize - migrateCount + 1

    //random start index
    //select random range is ok because individuals are not sorted
    //set temp individuals
    val tempIndividuals: ArrayBuffer[Chromosome] = new ArrayBuffer[Chromosome](migrateCount)
    var curStartIndex = Random.nextInt(maxStartIndex)
    for (j <- curStartIndex until curStartIndex + migrateCount) {
      tempIndividuals += islandPop(0).population(j)
    }
    //migrate is achieved by swap
    for (i <- 0 until islandNum - 1) {
      val nextStartIndex = Random.nextInt(maxStartIndex)
      for (j <- 0 until migrateCount) {
        islandPop(i).population(j + curStartIndex) = islandPop(i + 1).population(j + nextStartIndex)
      }
      curStartIndex = nextStartIndex
    }
    for (j <- 0 until migrateCount) {
      islandPop(islandNum - 1).population(j + curStartIndex) = tempIndividuals(j)
    }
  }

}
