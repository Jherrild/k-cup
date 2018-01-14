package com.herrild.espiresso.display

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import com.pi4j.wiringpi.I2C

/**
 * @author jestenh@gmail.com
 * Created on 9/24/17
 */
class Display(address: Int = 0x3c,
              i2c: I2CBus = I2CFactory.getInstance(I2C.CHANNEL_1),
              gpio: GpioController = GpioFactory.getInstance(),
              constants: SSD1306_Constants = SSD1306_Constants()) {

    var display = SSD1306(constants.LCD_WIDTH_128, constants.LCD_HEIGHT_64, gpio, i2c, address)
    var shotTimer = false

    fun init() {
        display.begin()
    }

    fun clear() {
        display.clearImage()
        display.clear()
    }

    fun updateText() {
        display.displayImage()
    }

    fun update() {
        display.display()
    }

    fun write(content: String, x: Int = 0, y: Int = 0) {
        display.addString(content, x=x, y=y)
    }

    fun write(content: String, x: Int = 0, y: Int = 0, size: Int, v_offset: Int = size) {
        display.addString(content, x=x, y=y, size=size, v_offset = v_offset)
    }

    fun vLine(index: Int) {
        display.verticalLine(index)
    }

    fun hLine(index: Int) {
        display.horizontalLine(index)
    }

    fun vBlock(start: Int, end: Int) {
        for(i in start..end) {
            vLine(i)
        }
    }

    fun hBlock(start: Int, end: Int) {
        for(i in start..end) {
            hLine(i)
        }
    }

    fun hSegment(index: Int, x1: Int, x2: Int) {
        display.horizontalSegment(index, x1, x2)
    }

    fun vSegment(index: Int, y1: Int, y2: Int) {
        display.verticalSegment(index, y1, y2)
    }

    fun pixel(x: Int, y: Int) {
        display.setPixel(x, y, true)
    }
}