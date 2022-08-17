package com.lasat.ga.problem

import com.lasat.ga.problem.problemtrait.Problem

/**
 * find the minimum value of (x + 2)(x - 1) where x >= -5 and x < 5
 */
object QuadraticProblem extends Problem[Double] {
  /**
   * get the score of individual
   *
   * @param variableList variable list of an individual without normalization
   * @return score of the individual
   */
  override def getScore(variableList: Array[Double]): Double =  {
    val normalizedArray = normalization(variableList)
    getScoreWithNormalizedArray(normalizedArray)
  }

  /**
   * if an individual meet the constraint
   *
   * @param variableList variable list of an individual without normalization
   * @return whether this individual is suitable
   */
  override def isSuitable(variableList: Array[Double]): Boolean = {
    val normalizedArray = normalization(variableList)
    checkConstraint(normalizedArray)
  }

  /**
   * get a variable list of an individual
   *
   * @return variable list, put emphasis on type and length while element value is not important
   */
  override def getVar: Array[Double] = new Array[Double](1)

  /**
   * variable normalization is aimed at reducing random generation time
   *
   * @param variableList variable list
   * @return variable list after normalization
   */
  override def normalization(variableList: Array[Double]): Array[Double] = {
    val result = new Array[Double](variableList.length)
    result(0) = variableList(0) / 2
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
    result(0) = normalizedList(0) * 2
    result
  }

  /**
   * get score with normalized variable array
   *
   * @param normalizedList normalized array of an individual
   * @return score of this individual
   */
  override def getScoreWithNormalizedArray(normalizedList: Array[Double]): Double = {
    val variable = normalizedList(0)
    val value = (variable + 2) * (variable  - 1)
    - value
  }

  /**
   * get the name of this problem
   *
   * @return
   */
  override def getProblemName: String = "quadratic problem"

  /**
   * check whether given variables meet constraint
   *
   * @param variable variables
   * @return is variable meet constrains
   */
  override def checkConstraint(variable: Array[Double]): Boolean = {
    variable(0) >= -5 && variable(0) <= 5
  }
}
