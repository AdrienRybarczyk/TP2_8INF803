import scala.collection.mutable.ArrayBuffer

class Attack(n : String, atm : ArrayBuffer[Int], dmg_f : Int, dmg_d : Int, nb_d : Int , tp : String, crit_mult : Int, portee : Int) extends Serializable {
  var name : String = n
  var armor_test_modifiers : ArrayBuffer[Int] = atm //dégâts différentes attaque de l'arme
  var damage_flat : Int = dmg_f //dégâts effectifs
  var damage_dices : Int = dmg_d //valeur dé dégâts
  var number_dices : Int = nb_d //nombre dé lancé pour les dégâts
  var attack_type : String = tp
  var crit_multiplier : Int = crit_mult
  var range : Int = portee

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
  var speed : Int = sp //déplacement possible en feet
  var team : String = t
  var Buffs : ArrayBuffer[String] = bfs
  var posx : Int = x
  var posy : Int = y
  var posz : Int = z

  override def toString: String = {
    s"name : $name"
  }
}
