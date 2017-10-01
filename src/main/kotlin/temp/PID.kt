package main.kotlin.temp

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */
class PID(setTemp: Int, currentTemp: Int, p: Int = 2, i: Int = 0, d: Int = 0) {
    var setTemp = setTemp
    var currentTemp = currentTemp

    var p = p
    var i = i
    var d = d

    fun init() {

    }
}