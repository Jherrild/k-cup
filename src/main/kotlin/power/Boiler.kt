package main.kotlin.power

import main.kotlin.temp.PID

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */

/**
 * Boiler class contains a PID reference, and starts a thread for the PID control algorithm when initialized
 */
class Boiler(brew_temp: Int = 100, steam_temp: Int = 150, power_state: Boolean = false) {
    var pid = PID(brew_temp, 0)
    var power_state = power_state
    // brew_temp variable is C - this will be converted to F for output to user only.
    var brew_temp = brew_temp
    var steam_temp = steam_temp

    // brew_state is true if set to 'Brew', and false if set to 'Steam'
    var brew_state = true

    fun boilerPower(state: Boolean) {
        power_state = state
    }

    fun changeBrewTemp(degrees: Int) {
        brew_temp = degrees
    }

    fun init() {
        power_state = true
    }
}