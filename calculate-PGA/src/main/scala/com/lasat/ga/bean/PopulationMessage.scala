package com.lasat.ga.bean

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

class PopulationMessage[T: ClassTag]() extends Serializable {
  var bestVar: Array[T] = _
  //the generation of best chromosome
  var bestScore: Double = .0
  //current worst score
  var worstScore: Double = .0
  //total score of all chromosomes
  var totalScore: Double = .0
  //average score of all chromosomes
  var averageScore: Double = .0
  //the aggregate of chromosome
  var population: ArrayBuffer[Chromosome] = _
}
