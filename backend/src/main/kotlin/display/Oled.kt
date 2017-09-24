package display

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import com.pi4j.wiringpi.I2C

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */
class Oled(address: Int = 0x3c,
           i2c: I2CBus = I2CFactory.getInstance(I2C.CHANNEL_1),
           gpio: GpioController = GpioFactory.getInstance(),
           constants: SSD1306_Constants = display.SSD1306_Constants()) {

    var display = OLED.SSD1306_I2C_Display(constants.LCD_WIDTH_128, constants.LCD_HEIGHT_64, gpio, i2c, address)

    fun displayTest(content: String) {
        display.displayString(content)
        display.display()
    }
}