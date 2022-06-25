package com.lasat.dsdco.test.testApp

import com.lasat.dsdco.test.bean.ResultInDouble
import com.lasat.dsdco.test.handler.SystemHandler
import com.lasat.dsdco.test.problem.SystemProblem

object testTest extends App {

  val lowerLim = Array(0.0, -100.0)
  val upperLim = Array(100.0, 100.0)

  private val result: ResultInDouble = SystemHandler.getResultInRegion(upperLim, lowerLim)

  println(result.variables.mkString(","))
  println(result.score)

  println(SystemProblem.checkConstraint(result.variables))
}
