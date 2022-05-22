package com.lasat.ga.encoder.encodertrait

/**
 * chromosome gene encoder
 *
 * @tparam T variable type
 */
trait Encoder[T] extends Serializable {

  def encode(variableList: Array[T]): Array[Boolean]

  def decode(gene: Array[Boolean]): Array[T]

  def getGeneLength(variableList: Array[T]): Int
}
