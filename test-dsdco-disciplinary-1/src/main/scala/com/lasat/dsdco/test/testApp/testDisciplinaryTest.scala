package com.lasat.dsdco.test.testApp

import com.lasat.dsdco.test.handler.DisciplinaryHandler1
import com.lasat.dsdco.test.problem.DisciplinaryProblem1

object testDisciplinaryTest extends App {
  println(DisciplinaryProblem1.checkConstraint(Array(2.7851526884596307, -22.634785877966483)))
  //println(DisciplinaryHandler1.getClosetPoint(Array(2.741, -22.3)).variables.mkString(","))
}
