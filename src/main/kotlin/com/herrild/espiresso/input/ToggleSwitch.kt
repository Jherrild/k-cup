package com.herrild.espiresso.input

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import org.slf4j.LoggerFactory

/**
 * @author jherrild@expedia.com
 * Created on 9/26/17
 */
class ToggleSwitch(gpio: GpioController, pin: Pin, name: String) {
    var logger = LoggerFactory.getLogger(name)
    var last_modified = System.currentTimeMillis()
    private var gpioPin = gpio.provisionDigitalInputPin(pin, name, PinPullResistance.PULL_UP)
    var state = gpioPin.isState(PinState.HIGH)

    fun init() {
        resetModified()
        gpioPin.addListener(GpioPinListenerDigital {
            toggle()
        })
    }

    fun toggle() {
        logger.info(gpioPin.name + " was toggled on pin " + gpioPin.pin.name)
        resetModified()
        state = gpioPin.isState(PinState.HIGH)
    }

    fun resetModified() {
        last_modified = System.currentTimeMillis()
    }
}