package services

import java.text.DecimalFormat

/**
  * Template
  * Services for Play Framework templating
  *
  * Created by Grégoire JEANMART on 2015-12-27.
  */
object Template {

  /**
    * bytesToHumanReadable
    * @param size
    * @return human readable size (ex. 10000 bytes > 10 kB)
    */
  def bytesToHumanReadable(size: Int) : String = {
    if(size <= 0) return "0"

    val units:Array[String] = Array("B", "kB", "MB", "GB", "TB")
    val digitGroups:Int = (Math.log10(size)/Math.log10(1024)).toInt

    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units(digitGroups)
  }

}
