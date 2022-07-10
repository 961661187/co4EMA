package reducer.handler

import com.lasat.ga.bean.GAResult
import com.lasat.ga.encoder.DoubleArrEncoder
import com.lasat.ga.handler.{GAHandler, PGAHandler}
import reducer.bean.ResultInDouble
import reducer.problem.DisciplinaryProblem2

object DisciplinaryHandler2 {
  def getClosetPoint(targetVal: Array[Double]): ResultInDouble = {
    val result = new ResultInDouble
    if (DisciplinaryProblem2.checkConstraint(targetVal)) {
      result.score = .0
      result.variables = targetVal
      result
    } else {
        getResultByPGA(targetVal)
    }
  }

  def getResultByPGA(targetVal: Array[Double]): ResultInDouble = {
    val result = new ResultInDouble
    DisciplinaryProblem2.targetVal = targetVal

    // the compute resource is limited, so use GA here instead
    val pgaHandler = new PGAHandler[Double](DoubleArrEncoder, DisciplinaryProblem2)
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

  def getResultByGA(targetVal: Array[Double]): ResultInDouble = {
    val result = new ResultInDouble
    DisciplinaryProblem2.targetVal = targetVal

    // the compute resource is limited, so use GA here instead
    val gaHandler = new GAHandler[Double](DoubleArrEncoder, DisciplinaryProblem2)
    gaHandler.setAccuracy(500, 1000)
    var gaResult : GAResult[Double] = null;

    for (_ <- 0 until 10) {
      val tempResult: GAResult[Double] = gaHandler.getNormalizedResult
      if (gaResult == null || gaResult.score < tempResult.score)
        gaResult = tempResult
    }

    // change the result into the class that can be get by Java easily
    result.score = gaResult.score
    result.variables = gaResult.variables
    result
  }
}
