package com.lasat.ga.bean

import scala.reflect.ClassTag

class GAResult[T: ClassTag] {
  var variables: Array[T] = _
  var score: Double = Integer.MIN_VALUE.toDouble
}
