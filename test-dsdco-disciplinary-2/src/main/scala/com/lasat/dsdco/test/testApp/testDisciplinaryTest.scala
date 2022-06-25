package com.lasat.dsdco.test.testApp

import com.lasat.dsdco.test.handler.DisciplinaryHandler2
import com.lasat.dsdco.test.problem.DisciplinaryProblem2

object testDisciplinaryTest extends App {
  println(DisciplinaryProblem2.checkConstraint(Array(2.741, -22.4)))
  println(DisciplinaryHandler2.getClosetPoint(Array(2.741, -22.3)).variables.mkString(","))
}
