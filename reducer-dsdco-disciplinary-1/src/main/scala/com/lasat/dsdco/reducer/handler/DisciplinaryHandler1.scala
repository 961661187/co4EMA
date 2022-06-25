package com.lasat.dsdco.reducer.handler

import com.lasat.dsdco.reducer.bean.ResultInDouble
import com.lasat.dsdco.reducer.problem.DisciplinaryProblem1
import com.lasat.ga.bean.GAResult
import com.lasat.ga.encoder.DoubleArrEncoder
import com.lasat.ga.handler.{GAHandler, PGAHandler}

object DisciplinaryHandler1 {
  def getClosetPoint(targetVal: Array[Double]): ResultInDouble = {
    val result = new ResultInDouble
    if (DisciplinaryProblem1.checkConstraint(targetVal)) {
      result.score = .0
      result.variables = targetVal
      result
    } else {
      DisciplinaryProblem1.targetVal = targetVal
      // get the maximum value and its variables in this region by genetic algorithm
      // The compute resources are limited, so I have to used genetic algorithm instead
      // However, the results of two algorithm are same
      // but parallel genetic algorithm is used for the optimization calculate of the system
      val gaHandler = new GAHandler[Double](DoubleArrEncoder, DisciplinaryProblem1)
      val tempResult: GAResult[Double] = gaHandler.getNormalizedResult

      // change the result into the class that can be get by Java easily
      result.score = tempResult.score
      result.variables = tempResult.variables
      result
    }
  }
}
