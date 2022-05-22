package com.lasat.ga

import com.lasat.ga.bean.GAResult
import com.lasat.ga.encoder.DoubleArrEncoder
import com.lasat.ga.handler.GAHandler
import com.lasat.ga.problem.{QuadraticProblem, ShubertProblem}

//verify correctness of GA
object GATestAPP extends App {
    val handler = new GAHandler[Double](DoubleArrEncoder, ShubertProblem)
    val result: GAResult[Double] = handler.getNormalizedResult
    println(result.score)
}
