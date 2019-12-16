import com.gaocegege.scrala.core.spider.impl.DefaultSpider
import com.gaocegege.scrala.core.common.response.Response
import java.io.{BufferedReader, File, FileWriter, InputStreamReader}

import com.gaocegege.scrala.core.common.response.impl.HttpResponse
import com.gaocegege.scrala.core.common.response.impl.HttpResponse

class TestSpider extends DefaultSpider {

  var startUrl = List[String]()
  def parse(response: HttpResponse): Unit = {

    val links = (response getContentParser) select ("a")
    for (i <- 0 to links.size() - 1) {

      val link=links.get(i)attr("href")
      if (link.split("/").length>=7 &(link.contains("bestiary/monster-listings/aberrations/")| link.contains("bestiary/monster-listings/animals")| link.contains("bestiary/monster-listings/constructs")|
        link.contains("bestiary/monster-listings/dragons")| link.contains("bestiary/monster-listings/fey")| link.contains("bestiary/monster-listings/humanoids")|
        link.contains("bestiary/monster-listings/magical-Beasts")| link.contains("bestiary/monster-listings/monstrous-Humanoids")| link.contains("bestiary/monster-listings/oozes")|
        link.contains("bestiary/monster-listings/outsiders")| link.contains("bestiary/monster-listings/plants")| link.contains("bestiary/monster-listings/undead")|
        link.contains("bestiary/monster-listings/vermin"))){
        println(link)
        val fileName = "race_thread" + Thread.currentThread().getId() + ".txt"
        val fw = new FileWriter(fileName, true)
        try {

          fw.write( link + "\n")
        }
        finally fw.close()
      }
    }

  }

}

object crawler {
  def main(args: Array[String]) {
    val crawler = new TestSpider
    val race = Array("Aberrations","Animals","Constructs","Dragons","Fey","Humanoids","Magical-Beasts",
      "Monstrous-Humanoids","Oozes","Outsiders","Plants","Undead","Vermin")

    // close existing files
    for (i <- 0 to 30) {
      val fileName = "race_thread" + i + ".txt"

      val file_to_check = new File(fileName)
      val exist = file_to_check.exists()
      if (exist) {
        file_to_check.delete()
      }
    }

    for (i  <- race){
      val newUrl="https://www.d20pfsrd.com/bestiary/monster-listings/"+i
      val newUrlCollection = newUrl :: crawler.startUrl
      crawler.startUrl = newUrlCollection
    }
    crawler begin
  }
}
