package com.herrild.espiresso.input

import com.herrild.espiresso.enums.SwitchType
import com.herrild.espiresso.power.Boiler
import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin

/**
 * @author jestenh@gmail.com
 * Created on 10/3/17
 */
class TemperatureSwitch(gpio: GpioController, pin: Pin, name: String, var boiler: Boiler, val type: SwitchType) : MomentarySwitch(gpio, pin, name) {
    override fun onPress() {
        if(type == SwitchType.UP) {
            boiler.increaseTemp()
        }else if(type == SwitchType.DOWN) {
            boiler.decreaseTemp()
        }

        super.onPress()
    }
}