class Combat{

  //arc degat triple
  def degats(attaque : Attack, ennemi: Monster, attaqueNumber : Int): Int = {
    val De = scala.util.Random
    val valueDe: Int = De.nextInt(21)
    var degatEff = 0

    if(valueDe == 20){
      degatEff = attaque.crit_multiplier*(attaque.number_dices*De.nextInt(attaque.damage_dices)+attaque.damage_flat)-ennemi.damage_reduce
    }else if(valueDe == 0){
      degatEff = 0
    }else if(attaque.armor_test_modifiers(attaqueNumber)+valueDe > ennemi.defense){
      degatEff = attaque.number_dices*De.nextInt(attaque.damage_dices)+attaque.damage_flat-ennemi.damage_reduce
    }
    degatEff
  }

  def distance(monster1 : Monster, monster2 : Monster):Int = {
    val distance = Math.sqrt(
      (monster1.posy - monster2.posy)  * (monster1.posy - monster2.posy)
        + (monster1.posx - monster2.posx)  * (monster1.posx - monster2.posx)
    ).toInt
    distance
  }


}