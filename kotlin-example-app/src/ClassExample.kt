class ClassExample(val name: String): InterfaceExample {

    private val property: Int = 10

    fun function() {
        println("Hello, $name")
    }

    override fun functionInterface() {
    }
}


interface InterfaceExample {
    val propertyInterface: Int
        get() = 10
    fun functionInterface()
}