
import java.io.{BufferedReader, File, FileWriter, InputStreamReader}

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object creation_premiere_bdd extends Serializable{
  System.setProperty("hadoop.home.dir", "C:\\winutils")
  val conf = new SparkConf()
    .setAppName("exercice1")
    .setMaster("local[*]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  def main(args: Array[String]): Unit = {

    var creature=Array[(String,Array[String])]()
    for (i <- 0 to 100) {
      val fileName = "spells_thread" + i + ".txt"
      val file_to_check = new File(fileName)
      if (file_to_check.exists()) {
        val text = Source.fromFile(fileName).getLines
        println("je m'occupe du fichier "+i)
        text.foreach(Line => {
          var line =Line.split(";")
          var creaturespell= Array[String]()

          var creaturename=""
          line.foreach(l=>{
            l.replace(" ","")
            if (l.contains("https://www.d20pfsrd.com/bestiary/monster-listings/")){
              var lien=l.split("/")
              creaturename=lien(lien.length-2).toString()
              //println ("la créature est "+creaturename+" elle est dans le fichier "+i)

            }
            else{
              var lien=l.split("/")
              //print ("ces sorts sont"+lien(lien.length-1))
              var buffer2=Array[String]()
              if(!creaturespell.contains(lien(lien.length-1).toString())) {
                buffer2 = Array(lien(lien.length - 1).toString()) ++ creaturespell
                creaturespell = buffer2
              }
            }
          })
          var buffer2=creature ++ Array((creaturename,creaturespell))
          creature=buffer2
        })
        }
      }
/*
    println("le tableau de tuple")
    creature.foreach(e=>
    println(e)

    )
*/

    val monRdd=sc.makeRDD(creature)
      /*monRdd.collect.foreach(e=>{
      println("")
      print("la créature est ")
      print(e._1+ "  ")
      print("ces sort sont ")

      e._2.foreach(f=> print (f+";"))
    })

       */

    var Rddinverse=monRdd.flatMap(e=>{
      var resultats = Array[(String,String)]()
      var elem=Tuple2[String,String]("","")

      e._2.foreach(f=> {
        elem=Tuple2(f,e._1)
        var buffer=resultats++Array(elem)
        resultats=buffer
      })
      resultats
    })
    Rddinverse.collect().foreach(e=>{
      //println(e)
    })
    var Rddinverse2=Rddinverse.reduceByKey((a,b)=>a+"/"+b).collect()
    println("chaque sort + liste des créatures")
    Rddinverse2.foreach(e=>println(e))



  }
}
