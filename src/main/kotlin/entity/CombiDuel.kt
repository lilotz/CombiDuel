package entity

class CombiDuel(
    val Player1 : Player,
    val Player2 : Player,
    var passCheck : Boolean,
    var currentPlayer : Int,
    var drawStack : ArrayDeque<Card>,
    var tradeDeck : MutableList<Card>
)
{}