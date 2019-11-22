import scala.collection.mutable.ArrayBuffer


class Attack(n : String, atm : ArrayBuffer[Int], dmg_f : Int, dmg_d : Int, nb_d : Int , tp : String, crit_mult : Int) extends Serializable {
  var name : String = n
  var armor_test_modifiers : ArrayBuffer[Int] = atm //degats differentes attaque de l arme
  var damage_flat : Int = dmg_f //degats effectifs
  var damage_dices : Int = dmg_d //valeur dé degats
  var number_dices : Int = nb_d //nombre dé lancé pour les degats
  var attack_type : String = tp
  var crit_multiplier : Int = crit_mult

  override def toString: String = {
    s"name : $name"
  }
}

class Monster(n :  String, hp_m : Int, hp_c : Int, df : Int, dr : Int, atks : ArrayBuffer[Attack], sp : Int, t : String, bfs : ArrayBuffer[String], x : Int, y : Int, z : Int) extends Serializable {
  var name : String = n
  var hp_max : Int = hp_m
  var hp_current : Int = hp_c
  var defense : Int = df
  var damage_reduce : Int = dr
  var attacks : ArrayBuffer[Attack] = atks
  var speed : Int = sp //deplacement possible en feet
  var team : String = t
  var Buffs : ArrayBuffer[String] = bfs
  var posx : Int = x
  var posy : Int = y
  var posz : Int = z

  override def toString: String = {
    s"name : $name"
  }
}

object Main {

  def main(args: Array[String]) : Unit = {
    println("***************")

    var tabURL = ArrayBuffer[String]()

    // Friendly team
    tabURL.append("https://www.d20pfsrd.com/bestiary/monster-listings/outsiders/angel/solar/")

    // Enemy team
    tabURL.append("https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-1/orc-worg-rider/")
    tabURL.append("https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-12/brutal-warlord-half-orc-fighter-13/")
    tabURL.append("https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-10/double-axe-fury-half-orc-barbarian-11/")

    /*
    for (element <- tabURL) {
      println("Crawling to : ", element)
    }
    */

    val monster_collection = ArrayBuffer[Monster]()

    // Solar creation
    val solar_attacks = ArrayBuffer[Attack]()
    val armor_test_modifiers1 = ArrayBuffer(35,30,25,20)
    val solar_attack1 = new Attack("Greatsword", armor_test_modifiers1, 18, 3, 2, tp = "melee",2)
    solar_attacks.append(solar_attack1)
    val armor_test_modifiers2 = ArrayBuffer(31,26,21,16)
    val solar_attack2 = new Attack("Longbow", armor_test_modifiers2, 14, 2,3, tp = "ranged",3)
    solar_attacks.append(solar_attack2)

    val solar_buffs = ArrayBuffer[String]()
    val solar = new Monster("Solar", 363, 363, 44, 15, solar_attacks, sp = 50, "Good", solar_buffs, 0, 0, 0)
    monster_collection.append(solar)

  }
}

