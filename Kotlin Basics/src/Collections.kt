import java.util.*

/**
 * # Arrays
 * An array is used to store a fixed-size sequential collection of same type elements.
 * Arrays are mutable in nature. The size of an array is defined at the time of creation.
 * The Array class represents arrays in Kotlin.
 * The Array class has a get and set function that allows you to access and modify elements at specific indices.
 * Arrays can be specifically typed or untyped. (for example, IntArray, DoubleArray, BooleanArray, etc.)
 * # Collections
 *
 * A collection is a group of related items. In Kotlin, collections are used to store, retrieve, manipulate, and aggregate data.
 *
 * ### Types of Collections
 *
 * 1. **List**: An ordered collection that allows duplicate elements.
 * 2. **Set**: An unordered collection that does not allow duplicate elements.
 * 3. **Map**: A collection of key-value pairs.
 *
 * Collections can be:
 *
 * 1. **Mutable**: Elements can be added, removed, or updated.(MutableList, MutableSet, MutableMap)
 * 2. **Immutable**: Elements cannot be added, removed, or updated. (List, Set, Map)
 */

fun main(){

    // Creating an array
    val numbers = intArrayOf(1, 2, 3, 4, 5)
    println(numbers[2]) // 3

    // Creating a list
    val names = listOf("Alice", "Bob", "Charlie")
    println(names[1]) // Bob

    // Creating a set
    val fruits = setOf("Apple", "Banana", "Orange")
    println(fruits.contains("Banana")) // true

    // Creating a map
    val ages = mapOf("Alice" to 25, "Bob" to 30, "Charlie" to 35)
    println(ages["Bob"]) // 30

    // Creating a mutable list
    val mutableNames = mutableListOf("Alice", "Bob", "Charlie")
    mutableNames.add("David")
    println(mutableNames) // [Alice, Bob, Charlie, David]

    // Creating a mutable set
    val mutableFruits = mutableSetOf("Apple", "Banana", "Orange")
    mutableFruits.add("Mango")
    println(mutableFruits) // [Apple, Banana, Orange, Mango]

    // Creating a mutable map
    val mutableAges = mutableMapOf("Alice" to 25, "Bob" to 30, "Charlie" to 35)
    mutableAges["David"] = 40
    println(mutableAges) // {Alice=25, Bob=30, Charlie=35, David=40}

    // Iterating over a list
    for (name in names) {
        println(name)
    }

    // Iterating over a set
    for (fruit in fruits) {
        println(fruit)
    }

    // Iterating over a map
    for ((name, age) in ages) {
        println("$name is $age years old")
    }

    // Filtering a list
    val filteredNames = names.filter { it.startsWith("A") }
    println(filteredNames) // [Alice]

    // Mapping a list
    val mappedNames = names.map { it.uppercase(Locale.getDefault()) }
    println(mappedNames) // [ALICE, BOB, CHARLIE]

    // Sorting a list
    val sortedNames = names.sorted()
    println(sortedNames) // [Alice, Bob, Charlie]

    // Grouping a list
    val groupedNames = names.groupBy { it.first() }
    println(groupedNames) // {A=[Alice], B=[Bob, Charlie]}

    // Checking if all elements satisfy a condition
    val allNamesStartWithA = names.all { it.startsWith("A") }
    println(allNamesStartWithA) // false
}