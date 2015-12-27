package services

import java.text.DecimalFormat

/**
  * Created by Grégoire JEANMART on 2015-12-27.
  */
object Template {

  def bytesToHumanReadable(size: Int) : String = {

    if(size <= 0) return "0";
    val units:Array[String] = Array("B", "kB", "MB", "GB", "TB")

    val digitGroups:Int = (Math.log10(size)/Math.log10(1024)).toInt;
    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units(digitGroups);


  }

}
