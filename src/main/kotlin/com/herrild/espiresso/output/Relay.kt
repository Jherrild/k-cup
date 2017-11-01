package com.herrild.espiresso.output

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin

/**
 * @author jherrild@expedia.com
 * Created on 10/3/17
 */

/**
 * Controller class for a solid state relay - this utilizes pulse width modulation for power control
 */
open class Relay(val name: String, var gpio: GpioController, val pin: Pin) {

}