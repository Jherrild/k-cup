package com.herrild.espiresso.output

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.wiringpi.Gpio
import com.pi4j.wiringpi.SoftPwm
import org.slf4j.LoggerFactory

/**
 * @author jestenh@gmail.com
 * Created on 10/29/17
 */
//range was 4000, and clock was 2400
class PwmRelay(name: String, gpio: GpioController, pin: Pin, val pinNum: Int, val range: Int = 100) : Relay(name, gpio, pin) {
    //var pwm = gpio.provisionPwmOutputPin(pin, name, 0)
    //TODO: Read this number from constructor
    //var alwaysOn = gpio.provisionDigitalOutputPin(pin)
    val logger = LoggerFactory.getLogger("PWM_Relay")

    fun init() {
        SoftPwm.softPwmCreate(pinNum, 0, range)
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS)
        //pwm.setPwmRange(range)
        //Gpio.pwmSetRange(range)
        //Gpio.pwmSetClock(clock)
        //logger.debug("PWM Pin Mode: " + pwm.mode.toString())
    }

    fun update(dutyCycle: Int) {
        logger.info("Duty cycle updated: " + dutyCycle.toString())
        //alwaysOn.high()

        if(dutyCycle <= range) {
            SoftPwm.softPwmWrite(pinNum, dutyCycle)
            //pwm.setPwm(dutyCycle)
        }else {
            SoftPwm.softPwmWrite(pinNum, range)
            //pwm.setPwm(range)
        }
    }
}