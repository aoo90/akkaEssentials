package part1recap

object GeneralRecap extends App {
  val aCondition: Boolean = false
  //cannot reassign val
  //aCondition = true

  var aVariable = 42
  aVariable += 1

  // expressions
  val aConditionedVal = if (aCondition) 42 else 65

  //code block
  var aCodeBlock = {
    if(aCondition) 74
    56
  }

  // types
  // Unit
  val theUnit = println("Hello, Scala")

  def aFunction(x: Int) = x + 1

  // recursion - TAIL recursion
  def factorial (n: Int, acc: Int): Int =
    if(n<=0) acc
    else factorial(n - 1, acc * n)

  // OOP
  class Animal
  class Dog extends Animal
  var aDog: Animal = new Dog
  trait Carnivore{
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = ???
  }
}
