package com.herrild.espiresso.power

import com.herrild.espiresso.input.Thermocouple
import com.herrild.espiresso.input.ToggleSwitch
import com.herrild.espiresso.temp.PID
import org.slf4j.LoggerFactory

/**
 * @author jestenh@gmail.com
 * Created on 9/24/17
 */

/**
 * Boiler class contains a PID reference, and starts a thread for the PID control algorithm when initialized
 *   Temperature variables are stored in C, and
 */
class Boiler(var brew_temp: Int = 60,
             var steam_temp: Int = 150,
             var temp_sensor: Thermocouple,
             var power_state: Boolean = false,
             var brew_switch: ToggleSwitch) {

    val logger = LoggerFactory.getLogger("Boiler")
    var pid = PID(brew_temp)
    var currentTemp = 0.toFloat()

    fun init() {
        power_state = true
    }

    /**
     * Implemented for Hardware IO purposes - modifies the steam or brew temp based on which mode the machine is in.
     *   This is to ensure that pressing the temperature up or temperature down buttons changes the temperature of the
     *   current machine mode.
     */
    fun increaseTemp() {
        if(brew_temp < 150) {
            if(brew_switch.state) {
                brew_temp ++
            }else {
                steam_temp ++
            }
        }
    }

    /**
     * Implemented for Hardware IO purposes - modifies the steam or brew temp based on which mode the machine is in.
     *   This is to ensure that pressing the temperature up or temperature down buttons changes the temperature of the
     *   current machine mode.
     */
    fun decreaseTemp() {
        if(brew_temp > 80) {
            if(brew_switch.state) {
                brew_temp --
            }else {
                steam_temp --
            }
        }
    }

    /**
     * Intended for REST api, not to be utilized by hardware
     */
    fun changeBrewTemp(degrees: Int) {
        brew_temp = degrees
    }

    /**
     * Intended for REST api, not to be utilized by hardware
     */
    fun changeSteamTemp(degrees: Int) {
        steam_temp = degrees
    }

    fun updateTemperature() : Float {
        currentTemp = temp_sensor.readTemp()
        return currentTemp
    }

    /**
     * Executes a single cycle of the default PID algorithm on the boiler's PID object
     */
    fun runPid() : Float {
        val output = pid.execute(currentTemp)
        logger.info("runPid() executed with result: " + output.toString())
        return output
    }
}