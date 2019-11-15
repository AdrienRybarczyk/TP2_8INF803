import org.apache.spark._
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer
// import classes required for using GraphX
import org.apache.spark.graphx._


object creation extends App{
  val conf = new SparkConf().setAppName("Creation Graphe").setMaster("local[*]")
  val sc = SparkContext.getOrCreate(conf)
  sc.setLogLevel("ERROR")

  val armor_test_modifiers = ArrayBuffer(1,2,3)
  val new_attack = new Attack("FulguroPoing", armor_test_modifiers, 18, 3,2, 100, "ranged",3)
  val attacks = ArrayBuffer(new_attack)
  val buffs = ArrayBuffer("Handsome", "Justice warrior", "Alien butt kicker")

  var tabMonster = ArrayBuffer[Monster]()
  var nodes = ArrayBuffer[(VertexId, String)]()
  val edges = Array(Edge(1L,2L,1800),Edge(2L,3L,800),Edge(3L,1L,1400))

  var configurationCombat1 = Array(("Solar",1),("Worgs Rider",9),("Le Warlord",1),("Barbares Orc",4))
  var cpt = 0
  val i,j = 0
  for(i <- configurationCombat1.indices){
    for(j <- 0 until configurationCombat1(i)._2){
      val new_monster = new Monster(configurationCombat1(i)._1 + " " + cpt, 80, 70, 30, 20, attacks, 150, "Gud guys", buffs, 0, 0, 0)
      tabMonster+=new_monster
      nodes+=((cpt,new_monster.name))
      cpt= cpt+1
    }
  }
  /*for(i <- nodes.indices){
    println(nodes(i).toString)
  }*/

  //val value = Math.sqrt(Math.abs(y2 - y1) + Math.abs(x2 - x1))

  // create vertices RDD with ID and Name
  val vRDD: RDD[(VertexId, String)] = sc.parallelize(nodes)
  val eRDD: RDD[Edge[PartitionID]] = sc.parallelize(edges)

  // define the graph
  val graph = Graph(vRDD,eRDD)
  // graph vertices
  graph.vertices.collect.foreach(println)
  // graph edges
  graph.edges.collect.foreach(println)
}
