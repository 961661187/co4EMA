package com.lasat.dsdco.test.testApp

import com.lasat.dsdco.test.handler.DisciplinaryHandler1
import com.lasat.dsdco.test.problem.DisciplinaryProblem1

object testDisciplinaryTest extends App {
  println(DisciplinaryProblem1.checkConstraint(Array(2.741, -22.4)))
  println(DisciplinaryHandler1.getClosetPoint(Array(2.741, -22.3)).variables.mkString(","))
}
