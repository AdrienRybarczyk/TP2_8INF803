class Combat{

  def degats(attackingMonster : Monster, attaque : Attack, ennemi: Monster, attaqueNumber : Int): Int = {
    val De = scala.util.Random
    val valueDe: Int = De.nextInt(21)
    var degatEff = 0

    if(valueDe == 20){
      degatEff = attaque.crit_multiplier*(attaque.number_dices*De.nextInt(attaque.damage_dices)+attaque.damage_flat)-ennemi.damage_reduce

      println("Attaque critique de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      " contre " + Console.BLUE + ennemi.name +
      Console.WHITE + " : " + Console.RED + degatEff + " dégâts" + Console.WHITE)
    }else if(valueDe == 0){
      degatEff = 0

      println("Echec critique de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      " contre " + Console.BLUE + ennemi.name + Console.WHITE)
    }else if(attaque.armor_test_modifiers(attaqueNumber)+valueDe > ennemi.defense){
      degatEff = attaque.number_dices*De.nextInt(attaque.damage_dices)+attaque.damage_flat-ennemi.damage_reduce

      println("Attaque réussie de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      " contre " + Console.BLUE + ennemi.name +
      Console.WHITE + " : " + Console.RED + degatEff + " dégâts" + Console.WHITE)
    }else{
      println("Attaque ratée de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      " contre " + Console.BLUE + ennemi.name + Console.WHITE)
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

  def bestMove(attackingMonster : Monster, DefenseMonster : Monster):Int ={
    val dist = distance(attackingMonster, DefenseMonster)
    var nbAttackRest = 0
    var findMove = false
    for (i <- attackingMonster.attacks.indices){
      if(!findMove){
        if(11 > dist){
          if (attackingMonster.attacks(i).attack_type == "melee"){
            nbAttackRest = attackingMonster.attacks(i).armor_test_modifiers.size
            findMove = true
            nbAttackRest = nbAttackRest - useAttack(attackingMonster, DefenseMonster, attackingMonster.attacks(i))
          }
        }else if(attackingMonster.attacks(i).range > dist){
          nbAttackRest = attackingMonster.attacks(i).armor_test_modifiers.size
          findMove = true
          nbAttackRest = nbAttackRest - useAttack(attackingMonster, DefenseMonster, attackingMonster.attacks(i))
        }
      }
    }
    nbAttackRest
  }

  def useAttack(attackingMonster : Monster, DefenseMonster : Monster, attack : Attack):Int ={
    var nbAttackUse = 0
    var degatEff = 0
    for(_ <- attack.armor_test_modifiers){
      degatEff = degats(attackingMonster, attack, DefenseMonster, nbAttackUse)
      nbAttackUse+=1
      if(degatEff > 0){
        DefenseMonster.hp_current = DefenseMonster.hp_current - degatEff
        if(DefenseMonster.hp_current < 0){//On sort de la fonction si l'ennemi est mort
          return nbAttackUse
        }
      }
    }
    nbAttackUse
  }
}