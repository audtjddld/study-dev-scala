// 엄격성과 나태성

def square(x: Double) :Double = x * x;

square(41.0 + 1.0);


square(sys.error("failure"));



Stream(1,2,3,4).map(_ + 10).filter(_ % 2)



