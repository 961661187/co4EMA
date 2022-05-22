package com.lasat.ga.encoder

object IntegerEncoder {

  def encode(num: Int): Array[Boolean] = {
    var integer = num
    var result: Array[Boolean] = null
    var index = 0

    if (integer > 10 && integer <= 999) {
      result = new Array[Boolean](10)
      index = 9
    } else if (integer >= 0){
      result = new Array[Boolean](4)
      index = 3
    } else {
       throw new RuntimeException("This encoder only allow integer between 0 and 999")
    }

    var quotient = 0
    var remainder = 0

    while (integer != 0) {
      quotient = integer >> 1 // quotient = integer / 2
      remainder = integer % 2
      result(index) = remainder == 1
      index -= 1
      integer = quotient
    }

    result
  }

  def decode(gene: Array[Boolean]): Int = {
    var result = 0
    var times = 1

    for (i <- gene.indices) {
      if (gene(gene.length - i - 1)) result += times
      times *= 2
    }
    result
  }


}
