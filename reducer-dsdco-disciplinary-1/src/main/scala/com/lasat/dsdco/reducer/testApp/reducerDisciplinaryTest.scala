package com.lasat.dsdco.reducer.testApp

import com.lasat.dsdco.reducer.bean.ResultInDouble
import com.lasat.dsdco.reducer.handler.DisciplinaryHandler1
import com.lasat.dsdco.reducer.problem.DisciplinaryProblem1

object reducerDisciplinaryTest extends App {
  println(DisciplinaryProblem1.checkConstraint(Array(3.5, 0.7, 17, 7.3, 7.71, 3.35, 5.29)))
}
