package main.kotlin.power

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */
class Boiler(brew_temp: Int = 100, steam_temp: Int = 150) {
    var brew_temp = brew_temp
    var steam_temp = steam_temp

    fun changeBrewTemp(degrees: Int) {
        brew_temp += degrees
    }
}