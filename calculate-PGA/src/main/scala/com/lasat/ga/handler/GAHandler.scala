package com.lasat.ga.handler

import com.lasat.ga.bean.{Chromosome, GAResult, PopulationMessage}
import com.lasat.ga.encoder.encodertrait.Encoder
import com.lasat.ga.problem.problemtrait.Problem

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
 * general GA handler, this class is not thread safe
 *
 * @param encoder encode of variable
 * @param problem optimization goal function and constraint description
 * @tparam T variable type
 */
class GAHandler[T: ClassTag](val encoder: Encoder[T], val problem: Problem[T]) extends Serializable {
  //the aggregate of chromosome
  var population = new ArrayBuffer[Chromosome]
  //the maximum count of chromosome in population
  var popSize: Int = 50 * problem.getVar.length
  //maximum iterations
  var maxIterNum: Int = 300
  //current iterations
  var generation: Int = 1
  //the length of gene
  var geneSize: Int = encoder.getGeneLength(problem.getVar)
  //variable list of the chromosome with best score
  var bestVar: Array[T] = new Array[T](geneSize)
  //the generation of best chromosome
  var bestScore: Double = .0
  //current worst score
  var worstScore: Double = .0
  //total score of all chromosomes
  var totalScore: Double = .0
  //average score of all chromosomes
  var averageScore: Double = .0
  //mutation rate of chromosome
  var mutationRate: Double = 0.05
  //maximum mutation num of gene
  var maxMutationNum: Int = (3 * problem.getVar.length * 0.5).toInt
  //emergency individual, aimed at solving the problem of the dead cycle during select
  var emergencyParent: (Chromosome, Chromosome) = (new Chromosome(geneSize), new Chromosome(geneSize))

  /**
   * get the normalization result of GA, and make sure event result meet constraint condition
   *
   * @return
   */
  def getNormalizedResult: GAResult[T] = {
    var calculateResult = calculate()
    while (!problem.isSuitable(calculateResult)) {
      calculateResult = calculate()
    }
    val result = new GAResult[T]()
    result.variables = problem.normalization(bestVar)
    result.score = bestScore
    result
  }

  /**
   * get evolved population and best score,
   * result is not normalized
   *
   * @return (best score of the last generation, best individual's variable array, last population)
   */
  def getEvolvedPopulation(initPop: ArrayBuffer[Chromosome]): PopulationMessage[T] = {
    population = initPop
    var calculateResult = calculateWithoutInit()
    while (!problem.isSuitable(calculateResult)) {
      calculateResult = calculateWithoutInit()
    }
    encapsulation()
  }

  /**
   * set calculate accuracy by changing population size and maximum iterator number
   *
   * @param popSize    population size defined by caller
   * @param maxIterNum maximum iterator number defined by caller
   */
  def setAccuracy(popSize: Int = 100, maxIterNum: Int = 500): Unit = {
    this.popSize = popSize
    this.maxIterNum = maxIterNum
  }

  /**
   * get the result of optimization problem
   *
   * @return calculation result
   */
  private def calculate(): Array[T] = {
    init()
    calculateWithoutInit()
  }

  private def calculateWithoutInit(): Array[T] = {
    generation = 1
    while (generation < maxIterNum) {
      evolve()
      generation += 1
    }
    bestVar
  }
  /**
   * initialize population
   */
  private def init(): Unit = {
    while (population.size < popSize) {
      val c = new Chromosome(geneSize)
      //guarantee individuals meet constraint conditions
      if (problem.isSuitable(encoder.decode(c.gene)))
        population += c
    }
    calculateScore()
  }

  /**
   * calculate the score of a population
   */
  private def calculateScore(): Unit = {
    //population initialization
    setChromosomeScore(population(0))
    bestScore = population(0).score
    worstScore = population(0).score
    totalScore = 0
    //calculate best and worst score
    for (c <- population) {
      //score update
      setChromosomeScore(c)
      if (c.score > bestScore) {
        bestScore = c.score
        bestVar = encoder.decode(c.gene)
      }
      if (c.score < worstScore) {
        worstScore = c.score
      }
      totalScore += c.score
      //emergency individual update
      if (c.score > Math.min(emergencyParent._1.score, emergencyParent._2.score)) {
        if (c.score > emergencyParent._1.score) {
          emergencyParent = (c, emergencyParent._2)
        } else {
          emergencyParent = (emergencyParent._1, c)
        }
      }
    }
    //calculate average score
    averageScore = totalScore / popSize
    averageScore = if (averageScore > bestScore) bestScore else averageScore

  }

  /**
   * calculate the score of an individual and set it
   */
  private def setChromosomeScore(c: Chromosome): Unit = {
    if (c == null) {
      return
    }
    val score = problem.getScore(encoder.decode(c.gene))
    c.score = score
  }

  /**
   * select individuals whose score is greater than equal average score of the population
   *
   * @return
   */
  private def getParentChromosome: Chromosome = {
    val slice: Double = Math.random * totalScore
    var sum: Double = 0
    for (c <- population) {
      sum += c.score
      if (sum > slice && c.score >= averageScore) {
        return c
      }
    }
    null
  }

  /**
   * create next generation
   */
  private def evolve() {
    //new population
    val childPopulation = new ArrayBuffer[Chromosome]
    while (childPopulation.size < popSize) {
      var p1: Chromosome = null
      var p2: Chromosome = null

      var count = maxIterNum
      //get parent chromosome
      while (p1 == null && count > 0) {
        p1 = getParentChromosome
        count -= 1
      }
      if (count <= 0) p1 = emergencyParent._1
      count = maxIterNum
      while (p2 == null && count > 0) {
        p2 = getParentChromosome
        count -= 1
      }
      if (count <= 0) p2 = emergencyParent._2

      val children = p1.genetic(p2)

      //ensure the individual fit constraint conditions
      if (children._1 != null && problem.isSuitable(encoder.decode(children._1.gene)))
        childPopulation += children._1
      else childPopulation += p1
      if (children._2 != null && problem.isSuitable(encoder.decode(children._2.gene)))
        childPopulation += children._2
      else childPopulation += p2

    }

    var t: ArrayBuffer[Chromosome] = population
    population = childPopulation
    //clear variable
    t.clear
    t = null
    mutation()
    calculateScore()
  }


  /**
   * population generation
   */
  private def mutation() {
    for (i <- 0 until popSize) {
      if (Math.random < mutationRate) {
        val mutationNum: Int = (Math.random * maxMutationNum).toInt
        val c = population(i)
        val pre = c.selfClone()
        c.mutation(mutationNum)
        //rollback if mutation individual don't fit the constraint conditions
        if (!problem.isSuitable(encoder.decode(c.gene))) {
          population(i) = pre
        }
      }
    }
  }

  /**
   * clear variables in order to recalculate
   */
  private def clearVariable(): Unit = {
    population.clear
    generation = 1
    bestVar = new Array[T](geneSize)
    bestScore = .0
    worstScore = .0
    totalScore = .0
    averageScore = .0
    emergencyParent = (new Chromosome(geneSize), new Chromosome(geneSize))
  }

  /**
   * population information encapsulation
   * @return
   */
  private def encapsulation(): PopulationMessage[T] = {
    val result = new PopulationMessage[T]()
    result.averageScore = this.averageScore
    result.bestScore = this.bestScore
    result.worstScore = this.worstScore
    result.averageScore = this.averageScore
    result.bestVar = this.bestVar
    result.population = this.population
    result
  }
}
