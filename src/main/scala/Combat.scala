import scala.collection.mutable.ArrayBuffer

class Combat extends Serializable {

  //calcul des dégats d'une attaque d'un monstre
  def degats(attackingMonster : Monster, attaque : Attack, ennemi: Monster, attaqueNumber : Int): Int = {
    val De = scala.util.Random
    val valueDe: Int = De.nextInt(20) + 1
    var degatEff = 0

    if(valueDe == 20){
      degatEff = attaque.crit_multiplier*(attaque.number_dices*De.nextInt(attaque.damage_dices)+attaque.damage_flat)-ennemi.damage_reduce
      if(degatEff < 0){
        degatEff = 0
      }
      println("Attaque critique de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      "contre " + Console.BLUE + ennemi.name +
      Console.WHITE + " : " + Console.RED + degatEff + " dégâts" + Console.WHITE)
    }else if(valueDe == 1){
      degatEff = 0

      println("Echec critique de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      "contre " + Console.BLUE + ennemi.name + Console.WHITE)
    }else if(attaque.armor_test_modifiers(attaqueNumber)+valueDe > ennemi.defense){
      degatEff = attaque.number_dices*De.nextInt(attaque.damage_dices)+attaque.damage_flat-ennemi.damage_reduce

      println("Attaque réussie de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      "contre " + Console.BLUE + ennemi.name +
      Console.WHITE + " : " + Console.RED + degatEff + " dégâts" + Console.WHITE)
    }else{
      println("Attaque ratée de " + Console.BLUE + attackingMonster.name + Console.WHITE +
      " avec " + Console.YELLOW + attaque.name + Console.WHITE +
      "contre " + Console.BLUE + ennemi.name + Console.WHITE)
    }
    degatEff
  }

  //calcul distance entre 2 monstres
  def distance(monster1 : Monster, monster2 : Monster):Int = {
    val distance = Math.sqrt(
      (monster1.posy - monster2.posy)  * (monster1.posy - monster2.posy)
        + (monster1.posx - monster2.posx)  * (monster1.posx - monster2.posx)
    ).toInt
    distance
  }

  def bestMove(attackingMonster : Monster, DefenseMonster : Monster):(Int,Int) ={
    val dist = distance(attackingMonster, DefenseMonster)
    var nbAttackRest = -1
    var findMove = false
    var hp_current = DefenseMonster.hp_current
    var resAttack = ArrayBuffer[(Int,Int)]()
    for (i <- attackingMonster.attacks.indices){
      if(!findMove){
        if(11 > dist){
          if (attackingMonster.attacks(i).attack_type == "melee"){
            nbAttackRest = attackingMonster.attacks(i).armor_test_modifiers.size
            findMove = true
            resAttack += useAttack(attackingMonster, DefenseMonster, attackingMonster.attacks(i))
            nbAttackRest = nbAttackRest - resAttack(0)._2
            hp_current = resAttack(0)._1
          }
        }else if(attackingMonster.attacks(i).range > dist){
          nbAttackRest = attackingMonster.attacks(i).armor_test_modifiers.size
          findMove = true
          resAttack += useAttack(attackingMonster, DefenseMonster, attackingMonster.attacks(i))
          nbAttackRest = nbAttackRest - resAttack(0)._2
          hp_current = resAttack(0)._1
        }
      }
    }
    (hp_current, nbAttackRest)
  }

  //utilisation des différentes attaques pour une attaque donnée contre un monstre
  def useAttack(attackingMonster : Monster, DefenseMonster : Monster, attack : Attack):(Int,Int) ={
    var nbAttackUse = 0
    var degatEff = 0
    for(_ <- attack.armor_test_modifiers){
      degatEff = degats(attackingMonster, attack, DefenseMonster, nbAttackUse)
      nbAttackUse+=1
      if(degatEff > 0){
        DefenseMonster.hp_current = DefenseMonster.hp_current - degatEff
        if(DefenseMonster.hp_current < 0){//On sort de la fonction si l'ennemi est mort
          DefenseMonster.hp_current = 0
          return (DefenseMonster.hp_current,nbAttackUse)
        }
      }
    }
    (DefenseMonster.hp_current,nbAttackUse)
  }
}