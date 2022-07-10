package com.lasat.dsdco.reducer.handler

import com.lasat.dsdco.reducer.bean.ResultInDouble
import com.lasat.dsdco.reducer.problem.SystemProblem
import com.lasat.ga.bean.GAResult
import com.lasat.ga.encoder.DoubleArrEncoder
import com.lasat.ga.handler.{GAHandler, PGAHandler}

object SystemHandler {
  def getResultInRegion(upperLim: Array[Double], lowerLim: Array[Double]): ResultInDouble = {
    // set upper and lower limit of this problem
    SystemProblem.upperLim = upperLim
    SystemProblem.lowerLim = lowerLim

    // get the maximum value and its variables in this region by parallel genetic algorithm
    val pgaHandler = new PGAHandler[Double](DoubleArrEncoder, SystemProblem)
    val tempResult: GAResult[Double] = pgaHandler.getResult
    val result = new ResultInDouble

    // change the result into the class that can be get by Java easily
    result.score = tempResult.score
    result.variables = tempResult.variables
    result
  }
}
