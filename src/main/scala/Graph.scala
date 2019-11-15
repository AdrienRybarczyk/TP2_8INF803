import org.apache.spark._
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer
// import classes required for using GraphX
import org.apache.spark.graphx._
import org.jsoup.Jsoup


object creation extends App{

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

    println(dr)
    //println(hp)
    //println(defense)

    val new_monster = new Monster(monster_name, hp, hp, defense, dr, attacks, 150, "Gud guys", buffs, 0, 0, 0)

    return new_monster
  }

  val conf = new SparkConf()
    .setAppName("Creation Graphe")
    .setMaster("local[*]")
  val sc = SparkContext.getOrCreate(conf)
  sc.setLogLevel("ERROR")

  var tabVertexID: Array[VertexId] = Array(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L )

  val armor_test_modifiers = ArrayBuffer(1,2,3)

  val new_attack = new Attack("FulguroPoing", armor_test_modifiers, 18, 3, 100, "ranged")
  val attacks = ArrayBuffer(new_attack)
  val buffs = ArrayBuffer("Handsome", "Justice warrior", "Alien butt kicker")

  var tabMonster = ArrayBuffer[Monster]()
  //val vertices = ArrayBuffer[(VertexId, String)]

  var configurationCombat1 = Array( ("Solar",1,"https://www.d20pfsrd.com/bestiary/monster-listings/outsiders/angel/solar/"),
                                    ("Worgs Rider",9,"https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-1/orc-worg-rider/"),
                                    ("Le Warlord",1,"https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-12/brutal-warlord-half-orc-fighter-13/"),
                                    ("Barbares Orc",4,"https://www.d20pfsrd.com/bestiary/npc-s/npcs-cr-10/double-axe-fury-half-orc-barbarian-11/"))
  var cpt = 1
  val i,j = 0
  for(i <- configurationCombat1.indices){
    for(j <- 0 until configurationCombat1(i)._2){
      val monster_name = configurationCombat1(i)._1 + " " + cpt
      val new_monster = create_monster(configurationCombat1(i)._3, monster_name)

      tabMonster+=new_monster
      //vertices+=(tabVertexID(i),new_monster.name)
      cpt= cpt+1
    }
  }
  for(i <- tabMonster.indices){
    println(tabMonster(i).toString)
  }

  //val value = Math.sqrt(Math.abs(y2 - y1) + Math.abs(x2 - x1))

  // create vertices RDD with ID and Name
  val vertices: Array[(VertexId, String)] =Array((1L, "SFO"),(2L, "ORD"),(3L,"DFW"))
  val vRDD: RDD[(VertexId, String)] = sc.parallelize(vertices)

  val edges = Array(Edge(1L,2L,1800),Edge(2L,3L,800),Edge(3L,1L,1400))
  val eRDD= sc.parallelize(edges)

  // define the graph
  val graph = Graph(vRDD,eRDD)
  // graph vertices
  graph.vertices.collect.foreach(println)
  // (2,ORD)
  // (1,SFO)
  // (3,DFW)
  // graph edges
  graph.edges.collect.foreach(println)

  // Edge(1,2,1800)
  // Edge(2,3,800)
  // Edge(3,1,1400)
}
