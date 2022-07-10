package com.lasat.dsdco.test.handler

import com.lasat.dsdco.test.bean.ResultInDouble
import com.lasat.dsdco.test.problem.SystemProblem
import com.lasat.ga.bean.GAResult
import com.lasat.ga.encoder.DoubleArrEncoder
import com.lasat.ga.handler.PGAHandler

object SystemHandler {
  def getResultInRegion(upperLim: Array[Double], lowerLim: Array[Double]): ResultInDouble = {
    // set upper and lower limit of this problem
    SystemProblem.upperLim = upperLim
    SystemProblem.lowerLim = lowerLim

    // get the maximum value and its variables in this region by parallel genetic algorithm
    /*val pgaHandler = new PGAHandler[Double](DoubleArrEncoder, SystemProblem)
    val tempResult: GAResult[Double] = pgaHandler.getResult*/
    val result = new ResultInDouble

    // change the result into the class that can be get by Java easily
    result.score = - lowerLim(0)
    result.variables = lowerLim
    result
  }
}
