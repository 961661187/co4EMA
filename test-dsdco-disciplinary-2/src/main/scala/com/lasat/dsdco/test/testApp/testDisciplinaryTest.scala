package com.lasat.dsdco.test.testApp

import com.lasat.dsdco.test.handler.DisciplinaryHandler2
import com.lasat.dsdco.test.problem.DisciplinaryProblem2

object testDisciplinaryTest extends App {
  println(DisciplinaryProblem2.checkConstraint(Array(2.7851526884596307, -22.634785877966483)))
  //println(DisciplinaryHandler2.getClosetPoint(Array(0.005, 70)).variables.mkString(","))
}
