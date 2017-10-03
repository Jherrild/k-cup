package com.herrild.espiresso.input

import com.herrild.espiresso.output.Relay
import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin

/**
 * @author jherrild@expedia.com
 * Created on 10/3/17
 */
class RelaySwitch(gpio: GpioController, pin: Pin, name: String, var relay : Relay) : ToggleSwitch(gpio, pin, name) {
    override fun toggle() {
        super.toggle()

        if(state) {
            relay.on()
        }else {
            relay.off()
        }
    }
}