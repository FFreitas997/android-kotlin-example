import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

private val GAME_DURATION = 1.minutes

suspend fun main() {

    val timeSource = TimeSource.Monotonic

    val gameDuration = timeSource.markNow().plus(GAME_DURATION)

    var countWins = 0
    var countLosses = 0
    var countTies = 0

    do {


        println("\nWelcome to the Rock, Paper, Scissors game! \n")

        println("Enter your choice: \n")

        println(
            "(Please enter " +
                    "${PlayerChoice.ROCK.choiceID} for ${PlayerChoice.ROCK}, " +
                    "${PlayerChoice.PAPER.choiceID} for ${PlayerChoice.PAPER}, " +
                    "${PlayerChoice.SCISSORS.choiceID} for ${PlayerChoice.SCISSORS})\n"
        )

        val userChoice = getResponse(readln().toByte())

        println("\nYou chose: $userChoice")

        delay(1000)

        println("\nComputer is choosing...")

        delay(4000)

        val computerChoice: PlayerChoice = getResponse((1..3).random().toByte())

        println("\nComputer chose: $computerChoice")

        delay(1000)

        println("\nCalculating the result...")

        delay(4000)

        val result = getResult(computerChoice, userChoice)

        println("\nFinal result: $result\n")

        when (result) {
            "You win!" -> countWins++
            "Computer wins!" -> countLosses++
            "It's a tie!" -> countTies++
        }

    } while (timeSource.markNow() < gameDuration);

    println("You won $countWins times, lost $countLosses times and you tied $countTies times in ${GAME_DURATION.toInt(DurationUnit.MINUTES)} minutes!")
}

fun getResult(computerChoice: PlayerChoice, userChoice: PlayerChoice): String =
    when {
        computerChoice == userChoice -> "It's a tie!"
        computerChoice == PlayerChoice.ROCK && userChoice == PlayerChoice.SCISSORS -> "Computer wins!"
        computerChoice == PlayerChoice.SCISSORS && userChoice == PlayerChoice.PAPER -> "Computer wins!"
        computerChoice == PlayerChoice.PAPER && userChoice == PlayerChoice.ROCK -> "Computer wins!"
        else -> "You win!"
    }

fun getResponse(param: Byte) =
    when (param) {
        PlayerChoice.PAPER.choiceID -> PlayerChoice.PAPER
        PlayerChoice.ROCK.choiceID -> PlayerChoice.ROCK
        PlayerChoice.SCISSORS.choiceID -> PlayerChoice.SCISSORS
        else -> throw IllegalArgumentException("$param is an invalid input, please enter a valid option")
    }