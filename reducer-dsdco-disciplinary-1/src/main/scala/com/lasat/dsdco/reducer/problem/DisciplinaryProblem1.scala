package com.lasat.dsdco.reducer.problem

import com.lasat.ga.problem.problemtrait.Problem

object DisciplinaryProblem1 extends Problem[Double] {

  //the upper limit of each variables
  val lowerLim: Array[Double] = Array(2.6, 0.7, 17, 7.3, 7.3, 2.9, 5.0)
  //the lower limit of each variables
  val upperLim: Array[Double] = Array(3.6, 0.8, 28, 8.3, 8.3, 3.9, 5.5)
  //get the closet point in the region to target point
  var targetVal: Array[Double] = _

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
  override def getVar: Array[Double] = new Array[Double](7)

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
    -Math.sqrt(result)
  }

  /**
   * get the name of this problem
   *
   * @return
   */
  override def getProblemName: String = "reducer-optimization"

  /**
   * check whether given variables meet constraint
   *
   * @param x normalized variables
   * @return is variable meet constrains
   */
  override def checkConstraint(x: Array[Double]): Boolean = {
    // the constraints is set in disciplinary model, as a result, in system model, only upper and lower limit is checked
    for (i <- 0 until 7) {
      if (x(i) > upperLim(i) || x(i) < lowerLim(i)) return false
    }
    val c1 = 27 / (x(0) * x(1) * x(1) * x(2)) - 1
    val c2 = 397.5 / (x(0) * x(1) * x(1) * x(2) * x(2)) - 1
    val c3 = 1.93 * Math.pow(x(3), 3) / (x(1) * x(2) * Math.pow(x(5), 4)) - 1
    val c4 = 1.93 * Math.pow(x(4), 3) / (x(1) * x(2) * Math.pow(x(6), 4)) - 1
    val c5 = (Math.sqrt(Math.pow(745 * x(3) / (x(1) * x(2)), 2) + 16.9e6) / (0.1 * Math.pow(x(5), 3))) - 1100
    val c6 = (Math.sqrt(Math.pow(745 * x(4) / (x(1) * x(2)), 2) + 157.5e6) / (0.1 * Math.pow(x(6), 3))) - 850
    c1 <= 0.002 && c2 <= 0.002 && c3 <= 0.002 && c4 <= 0.002 && c5 <= 0.002 && c6 <= 0.002
  }
}
