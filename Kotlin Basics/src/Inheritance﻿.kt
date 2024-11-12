/**
 * # Inheritance in Kotlin
 *
 * Inheritance is a mechanism in which a new class inherits properties and methods from an existing class.
 *
 * 1. **Open keyword**: The `open` keyword is used to allow a class to be inherited by other classes.
 * 2. **Override keyword**: The `override` keyword is used to override a method or property in a derived class.
 * 3. **Super keyword**: The `super` keyword is used to call the superclass implementation of a method or property.
 * 4. **init block**: The `init` block is used to initialize properties of a class.
 * 5. **Abstract classes**: Abstract classes are used to define a common interface for subclasses. They cannot be instantiated.
 * 6. **Data classes**: Data classes are used to store data. They automatically generate `equals()`, `hashCode()`, and `toString()` methods. (DTOs)
 */

interface Contract {

    fun doSomething()
}

open class BaseClass(val name: String) : Contract {

    init {
        println("Initializing the base class")
    }

    open val myContext = "BaseClass"

    override fun doSomething() {
        println("Doing something in the base class, $name")
    }
}

class DerivedClass(name: String) : BaseClass(name) {

    override val myContext = "DerivedClass"

    override fun doSomething() {
        super.doSomething()
        println(super.myContext)
        println("Doing something in the derived class, $name")
    }
}

abstract class AbstractClass {

    fun doSomething() {
        println("Doing something in the abstract class")
    }

    abstract fun doSomethingElse()
}

class DerivedAbstractClass : AbstractClass() {

    override fun doSomethingElse() {
        println("Doing something else in the derived abstract class")
    }
}

data class DataClass(val name: String)

fun main() {

    val myClass = DerivedClass("Kotlin")
    myClass.doSomething()
    println(myClass.myContext)

    val dataClass = DataClass("Data")
    println(dataClass)
}