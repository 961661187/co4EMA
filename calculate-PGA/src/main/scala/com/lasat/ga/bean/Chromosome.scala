package com.lasat.ga.bean

class Chromosome extends Serializable {

  //binary gene array
  var gene: Array[Boolean] = _
  //chromosome score
  var score: Double = .0

  def this(n: Int) = {
    this()
    if (n <= 0) {}
    this.gene = new Array[Boolean](n)
    for (i <- 0 until n) gene(i) = Math.random() >= 0.5
  }

  def mutation(n: Int): Unit = {
    if (gene == null) return
    val length = gene.length
    for (_ <- 0 until n) {
      val index = (Math.random() * length).toInt % length
      gene(index) = !gene(index)
    }
  }

  /**
   * gene array of child is the same as it of parent
   *
   * @return child chromosome
   */
  def selfClone(): Chromosome = {
    if (gene != null) {
      val child = new Chromosome()
      child.gene = new Array[Boolean](gene.length)
      gene.copyToArray(child.gene)
      return child
    }
    null
  }

  /**
   * elect hybridization index of gene and produce child chromosome
   *
   * @param p parent chromosome
   * @return child chromosome tuple
   */
  def genetic(p: Chromosome): (Chromosome, Chromosome) = {
    if (p == null) {
      return null
    }
    if (this.gene.length != p.gene.length) {
      return null
    }
    val c1: Chromosome = this.selfClone()
    val c2: Chromosome = p.selfClone()

    val size: Int = c1.gene.length
    val a: Int = (Math.random * size).toInt % size
    val b: Int = (Math.random * size).toInt % size
    val min: Int = if (a > b) b else a
    val max: Int = if (a > b) a else b

    for (i <- min to max) {
      val t: Boolean = c1.gene(i)
      c1.gene(i) = c2.gene(i)
      c2.gene(i) = t
    }

    (c1, c2)
  }
}
