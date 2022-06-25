package com.lasat.dsdco.reducer.testApp

import com.lasat.dsdco.reducer.bean.ResultInDouble
import com.lasat.dsdco.reducer.handler.SystemHandler
import com.lasat.dsdco.reducer.problem.SystemProblem

object reducerTest extends App {

  val lowerLim = Array(2.6, 0.7, 17, 7.3, 7.3, 2.9, 5.0)
  val upperLim = Array(3.6, 0.8, 28, 8.3, 8.3, 3.9, 5.5)

  private val result: ResultInDouble = SystemHandler.getResultInRegion(upperLim, lowerLim)

  println(result.variables.mkString(","))
  println(result.score)

  println(SystemProblem.checkConstraint(result.variables))
}
