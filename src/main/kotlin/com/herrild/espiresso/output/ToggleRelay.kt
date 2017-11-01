package com.herrild.espiresso.output

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.PinState

/**
 * @author jherrild@expedia.com
 * Created on 10/29/17
 */
class ToggleRelay(name: String, gpio: GpioController, pin: Pin) : Relay(name, gpio, pin) {
    val gpioPin = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW)

    fun on() {
        gpioPin.state = PinState.HIGH
    }

    fun off() {
        gpioPin.state = PinState.LOW
    }
}