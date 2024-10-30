class BankAccount(var accountHolder: String, var balance: Double) {

    private val transactionHistory: MutableList<String> = mutableListOf()

    fun deposit(amount: Double) {
        balance += amount
        transactionHistory.add("$accountHolder deposited $$amount")
    }

    fun currentBalance(): Double {
        return balance
    }

    fun withdraw(amount: Double) {
        if (amount > balance) {
            println("Insufficient funds")
            return
        }
        balance -= amount
        transactionHistory.add("$accountHolder withdrew $$amount")
    }

    fun displayTransactionHistory() {
        println("Transaction History for $accountHolder:")
        transactionHistory.forEach { println(it) }
    }
}