fun main() {
    val account = BankAccount("Alice", 100.0)

    account.deposit(50.0)
    account.withdraw(25.0)

    account.displayTransactionHistory()

    println("Current balance: $${account.balance}")
}