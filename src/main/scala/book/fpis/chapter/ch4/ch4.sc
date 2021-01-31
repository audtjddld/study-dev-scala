
object Option {
  def failingFn(i: Int): Int = {
    val y: Int = throw new Exception("fail!") // `val y: Int = ...` declares `y` as having type `Int`, and sets it equal to the right hand side of the `=`.
    try {
      val x = 42 + 5
      x + y
    }
    catch {
      case e: Exception => 43
    } // A `catch` block is just a pattern matching block like the ones we've seen. `case e: Exception` is a pattern that matches any `Exception`, and it binds this value to the identifier `e`. The match returns the value 43.
  }

  def failingFn2(i: Int): Int = {
    try {
      val x = 42 + 5
      x + ((throw new Exception("fail!")): Int) // A thrown Exception can be given any type; here we're annotating it with the type `Int`
    }
    catch {
      case e: Exception => 43
    }
  }

  // http://www.scala-lang.org/api/current/scala/collection/Seq.html
  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length) // <-- 평균

  /**
   * ex 4.2: variance 함수를 flatMap을 이용해 구현하라.
   * 순차열의 평균이 m이라 할 때, 분산은 각 요소 x에 대한 math.pow(x-m, 2)
   * 들의 평균이다.
   *
   * 분산(variance) = sum( (변량-평균)^2 ) / 변량개수
   * 예를들면 값들이 1,3,5 가 있다면 평균 = 3, 변량개수 = 3, 분산 = ( (1-3)^2 + (3-3)^2 + (1-5)^2 ) / 3
   */
  def variance(xs: Seq[Double]): Option[Double] =
    mean(xs).flatMap(m => mean(xs.map(x => math.pow(x - m, 2))))

  // http://stackoverflow.com/questions/28375449/meaning-of-underscore-in-lifta-bf-a-b-optiona-optionb-map-f
  def lift[A, B](f: A => B): Option[A] => Option[B] = _ map f //  =  _.map(f)  =  (a) => a.map(f)

  // a를 평가하는 도중 예외가 발생하면 None 으로 변환하기 위해 엄격하지 않은 방식으로 A 인수를 받는다.
  // ( 표현식 a를 함수 본문에서 사용되는 시점에 평가되도록 만든다. )
  def Try[A](a: => A): Option[A] =
    try Some(a)
    catch {
      case e: Exception => None
    }

  def Try2[A](a: A): Option[A] =
    try Some(a)
    catch {
      case e: Exception => None
    }

  // ex 4.3
  def map2[A, B, C](oa: Option[A], ob: Option[B])(f: (A, B) => C): Option[C] =
    oa.flatMap(a => ob.map(b => f(a, b)))

  // ex 4.4
  // http://www.scala-lang.org/api/current/scala/collection/immutable/List.html
  def sequence[A](a: List[Option[A]]): Option[List[A]] =
    a.foldRight(Some(Nil): Option[List[A]])((oa, ob) => ob.flatMap(b => oa.map(a => a :: b)))
  // ->  List 연산자 ::


  // ex 4.5
  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
    a.foldRight(Some(Nil): Option[List[B]])((a, ob) => ob.flatMap(b => f(a).map(aa => aa :: b)))

  def sequence2[A](a: List[Option[A]]): Option[List[A]] =
    traverse(a)(aa => aa)
}

println(Option.sequence(List(Some(1), Some(2), Some(3))))


sealed trait Either[+E, +A] {

  //ex 4.6
  def map[B](f: A => B): Either[E, B] = this match {
    case Left(e) => Left(e)
    case Right(a) => Right(f(a))
  }

  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
    case Left(e) => Left(e)
    case Right(a) => f(a)
  }

  def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = this match {
    case Left(e) => b
    case Right(a) => this
  }

  def map2[EE >: E, B, C](eb: Either[EE, B])(f: (A, B) => C): Either[EE, C] =
    this.flatMap(a => eb.map(b => f(a, b)))
}

case class Left[+E](get: E) extends Either[E, Nothing]

case class Right[+A](get: A) extends Either[Nothing, A]
