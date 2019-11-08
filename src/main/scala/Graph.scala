import org.apache.spark._
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer
// import classes required for using GraphX
import org.apache.spark.graphx._


object creation extends App{
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

  var configurationCombat1 = Array(("Solar",1),("Worgs Rider",9),("Le Warlord",1),("Barbares Orc",4))
  var cpt = 1
  val i,j = 0
  for(i <- configurationCombat1.indices){
    for(j <- 0 until configurationCombat1(i)._2){
      val new_monster = new Monster(configurationCombat1(i)._1 + " " + cpt, 80, 70, 30, 20, attacks, 150, "Gud guys", buffs, 0, 0, 0)
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
