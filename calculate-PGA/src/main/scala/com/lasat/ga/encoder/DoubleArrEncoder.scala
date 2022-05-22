package com.lasat.ga.encoder

import com.lasat.ga.encoder.encodertrait.Encoder

/**
 * Encoder of double floating point array
 */
object DoubleArrEncoder extends Encoder[Double] {

  override def encode(variableList: Array[Double]): Array[Boolean] = {
    val length = getGeneLength(variableList)
    val result = new Array[Boolean](length)
    var resultIndex = 0
    var numIndex = 0

    while (resultIndex < length) {
      val tempArr: Array[Boolean] = encodeNum(variableList(numIndex))
      for (elem <- tempArr) {
        result(resultIndex) = elem
        resultIndex += 1
      }
      numIndex += 1
    }

    result
  }

  override def decode(gene: Array[Boolean]): Array[Double] = {
    val length = gene.length / 15
    val result = new Array[Double](length)

    var geneIndex = 0
    var numIndex = 0

    while (numIndex < length) {
      val tempArr = new Array[Boolean](15)
      for (i <- 0 until 15) {
        tempArr(i) = gene(geneIndex)
        geneIndex += 1
      }
      result(numIndex) = decodeNum(tempArr)
      numIndex += 1
    }

    result
  }

  override def getGeneLength(variableList: Array[Double]): Int = variableList.length * 15

  /**
   * encode a double type number
   *
   * @param num number need to be encoded
   * @return
   */
  private def encodeNum(num: Double): Array[Boolean] = {
    //true in gene array means 1 in binary while false means 0
    val result: Array[Boolean] = new Array[Boolean](15)
    var index = 0

    //value of the first element express the sign of the number
    //true means positive while false means negative
    if (num >= 0) result(index) = true
    index += 1

    //the number of the integer part is between 0 to 10 which means it has occupies 4 bits
    val intNum = num.toInt
    var temp = IntegerEncoder.encode(intNum)
    for (elem <- temp) {
      result(index) = elem
      index += 1
    }

    /*
      keep 3 significant digits in the decimal parts
      take the same encode method of integer part
      decimal part is between 0 to 1000 after multiplying 1000
      which means it while occupies 10 binary bits
     */
    val decNum = num - intNum
    val decIntNum = (decNum * 1000).toInt
    if (decIntNum <= 10) index += 6
    temp = IntegerEncoder.encode(decIntNum)
    for (elem <- temp) {
      result(index) = elem
      index += 1
    }

    result
  }

  /**
   * decode a double type number
   *
   * @param gene boolean type array
   * @return
   */
  private def decodeNum(gene: Array[Boolean]): Double = {
    var result: Double = 0

    //handle integer part
    var temp = new Array[Boolean](4)
    for (i <- 1 to 4) {
      temp(i - 1) = gene(i)
    }
    result += IntegerEncoder.decode(temp)

    //handle decimal part
    temp = new Array[Boolean](10)
    for (i <- 5 to 14) {
      temp(i - 5) = gene(i)
    }
    result += IntegerEncoder.decode(temp).toDouble / 1000

    //handle signal part
    if (!gene(0)) result = -result

    result
  }
}
