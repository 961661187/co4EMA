package com.lasat.ga.problem

import com.lasat.ga.problem.problemtrait.Problem

case object ShubertProblem extends Problem[Double] {
  /**
   * get the score of individual
   *
   * @param variableList variable list of an individual without normalization
   * @return score of the individual
   */
  override def getScore(variableList: Array[Double]): Double = {
    val normalizedVar = normalization(variableList)
    getScoreWithNormalizedArray(normalizedVar)
  }

  /**
   * if an individual meet the constraint
   *
   * @param variableList variable list of an individual, the variables has not been normalized
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
  override def normalization(variableList: Array[Double]): Array[Double] = variableList

  /**
   * inverse variable
   *
   * @param normalizedList normalized array of an individual
   * @return inverse normalized result of this individual
   */
  override def inverseNormalization(normalizedList: Array[Double]): Array[Double] = normalizedList

  /**
   * get score with normalized variable array
   *
   * @param normalizedList normalized array of an individual
   * @return score of this individual
   */
override def getScoreWithNormalizedArray(normalizedList: Array[Double]): Double = {
  var xSum = .0
  var ySum = .0
  val x = normalizedList(0)
  val y = normalizedList(1)
  for (i <- 1 to 5) {
    xSum += i * Math.cos((i + 1) * x + i)
    ySum += i * Math.cos((i + 1) * y + i)
  }
  xSum * ySum
}

  /**
   * get the name of this problem
   *
   * @return
   */
  override def getProblemName: String = "Shubert"

  /**
   * check whether given variables meet constraint
   *
   * @param variable normalized variables
   * @return is variable meet constrains
   */
  override def checkConstraint(variable: Array[Double]): Boolean = {
    for (elem <- variable) {
      if (elem > 10 || elem < -10) return false
    }
    true
  }
}
