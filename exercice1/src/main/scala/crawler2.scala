import com.gaocegege.scrala.core.spider.impl.DefaultSpider
import com.gaocegege.scrala.core.common.response.Response
import java.io.{BufferedReader, File, FileWriter, InputStreamReader}

import com.gaocegege.scrala.core.common.response.impl.HttpResponse
import com.gaocegege.scrala.core.common.response.impl.HttpResponse
import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source



class TestSpider2 extends DefaultSpider {

  var startUrl = List[String]()


  def parse(response: HttpResponse): Unit = {
    println("je passe par le parse")
    var races= (response getContentParser).select("head").toString()
    var linksseclection=(response getContentParser).tagName("statblock").toString()
    println("les datas sont")
    try{
    linksseclection=linksseclection.split("<p class=\"divider\">OFFENSE</p> ")(1)
    linksseclection=linksseclection.split("<p class=\"divider\">STATISTICS</p> ")(0)
    }
    catch{
      case x:java.lang.ArrayIndexOutOfBoundsException=>{
        print("la créature n'a aps de sort")
      }
        linksseclection=""
    }
    println(linksseclection)
    val links =linksseclection.split("<a class=\"spell\"")
    links.foreach(e=>println(e))
    var listesorts=""
    var sorts=links.flatMap(e=> {
      var tab = ""
      if (e.contains(("all-spells"))) {
        tab = e.split("\"")(1).toString()
      }
      tab
    })
    sorts.foreach(e=>{
      listesorts+=e
    })
    listesorts=listesorts.replaceAll("https://",";https://")
    println("les sorts sont "+listesorts)
    println("fin de la data")
    //println("le header est "+races)
    var tampon_race=""
    var race=races.split("\n")
    race.foreach(line => {
      if (line.contains("base href")) {
        print(line)
        tampon_race = line
      }
    })
    // un sort est défini comme le nom de la créature  + tous les spells que cette créature peut faire
    var spell=tampon_race+listesorts
    if(!spell.equals(tampon_race) ) {
      val fileName = "spells_thread" + Thread.currentThread().getId() + ".txt"
      println("la ligne final est"+spell)
      val fw = new FileWriter(fileName, true)
      try {

        fw.write(spell + "\n")
      }
      finally fw.close()
    }

    /*for (i <- 0 to links.size() - 1) {

      val link=links.get(i)attr("href")
      if ((link.split("/").length>=6 & link.contains("all-spells"))){
        val tampon =spell+";"+ link
        spell = tampon

      }
    }
    println("les sorts sont "+ spell)

*/
  }

}
object crawler2 {


  val conf = new SparkConf()
    .setAppName("Petit exemple")
    .setMaster("local[*]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  var dataset=""

  def main(args: Array[String]) {
    val crawler2 = new TestSpider2

   /*// close existing files
    for (i <- 0 to 100) {
      val fileName = "spells_thread" + i + ".txt"

      val file_to_check = new File(fileName)
      val exist = file_to_check.exists()
      if (exist) {
        file_to_check.delete()
      }
    }

    */
    // normalement une boucle for devrait s'occuper de tout les fichier 1 à 1 mais comme maintenant nous avons des problèmes si nous chargeons
    // trop de page, nous nous en sommes occupé petit à petit
      val fileName = "race_thread15.txt"
      val file_to_check = new File(fileName)

      if (file_to_check.exists()) {
        val text = Source.fromFile(fileName).getLines
        var i=0
        text.foreach(Line => {
          if(Line.toString.contains("https") & i>2399 & i<2800){
            //println("je suis en train de m'occuper de la créature :"+ Line)
            val newUrlCollection=Line :: crawler2.startUrl
            crawler2.startUrl=newUrlCollection

          }
          i+=1
        })
      }


    crawler2 begin()
  }
}



