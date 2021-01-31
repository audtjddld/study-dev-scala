// 엄격성과 나태성

def square(x: Double) :Double = x * x;

square(41.0 + 1.0);


square(sys.error("failure"));


/**==========================
         - 엄격성과 나태성 -
     ==========================*/

// 비엄격 함수를 만드는 방법: 인자에 thunk 사용, 스칼라가 제공하는 편의 구문(: => A) 이용, || 과 && 연산자 사용

// 인자를 평가되지 않은 채로 함수 본문에 전달하기 위한 방법으로 '() => A' 형태의 함수를 사용할 수 있다.
// 이러한 형태를 thunk 라고 하며 함수 본문에서 해당 함수를 호출하여 인자가 평가되도록 할 수 있다.
def if2[A](cond: Boolean, onTrue: () => A, onFalse: () => A): A =
  if (cond) onTrue() else onFalse()             //> if2: [A](cond: Boolean, onTrue: () => A, onFalse: () => A)A

if2(true, () => 1, () => {print("@");0})        //> res0: Int = 1

// 어떤 인수의 형식 바로 앞에 '=>' 를 붙이면 해당 인수는 평가되지 않은 채로 함수 본문에 전달된다. ( 함수 시그니쳐의 onTrue: => A )
// 함수 본문에서 해당 인수를 평가하기 위해 특별한 구문이 필요하지 않으며 '식별자'를 그대로 사용한다. ( 함수 본문의 onTrue )
def if3[A](cond: Boolean, onTrue: => A, onFalse: => A): A =
  if (cond) onTrue else onFalse                 //> if3: [A](cond: Boolean, onTrue: => A, onFalse: => A)A

if3(true, 1, {print("@");0})                    //> res1: Int = 1
if3(true, () => 1, () => 0)                     //> res2: () => Int = <function0>
if3(true, () => 1, () => 0)()                   //> res3: Int = 1
if3(true, 1, if3(true, 1, 1))                   //> res4: Int = 1


def twice(b: Boolean, i: => Int) = if (b) i+i else 0
//> twice: (b: Boolean, i: => Int)Int
// val 선언에 lazy 를 추가하면 우변의 평가가 처음 참조되는 시점까지 지연되며 평가 결과를 캐쉬에 담아두고 이후 참조 시 캐시된 값을 사용한다.
def twice2(b: Boolean, i: => Int) = {
  lazy val j = i
  if (b) j+j else 0
}                                               //> twice2: (b: Boolean, i: => Int)Int

twice(true, { print("@"); 1+1 })                //> @@res5: Int = 4
twice2(true, { print("@"); 1+1 })               //> @res6: Int = 4






