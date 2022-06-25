package com.lasat.ga.problem.problemtrait

/**
 * Optimization problem description
 * including objective goal and constraint condition
 *
 * Normalization of variables is very important
 * for example, value between 0 to 10 is suitable for double encoder
 *
 * @tparam T type of the variable
 */
trait Problem[T] extends Serializable {

  /**
   * get the score of individual, the GA handler will get the individual with max score
   *
   * @param variableList variable list of an individual without normalization
   * @return score of the individual
   */
  def getScore(variableList: Array[T]): Double

  /**
   * if an individual meet the constraint
   *
   * @param variableList variable list of an individual
   * @return whether this individual is suitable
   */
  def isSuitable(variableList: Array[T]): Boolean

  /**
   * get a variable list of an individual
   *
   * @return variable list, put emphasis on type and length while element value is not important
   */
  def getVar: Array[T]

  /**
   * variable normalization is aimed at reducing random generation time,
   * transform value between -10.999 and 10.99 to the range of problem
   *
   * @param variableList variable list
   */
  def normalization(variableList: Array[T]): Array[T]

  /**
   * inverse variable
   * @param normalizedList normalized array of an individual
   * @return inverse normalized result of this individual
   */
  def inverseNormalization(normalizedList: Array[T]): Array[T]
  /**
   * get score with normalized variable array
    * @param normalizedList normalized array of an individual
   * @return score of this individual
   */
  def getScoreWithNormalizedArray(normalizedList: Array[T]): Double

  /**
   * get the name of this problem
   * @return
   */
  def getProblemName: String

  /**
   * check whether given variables meet constraint
   * @param variable normalized variables
   * @return is variable meet constrains
   */
  def checkConstraint(variable: Array[T]): Boolean
}
