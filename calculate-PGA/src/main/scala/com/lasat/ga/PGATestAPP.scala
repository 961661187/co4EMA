package com.lasat.ga

import com.lasat.ga.bean.GAResult
import com.lasat.ga.encoder.DoubleArrEncoder
import com.lasat.ga.handler.PGAHandler
import com.lasat.ga.problem.ShubertProblem

/**
 * verify the correctness of parallel genetic algorithm
 */
object PGATestAPP extends App {
  //get a new handler based on parallel genetic algorithm,
  //and set the variable encoder and the problem including optimization function and constraint condition
  val pgaHandler = new PGAHandler[Double](DoubleArrEncoder, ShubertProblem)
  //get the calculate result adn print it
  val result: GAResult[Double] = pgaHandler.getResult
  println(result.score)
}
