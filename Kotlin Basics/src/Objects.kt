fun main() {

    // Accessing the object
    ObjectExample.increment()
    ObjectExample.increment()
    println(ObjectExample.getCount()) // 2

    // Resetting the object
    ObjectExample.reset()
    println(ObjectExample.getCount()) // 0

    // Accessing the companion object
    val car = Car.create("Toyota", "Corolla")
    println(car.getCarDetails()) // Toyota Corolla

}

/**
 * # Kotlin Objects
 *
 * Object is used to create a singleton class, which means only one instance of the class can be created.
 * It is used to create a class with only one instance.
 *
 * 1. **Using singletons for shared resources**: You need to ensure that only one instance of a class exists throughout the application. For example, managing a database connection pool.
 *
 * 2. **Creating factory methods**: You need a convenient way to create instances efficiently. Companion objects allow you to define class-level functions and properties tied to a class, simplifying the creation and management of these instances.
 *
 * 3. **Modifying existing class behavior temporarily**: You want to modify the behavior of an existing class without the need to create a new subclass. For example, adding temporary functionality to an object for a specific operation.
 *
 * 4. **Type-safe design is required**: You require one-time implementations of interfaces or abstract classes using object expressions. This can be useful for scenarios like a button click handler.
 */
object ObjectExample {

    private var count = 0

    fun increment() {
        count++
    }

    fun getCount(): Int {
        return count
    }

    fun reset() {
        count = 0
    }
}

/**
 * # Companion Objects
 *
 * Companion objects are used to define class-level functions and properties tied to a class.
 * They are similar to static methods in Java, but they can also be used to define factory methods.
 *
 * 1. **Factory methods**: You need a convenient way to create instances efficiently.
 *
 * 2. **Accessing private members**: You need to access private members of a class.
 *
 * 3. **Type-safe design is required**: You require one-time implementations of interfaces or abstract classes using object expressions.
 *
 * 4. **Modifying existing class behavior temporarily**: You want to modify the behavior of an existing class without the need to create a new subclass.
 */
class Car(private val carName: String, private val carModel: String) {

    companion object Factory{
        fun create(carName: String, carModel: String): Car = Car(carName, carModel)
    }

    fun getCarDetails() = "$carName $carModel"
}
