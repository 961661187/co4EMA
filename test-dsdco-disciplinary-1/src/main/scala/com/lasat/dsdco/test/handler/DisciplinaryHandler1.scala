package com.lasat.dsdco.test.handler

import com.lasat.dsdco.test.bean.ResultInDouble
import com.lasat.dsdco.test.problem.DisciplinaryProblem1
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

      // the compute resource is limited, so use GA here instead
      val pgaHandler = new PGAHandler[Double](DoubleArrEncoder, DisciplinaryProblem1)
      var gaResult : GAResult[Double] = null;

      for (_ <- 0 until 3) {
        val tempResult: GAResult[Double] = pgaHandler.getResult
        if (gaResult == null || gaResult.score < tempResult.score)
          gaResult = tempResult
      }

      // change the result into the class that can be get by Java easily
      result.score = gaResult.score
      result.variables = gaResult.variables
      result
    }
  }
}
