package com.herrild.espiresso.input

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.Pin
import com.pi4j.wiringpi.Spi
import org.slf4j.LoggerFactory

/**
 * @author jestenh@gmail.com
 * Created on 9/26/17
 */
class Thermocouple(gpio: GpioController, DO: Pin, CS: Pin, CLK: Pin) {
    var gpio = gpio
    val DO = DO
    val CS = CS
    val CLK = CLK
    val logger = LoggerFactory.getLogger("Thermocouple")
    var sensor = com.herrild.espiresso.input.MAX31855(-1) //Placeholder until Thermocouple is initialized

    fun init() {
        val channel = Spi.CHANNEL_0
        val setupStatus = Spi.wiringPiSPISetup(channel, 500000)

        if (setupStatus == -1) {
            logger.error("Unable to setup SPI for Thermocouple")
            throw RuntimeException("SPI Setup failed")
        }
        sensor = com.herrild.espiresso.input.MAX31855(channel)
    }

    fun readTemp() : Float {
        var raw = IntArray(2)
        val faults = sensor.readRaw(raw)
        if(faults != 0) {
            logger.error("MAX31855 returned faults on readRaw() - Fault: " + faults.toString())
        }
        val chipTemp = sensor.getInternalTemperature(raw[0]).toInt()
        val probeTemp = sensor.getThermocoupleTemperature(raw[1])

        logger.info("Chip temperature: " + chipTemp.toString())
        if(chipTemp > 110) {
            throw RuntimeException("Shutdown to avoid MAX31855 exceeding maximum operational temperature: Measured at " + chipTemp.toString())
        }

        return probeTemp
    }
}