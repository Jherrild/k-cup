package com.herrild.espiresso.input

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.PinPullResistance
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import org.slf4j.LoggerFactory

/**
 * @author jestenh@gmail.com
 * Created on 9/26/17
 */
open class ToggleSwitch(gpio: GpioController, pin: Pin, name: String) {
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

    open fun toggle() {
        state = gpioPin.isState(PinState.HIGH)
        resetModified()
        logger.info(gpioPin.name + " was toggled on pin " + gpioPin.pin.name)
    }

    fun resetModified() {
        last_modified = System.currentTimeMillis()
    }
}