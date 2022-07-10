package com.lasat.dsdco.test.problem

import com.lasat.ga.problem.problemtrait.Problem

object DisciplinaryProblem2 extends Problem[Double] {

  //the upper limit of each variables
  val lowerLim: Array[Double] = Array(0.0, -25.0)
  //the lower limit of each variables
  val upperLim: Array[Double] = Array(10.0, -15.0)
  //get the closet point in the region to target point
  var targetVal: Array[Double] = _
  //threshold of convergence condition
  val convergenceConditionThreshold = 0.00101

  /**
   * get the score of individual, the GA handler will get the individual with max score
   *
   * @param variables variable list of an indivvidual without normalization
   * @return score of the individual
   */
  override def getScore(variables: Array[Double]): Double = {
    val x = normalization(variables)
    getScoreWithNormalizedArray(x)
  }

  /**
   * if an individual meet the constraint
   *
   * @param variableList variable list of an individual, variables is not normalized
   * @return whether this individual is suitable
   */
  override def isSuitable(variableList: Array[Double]): Boolean = {
    val normalizedVar = normalization(variableList)
    checkConstraint(normalizedVar)
  }

  /**
   * get a variable list of an individual
   *
   * @return variable list, put emphasis on type and length while element value is not important
   */
  override def getVar: Array[Double] = new Array[Double](2)

  /**
   * variable normalization is aimed at reducing random generation time,
   * transform value between -10.999 and 10.99 to the range of problem
   *
   * @param variableList variable list
   */
  override def normalization(variableList: Array[Double]): Array[Double] = {
    val result: Array[Double] = new Array[Double](variableList.length)
    for (i <- result.indices) {
      result(i) = variableList(i) * (upperLim(i) - lowerLim(i)) / 20 + (upperLim(i) + lowerLim(i)) / 2
    }
    result
  }

  /**
   * inverse variable
   *
   * @param normalizedList normalized array of an individual
   * @return inverse normalized result of this individual
   */
  override def inverseNormalization(normalizedList: Array[Double]): Array[Double] = {
    val result = new Array[Double](normalizedList.length)
    for (i <- result.indices) {
      result(i) = (normalizedList(i) - lowerLim(i)) * 20 / (upperLim(i) - lowerLim(i)) - 10
    }
    result
  }

  /**
   * get score with normalized variable array
   *
   * @param  x array of an individualx
   * @return score of this individual
   */
  override def getScoreWithNormalizedArray(x: Array[Double]): Double = {
    var result: Double = 0;
    for (i <- x.indices) {
      result = result + (x(i) - targetVal(i)) * (x(i) - targetVal(i))
    }
    // in order to get the minimum distance, take the negative value of distance as score
    // because PGA will get the maximum score in region
    -result
  }

  /**
   * get the name of this problem
   *
   * @return
   */
  override def getProblemName: String = "test-optimization-disciplinary-2"

  /**
   * check whether given variables meet constraint
   *
   * @param x normalized variables
   * @return is variable meet constrains
   */
  override def checkConstraint(x: Array[Double]): Boolean = {
    // the constraints is set in disciplinary model, as a result, in system model, only upper and lower limit is checked
    for (i <- 0 until 2) {
      if (x(i) > upperLim(i) || x(i) < lowerLim(i)) return false
    }
    val constraints1 = 0.1 * Math.pow((1.5 * x(0) - 20), 2) - 70 - x(1)
    val constraints2 = 50 / (x(0) + 0.1) - 40 - x(1)
    constraints1 <= convergenceConditionThreshold && constraints2 <= convergenceConditionThreshold
  }
}
