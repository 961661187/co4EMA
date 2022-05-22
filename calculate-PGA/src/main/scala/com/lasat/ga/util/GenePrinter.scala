package com.lasat.ga.util

object GenePrinter {
  def printGene(gene: Array[Boolean]): Unit = {
    val sb = new StringBuilder()
    for (elem <- gene) {
      if (elem) sb.append("1")
      else sb.append("0")
    }

    print(sb + " ")
  }
}
