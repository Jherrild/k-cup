package main.kotlin.power

import input.Thermocouple
import main.kotlin.temp.PID

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */

/**
 * Boiler class contains a PID reference, and starts a thread for the PID control algorithm when initialized
 *   Temperature variables are stored in C, and
 */
class Boiler(brew_temp: Int = 100, steam_temp: Int = 150, temp_sensor: Thermocouple, power_state: Boolean = false) {
    var pid = PID(brew_temp, 0.toFloat())
    var power_state = power_state
    var brew_temp = brew_temp
    var steam_temp = steam_temp
    var temp_sensor = temp_sensor

    fun init() {
        power_state = true
    }

    fun boilerPower(state: Boolean) {
        power_state = state
    }

    fun changeBrewTemp(degrees: Int) {
        brew_temp = degrees
    }

    fun updateTemperature() : Float {
        pid.currentTemp = temp_sensor.readTemp()
        return pid.currentTemp
    }

    /**
     * Executes a single cycle of the default PID algorithm on the boiler's PID object
     */
    fun runPid() {
        pid.execute()
    }
}