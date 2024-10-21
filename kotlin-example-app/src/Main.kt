import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {

    val example = ClassExample("")
    example.function()
    example.functionInterface()


    println("Hello World!")

    val list = listOf(1, 2, 3, 4, 5)
    println(list)

    val mutableList = mutableListOf(1, 2, 3, 4, 5)
    println(mutableList)

    val set = setOf(1, 2, 3, 4, 5)
    println(set)

    val mutableSet = mutableSetOf(5, 4, 3, 1, 2)
    println(mutableSet)

    val map = mapOf(1 to "one", 2 to "two", 3 to "three").also {
        println(it)
    }

    val mutableMap = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
    println(mutableMap)

    println("List size: ${list.size}")

    when (list.contains(3)) {
        true -> println("List contains 3")
        false -> println("List does not contain 3")
    }

    val result = list.filter { it % 2 == 0 }

    println(result)

    for (i in 1..10) {
        print("${i},\t")
    }

    println()

    for (i in 1..<10) {
        print("${i},\t")
    }

    println()

    for (i in 10 downTo 1) {
        print("${i},\t")
    }

    println()

    for (pair in map) {
        print("${pair.key} -> ${pair.value}, ")
    }

    // Courotines

    println("Start")

    runBlocking {
        repeat(5) {
            launch {
                delay(5000)
                println(message = "World!")
            }
        }
        println("Hello,")
    }
}