import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

case class node(var color: Int = 1, var tiebreakvalue: Int = 0, len: Int = 0)
{
  var adjlist = new Array[Int](len)

  def printadjlist: String = {
    var output = "adjlist : "
    adjlist.foreach(output += _)
    output
  }

  override def toString: String = {
    s"color : $color, tiebreakvalue : $tiebreakvalue"
  }
}

case class edge_data(src: Long, dst: Long)

object coloring extends Serializable {

  //Return infered
  def randomTieBreakers(graph: Array[(Int,node)]) = {
    var tiebreakers = ArrayBuffer[Int]()
    var count = graph.size
    //Generate normal IDs
    for (i <- 0 until count) tiebreakers += i
    //Generate tiebreakers
    Random.setSeed(System.nanoTime())
    tiebreakers = Random.shuffle(tiebreakers)
    tiebreakers
  }

  //Color using the FC2 algorithm
  def fc2(graph: Array[(Int,node)], sc: SparkContext) = {
    //Step 1 : We generate tiebreakers
    var tiebreakers = randomTieBreakers(graph)

    //Apply tiebreakers
    for (i <- 0 until tiebreakers.size)
      graph(i)._2.tiebreakvalue = tiebreakers(i)

    println("Printing after tiebreakers")
    graph foreach println
    println

    //Execute the algorithm
    var graphRDD = sc.makeRDD(graph)
    loop

    def loop(): Unit = {
      //On broadcast les couleurs, tiebreakers à tout le monde
      val essentiels = graphRDD.flatMap(node => {
        Some((node._1, node._2.tiebreakvalue))
      }).collect.sortBy(_._1).map(elem => elem._2)

      val bcast = sc.broadcast(essentiels)

      graphRDD = graphRDD.map(n =>
      {
        var toBeKnight = true

        for (i <- 0 until n._2.adjlist.length) {
          var adjacent = n._2.adjlist(i)
          if (adjacent == 1) {
            val tieBreakerVoisin = bcast.value(i)
            if (tieBreakerVoisin != -1) {
              if (tieBreakerVoisin < n._2.tiebreakvalue) //-1 = knight
                toBeKnight = false
            }
          }

        }
        if (toBeKnight == true) {
          n._2.tiebreakvalue = -1
        }
        else {
          n._2.color += 1
        }

        n
        //return the modified object
      })

      // graphRDD.cache()
      graphRDD.localCheckpoint()  //on sauve une copie de ce RDD en mémoire. Les prochains calculs qui l'utilisent partiront de celui-ci.
      println("Check pour fin")

      //DEBUG on check les couleurs
      graphRDD.collect.foreach( println(_))

      //On check pour la fin. Fin = tout le monde est un knight
      val result = graphRDD.filter(node => {
        if (node._2.tiebreakvalue == -1) false
        else true
      })

      //If every node is a knight we can return
      if (result.isEmpty()) return

      //Else we restart the loop
      loop
    }

    //Return the colors of the graph
    graphRDD.map(node => node._2.color).collect()

  }
}

object testAlgo extends App {

  val conf = new SparkConf()
    .setAppName("Petersen Graph version Examen")
    .setMaster("local[*]")
  val sc = SparkContext.getOrCreate(conf)
  sc.setLogLevel("ERROR")

  var edges = Array(
    edge_data(1L, 2L), edge_data(1L, 3L), edge_data(1L, 6L),
    edge_data(2L, 7L), edge_data(2L, 8L),
    edge_data(3L, 4L), edge_data(3L, 9L),
    edge_data(4L, 5L), edge_data(4L, 8L),
    edge_data(5L, 6L), edge_data(5L, 7L),
    edge_data(6L, 10L),
    edge_data(7L, 9L),
    edge_data(8L, 10L),
    edge_data(9L, 10L)
  )

  //Créer une collection avec des yield
  var graph = for (i <- 1 to 10) yield  Tuple2(i-1, new node(1, 0, 10))

  //Fill the adjmatrix
  edges.foreach(e => {
    val src = e.src.toInt
    val dst = e.dst.toInt
    graph(src - 1)._2.adjlist(dst - 1) = 1
    graph(dst - 1)._2.adjlist(src - 1) = 1
  })

  println
  graph foreach( e=> print(e._2.printadjlist+"\n") )
  graph foreach println
  println

  //Execute the algorithm
  val results = coloring.fc2(graph.toArray, sc)

  println("\nColors of the graph\n")
  results.foreach(color => print(color + " "))
}