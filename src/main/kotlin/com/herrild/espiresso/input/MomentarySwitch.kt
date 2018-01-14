package com.herrild.espiresso.input

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import org.slf4j.LoggerFactory

/**
 * @author jestenh@gmail.com
 * Created on 9/26/17
 */
open class MomentarySwitch(gpio: GpioController, pin: Pin, name: String) {
    var logger = LoggerFactory.getLogger(name)
    var last_modified = System.currentTimeMillis()
    //TODO: Check if these should be pullup or pulldown
    var gpioPin = gpio.provisionDigitalInputPin(pin, name, PinPullResistance.PULL_UP)

    fun init() {
        gpioPin.addListener(GpioPinListenerDigital {
            onPress()
        })
    }

    open fun onPress() : Long {
        logger.info(gpioPin.name + " was actuated on pin " + gpioPin.pin.name)
        return resetModified()
    }

    fun resetModified() : Long {
        last_modified = System.currentTimeMillis()
        return last_modified
    }
}