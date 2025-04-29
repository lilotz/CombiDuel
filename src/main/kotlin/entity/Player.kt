package entity

data class Player(
    val name : String,
    var score : Int,
//    var actionCount : Int,
    var actions : MutableList<Action>,
    val disposalArea : MutableList<Card>,
    val handCards : MutableList<Card>)
{
}