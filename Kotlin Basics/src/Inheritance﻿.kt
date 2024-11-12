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
 *
 * ### Visibility Modifiers
 *
 * 1. **Public**: The class, method, or property is accessible from anywhere.
 * 2. **Protected**: The class, method, or property is accessible within the same package and subclasses.
 * 3. **Private**: The class, method, or property is accessible within the same file.
 * 4. **Internal**: The class, method, or property is accessible within the same module.
 *
 * ### Nested classes and Inner classes
 *
 * 1. **Nested classes**: A class declared inside another class is called a nested class. It cannot access the outer class members.
 * 2. **Inner classes**: An inner class is a nested class that is marked with the `inner` keyword. It can access the outer class members.
 *
 * ### Sealed classes
 *
 * Sealed classes are used to represent restricted class hierarchies. They are useful when a value can have one of a fixed set of types.
 *
 * Sealed classes are useful:
 *
 *1. **Limited class inheritance is desired**: You have a predefined, finite set of subclasses that extend a class, all of which are known at compile time.
 *2. **Type-safe design is required**: Safety and pattern matching are crucial in your project. Particularly for state management or handling complex conditional logic. For an example, check out Use sealed classes with when expressions.
 *3. **Working with closed APIs**: You want robust and maintainable public APIs for libraries that ensure that third-party clients use the APIs as intended.
 *
 * ### Interfaces
 *
 * Interfaces are used to define a contract for classes. They can contain abstract methods, properties, and default implementations.
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

class OuterClass {

    val outerProperty = "Outer property"

    class NestedClass {

        val innerProperty = "Inner property"

        fun doSomething() {
            println("Doing something in the nested class")
        }
    }
}


class OuterClass1 {

    val outerProperty = "Outer property"

    inner class NestedClass {

        val innerProperty = "Inner property"

        fun doSomething() {
            println(outerProperty)
            println("Doing something in the nested class")
        }
    }
}


fun main() {

    val myClass = DerivedClass("Kotlin")
    myClass.doSomething()
    println(myClass.myContext)

    val dataClass = DataClass("Data")
    println(dataClass)
}