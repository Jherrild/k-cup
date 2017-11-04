package com.herrild.espiresso.output

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.wiringpi.Gpio

/**
 * @author jestenh@gmail.com
 * Created on 10/29/17
 */
class PwmRelay(name: String, gpio: GpioController, pin: Pin, val range: Int = 4000, val clock: Int = 2400) : Relay(name, gpio, pin) {
    var gpioPin = gpio.provisionPwmOutputPin(pin, name, 0)

    init {
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS)
        Gpio.pwmSetRange(range)
        Gpio.pwmSetClock(clock)
    }

    fun update(dutyCycle: Int) {
        if(dutyCycle <= range) {
            gpioPin.setPwmRange(dutyCycle)
        }
    }
}