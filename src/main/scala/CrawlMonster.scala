import scala.collection.mutable.ArrayBuffer


//class CrawlMonster {

//}

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

    for (element <- tabURL) {
      println("Crawling to : ", element)
    }
  }
}

