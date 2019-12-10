import org.apache.spark._
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer
import org.apache.spark.graphx._
import org.jsoup.Jsoup

class node(var id_m : Int, var m: Monster, len : Int )extends Serializable
{
  var id: Int = id_m
  var monster: Monster = m
  var taille : Int = len
  var adjlist = new Array[Int](taille)
  var nearestEnnemy: Option[Monster] = None : Option[Monster]

  def printadjlist: String = {
    var output = "adjlist : "
    adjlist.foreach(output += _)
    output
  }

  override def toString: String = {
    s" $monster"
  }
}

object creation extends App{
  val conf = new SparkConf().setAppName("Creation Graphe").setMaster("local[*]")
  val sc = SparkContext.getOrCreate(conf)
  sc.setLogLevel("ERROR")

  def create_attack(detail_attack : Array[String], type_weapon : String): Attack = {
    var weapon_name = ""
    var damage_flat = 0
    var number_dices = 0
    var damage_dice = 0
    var crit_mult = 1
    val armor_test_modifiers = ArrayBuffer[Int]()
    var booleanTestName = false
    for(i <- detail_attack.indices){
      if(!detail_attack(i)(1).isDigit && !booleanTestName){
        weapon_name+= detail_attack(i) + " "
      }else if(i != 0){
        booleanTestName = true
      }
      if(detail_attack(i) contains "("){
        var tmp_degat = detail_attack(i).substring(1,detail_attack(i).length)
        if(detail_attack(i) contains ")"){
          tmp_degat = detail_attack(i).substring(1,detail_attack(i).length-1)
        }
        if(tmp_degat contains "x"){
          crit_mult = tmp_degat.split("x")(1).toInt
          tmp_degat = tmp_degat.split("/")(0)
        }
        if(tmp_degat contains "×"){
          crit_mult = tmp_degat.split("×")(1).toInt
          tmp_degat = tmp_degat.split("/")(0)
        }
        if(tmp_degat contains "/"){
          tmp_degat = tmp_degat.split("/")(0)
        }
        if(tmp_degat contains "+"){
          damage_flat = tmp_degat.split("""\+""")(1).toInt
          val tmp_dice = tmp_degat.split("""\+""")(0)
          number_dices = tmp_dice.split("d")(0).toInt
          damage_dice = tmp_dice.split("d")(1).toInt
        }else{
          number_dices = tmp_degat.split("d")(0).toInt
          damage_dice = tmp_degat.split("d")(1).toInt
        }
      }else if(detail_attack(i) contains "/"){
        val tmp_modifiers = detail_attack(i).split("/")
        for(j <- tmp_modifiers){
          val value_modifier: String = j.substring(1)
          armor_test_modifiers+= value_modifier.toInt
        }
      }else if(detail_attack(i) contains "+"){
        if(i>0){
          armor_test_modifiers+= detail_attack(i).split("""\+""")(1).toInt
        }
      }
    }
    var range = 0
    if(type_weapon == "melee"){
      range = 10
    }else if(type_weapon == "ranged"){
      range = 100
    }
    val attack = new Attack(weapon_name, armor_test_modifiers, damage_flat, damage_dice, number_dices, type_weapon,crit_mult, range)
    attack
  }

  def create_monster(monster_url : String, monster_name : String): Monster = {
    val doc = Jsoup.connect(monster_url).get()
    val statblock = doc.select(".statblock")

    // HP max
    var defense_block = statblock.select("p").get(3)
    if (!(defense_block.html() contains "<b>AC</b>")) {
      defense_block = statblock.select("p").get(4)
    }
    val defense_stats = defense_block.text().split(" |,|;")
    val defense = defense_stats(1).toInt

    var hp = 0
    var dr = 0
    var current_index = 0
    for (element <- defense_stats) {
      current_index = defense_stats.indexOf(element)
      if (element == "hp") {
        hp = defense_stats(current_index+1).toInt
      }
      if (element == "DR") {
        dr = defense_stats(current_index+1).split("/")(0).toInt
      }
    }

    //Team
    val info_block = statblock.select("p").get(1)
    var team = "B"//B for Bad
    if (info_block.html() contains "good") {
      team = "G"//Good
    }

    //Speed
    var offense_block = statblock.select("p").get(5)
    if (!(offense_block.html() contains "<b>Speed</b>")) {
      offense_block = statblock.select("p").get(6)
    }

    val offense_stats = offense_block.text().split(" |,|;")
    val speed = offense_stats(1).toInt

    //Attacks
    val attacks_stats = offense_block.text().split("Melee ")
    val attack_block_tmp = attacks_stats(1)
    val split_melee_ranged = attack_block_tmp.split("Ranged ")
    val melee_block = split_melee_ranged(0)

    val melee_attack_block: String = melee_block.split(" or ")(0)
    val deuxieme_attack_block = melee_attack_block.split(", ")
    var melee_detail_block = deuxieme_attack_block(0).split(" ")
    val arrayAttack = ArrayBuffer[Attack]()

    val attackCac = create_attack(melee_detail_block, "melee")
    arrayAttack+= attackCac

    if(deuxieme_attack_block.size > 1){
      melee_detail_block = deuxieme_attack_block(1).split(" ")
      val SecondAttackCac = create_attack(melee_detail_block, "melee")
      arrayAttack+= SecondAttackCac
    }

    var ranged_block = split_melee_ranged(1)
    if (ranged_block contains "Special Attacks") {
      ranged_block = ranged_block.split("Special Attacks")(0)
    }
    if (ranged_block contains "plus") {
      ranged_block = ranged_block.split("plus")(0)
      ranged_block = ranged_block+")"
      ranged_block = ranged_block.replace(" )",")")
    }
    val indexParenthese = ranged_block.indexOf("(")
    if(ranged_block.indexOf("(",indexParenthese+1)!= -1){
      val endFirstParenthese = ranged_block.indexOf(")")
      ranged_block = ranged_block.substring(0,indexParenthese) + ranged_block.substring(endFirstParenthese+1)
      ranged_block = ranged_block.replace("  "," ")
    }
    val ranged_detail_block = ranged_block.split(" ")
    val attackDistance = create_attack(ranged_detail_block, "ranged")
    arrayAttack+= attackDistance

    var x,y,z = 0
    val r = new scala.util.Random
    x = r.nextInt(125)
    if(team == "G"){//équipe gentille elle sera dans la moitié 0 - 125 en x
      y = r.nextInt(250)
      z = 0
    }else{//équipe méchante elle sera à 110 ft en x de la team gentille
      x = x + 110
      y = r.nextInt(250)
      z = 0
    }

    val new_monster = new Monster(monster_name, hp, hp, defense, dr, arrayAttack, speed, team, buffs, x, y, z)
    new_monster
  }

  val buffs = ArrayBuffer[String]()

  var tabMonster = ArrayBuffer[Monster]()
  var tabMonsterDead = ArrayBuffer[Monster]()
  var nodes = ArrayBuffer[(VertexId,node)]()
  val combat = new Combat()

  var configurationCombat1 = Array( ("Solar",1,"https://www.d20pfsrd.com/bestiary/monster-listings/outsiders/angel/solar/"),
                                    ("Worgs Rider",9,"https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-1/orc-worg-rider/"),
                                    ("Le Warlord",1,"https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-12/brutal-warlord-half-orc-fighter-13/"),
                                    ("Barbares Orc",4,"https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-10/double-axe-fury-half-orc-barbarian-11/"))

  var nbElement = 0
  for(i <- configurationCombat1.indices){
    nbElement = nbElement + configurationCombat1(i)._2
  }
  var cpt = 0
  val i,j = 0
  for(i <- configurationCombat1.indices){
    for(j <- 0 until configurationCombat1(i)._2){
      val monster_name = configurationCombat1(i)._1 + " " + cpt
      val new_monster = create_monster(configurationCombat1(i)._3, monster_name)
      println(new_monster.name + " hp : "+ new_monster.hp_current +" defense:  "+ new_monster.defense +
      " damage reduce " + new_monster.damage_reduce + " attacks " + new_monster.attacks + " speed:  "
      + new_monster.speed + " team " + new_monster.team + "  Buffs "+ new_monster.Buffs)
      tabMonster+=new_monster
      val node =new node(cpt, new_monster, nbElement)
      nodes +=((cpt, node ))
      cpt= cpt+1
    }
  }

  for(i <- nodes.indices){
    if(nodes(i)._2.monster.team == "G"){//On crée les edges a partir des monstres gentils
      for(j <- nodes.indices){
        if(nodes(i) != nodes(j)){
          nodes(i)._2.adjlist(j) = 1
          nodes(j)._2.adjlist(i) = 1
        }
      }
    }
  }

  // create vertices RDD with ID and Name
  var vRDD: RDD[(VertexId, node)] = sc.parallelize(nodes)

  //loop for communication and fight
  @scala.annotation.tailrec
  def loop(cpt: Int): Unit = {
    vRDD = vRDD.map(n => {
      var tmp = n
      var nearestEnnemy = None: Option[Monster]
      for (i <- tmp._2.adjlist.indices) {
        var adjacent = tmp._2.adjlist(i)
        if (adjacent == 1 && tabMonster(i).hp_current != 0) {
          if (nearestEnnemy.isEmpty) {
            nearestEnnemy = Some(tabMonster(i))
          } else {
            var otherEnemy = tabMonster(i)
            var distanceNearestEnemy = distance(tmp._2.monster, nearestEnnemy.get)
            var distanceOtherEnemy = distance(tmp._2.monster, otherEnemy)
            if (distanceOtherEnemy < distanceNearestEnemy) {
              nearestEnnemy = Some(otherEnemy)
            }
          }
        }
      }
      if (nearestEnnemy.isDefined) {
        tmp._2.nearestEnnemy = nearestEnnemy
      }
      tmp
    })
   var arrayCombat: Array[(VertexId, node)] = vRDD.collect()

    println(Console.WHITE + "########")
    println("Tour "+ cpt)
    println("########")

    println("***** Actions réalisées *****")
    for(i <- arrayCombat.indices){
      if(arrayCombat(i)._2.nearestEnnemy.isDefined){
        val enemy = arrayCombat(i)._2.nearestEnnemy.get

        val actionUse: (PartitionID, PartitionID) = combat.bestMove(arrayCombat(i)._2.monster, arrayCombat(i)._2.nearestEnnemy.get)
        if(actionUse._2 != -1){
          var enemyInArray: (VertexId, node) = arrayCombat(i)
          var IndexEnemyInTabMonster = -1
          for(j <- arrayCombat.indices){
            if(arrayCombat(j)._2.monster.name == arrayCombat(i)._2.nearestEnnemy.get.name){
              enemyInArray = arrayCombat(j)
            }
          }
          for(h <- tabMonster.indices){
            if(tabMonster(h).name == arrayCombat(i)._2.nearestEnnemy.get.name){
              IndexEnemyInTabMonster = h
            }
          }

          if(IndexEnemyInTabMonster != -1) {
            enemy.hp_current = actionUse._1
            val indiceEnemy = arrayCombat.indexOf(enemyInArray)
            arrayCombat(indiceEnemy)._2.monster.hp_current = enemy.hp_current
            tabMonster(IndexEnemyInTabMonster).hp_current = enemy.hp_current
            if (enemy.hp_current == 0) {
              arrayCombat(i)._2.nearestEnnemy = None
              arrayCombat(i)._2.adjlist(IndexEnemyInTabMonster) = 0
              tabMonsterDead += enemy
            }
          }
        }else{
          arrayCombat(i)._2.monster.deplacer(enemy.posx,enemy.posy)
        }
        arrayCombat(i)._2.nearestEnnemy = None
      }
    }
    arrayCombat = arrayCombat.filter(_._2.monster.hp_current != 0)

    println("\n***** Status *****")

    println("\nEn vie: ")
    for(i <- arrayCombat.indices){
      println(Console.BLUE + arrayCombat(i)._2.monster.name +" : " + Console.GREEN + arrayCombat(i)._2.monster.hp_current + "/" + arrayCombat(i)._2.monster.hp_max +
        " HP" + Console.WHITE + " position: (" + arrayCombat(i)._2.monster.posx + ", " +arrayCombat(i)._2.monster.posy + ", " +  arrayCombat(i)._2.monster.posz + ")"
      )
    }

    println("\nMort: ")
    for(i <- tabMonsterDead.indices){
      println(Console.BLUE + tabMonsterDead(i).name + Console.WHITE)
    }

    vRDD = sc.parallelize(arrayCombat)
    if (arrayCombat.length == 1 || cpt == 40) return

    loop(cpt+1)
  }

  loop(1)

  println("Fin du combat")

  //calcul distance entre 2 monstres
  def distance(monster1 : Monster, monster2 : Monster):Int = {
    val distance = Math.sqrt(
      (monster1.posy - monster2.posy)  * (monster1.posy - monster2.posy)
        + (monster1.posx - monster2.posx)  * (monster1.posx - monster2.posx)
    ).toInt
    distance
  }
}
