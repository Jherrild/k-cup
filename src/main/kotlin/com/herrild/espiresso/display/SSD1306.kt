/**
 *
 */
package com.herrild.espiresso.display

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.GpioPinDigitalOutput
import com.pi4j.io.gpio.Pin
import com.pi4j.io.i2c.I2CBus
import com.pi4j.wiringpi.I2C
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.IOException
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * @author Tuan Vu
 */
class SSD1306
/**
 * Display object using I2C communication with a reset pin
 * <br></br>
 * As I haven't got an I2C main.com.herrild.espiresso.getDisplay and I don't understand I2C much, I just tried to copy
 * the Adafruit's library and I am using a hack to use WiringPi function similar to one in the original lib directly.
 *
 * @param width   Display width
 * @param height  Display height
 * @param gpio    GPIO object
 * @param i2c     I2C object
 * @param address Display address
 * @param rstPin  Reset pin
 * @see GpioFactory.getInstance
 * @see com.pi4j.io.i2c.I2CFactory.getInstance
 * @throws ReflectiveOperationException Thrown if I2C handle is not accessible
 * @throws IOException                  Thrown if the bus can't return device for specified address
 */
@Throws(IOException::class)
@JvmOverloads constructor(
        /**
         * @return Display width
         */
        val width: Int,
        /**
         * @return Display height
         */
        val height: Int, gpio: GpioController, i2c: I2CBus, address: Int, rstPin: Pin? = null) {

    protected var vccState: Int = 0
    /**
     * Returns internal AWT image
     * @return BufferedImage
     */
    private var image: BufferedImage
        set
    /**
     * Returns Graphics object which is associated to current AWT image,
     * if it wasn't set using setImage() with false createGraphics parameter
     * @return Graphics2D object
     */
    private var graphics: Graphics2D
        set
    private val pages: Int
    private var hasRst: Boolean = false
    private var rstPin: GpioPinDigitalOutput? = null
    private val fd: Int
    private var buffer: ByteArray? = null
    private var constants = SSD1306_Constants()
    var log = LoggerFactory.getLogger("Display")

    init {
        this.pages = height / 8
        this.buffer = ByteArray(width * this.pages)

        if (rstPin != null) {
            this.rstPin = gpio.provisionDigitalOutputPin(rstPin)
            this.hasRst = true
        } else {
            this.hasRst = false
        }

        this.image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
        this.graphics = this.image.createGraphics()

        this.fd = I2C.wiringPiI2CSetup(address)
        if (this.fd == -1) {
            throw IOException("Unable to open device at address: " + address)
        }
    }


    @Throws(IOException::class)
    private fun initDisplay() {
        if (this.width == constants.LCD_WIDTH_128 && this.height == constants.LCD_HEIGHT_64) {
            this.init(0x3F, 0x12, 0x80)
        } else if (this.width == constants.LCD_WIDTH_128 && this.height == constants.LCD_HEIGHT_32) {
            this.init(0x1F, 0x02, 0x80)
        } else if (this.width == constants.LCD_WIDTH_96 && this.height == constants.LCD_HEIGHT_16) {
            this.init(0x0F, 0x02, 0x60)
        } else {
            throw IOException("Invalid width: " + this.width + " or height: " + this.height)
        }

    }

    private fun init(multiplex: Int, compins: Int, ratio: Int) {
        this.command(constants.SSD1306_DISPLAYOFF.toInt())
        this.command(constants.SSD1306_SETDISPLAYCLOCKDIV.toInt())
        this.command(ratio.toShort().toInt())
        this.command(constants.SSD1306_SETMULTIPLEX.toInt())
        this.command(multiplex.toShort().toInt())
        this.command(constants.SSD1306_SETDISPLAYOFFSET.toInt())
        this.command(0x0.toShort().toInt())
        this.command(constants.SSD1306_SETSTARTLINE.toInt())
        this.command(constants.SSD1306_CHARGEPUMP.toInt())

        if (this.vccState == constants.SSD1306_EXTERNALVCC.toInt())
            this.command(0x10.toShort().toInt())
        else
            this.command(0x14.toShort().toInt())

        this.command(constants.SSD1306_MEMORYMODE.toInt())
        this.command(0x00.toShort().toInt())
        this.command((constants.SSD1306_SEGREMAP or 0x1).toShort().toInt())
        this.command(constants.SSD1306_COMSCANDEC.toInt())
        this.command(constants.SSD1306_SETCOMPINS.toInt())
        this.command(compins.toShort().toInt())
        this.command(constants.SSD1306_SETCONTRAST.toInt())

        if (this.vccState == constants.SSD1306_EXTERNALVCC.toInt())
            this.command(0x9F.toShort().toInt())
        else
            this.command(0xCF.toShort().toInt())

        this.command(constants.SSD1306_SETPRECHARGE.toInt())

        if (this.vccState == constants.SSD1306_EXTERNALVCC.toInt())
            this.command(0x22.toShort().toInt())
        else
            this.command(0xF1.toShort().toInt())

        this.command(constants.SSD1306_SETVCOMDETECT.toInt())
        this.command(0x40.toShort().toInt())
        this.command(constants.SSD1306_DISPLAYALLON_RESUME.toInt())
        this.command(constants.SSD1306_NORMALDISPLAY.toInt())
    }

    /**
     * Turns on command mode and sends command
     * @param command Command to send. Should be in short range.
     */
    private fun command(command: Int) {
        this.i2cWrite(0, command)
    }

    /**
     * Turns on data mode and sends data
     * @param data Data to send. Should be in short range.
     */
    private fun data(data: Int) {
        this.i2cWrite(0x40, data)
    }

    /**
     * Turns on data mode and sends data array
     * @param data Data array
     */
    private fun data(data: ByteArray?) {
        var i = 0
        while (i < data!!.size) {
            for (j in 0..15) {
                this.i2cWrite(0x40, data[i].toInt())
                i++
            }
        }

    }

    /**
     * Begin with SWITCHCAPVCC VCC mode
     * @see constants.SSD1306_SWITCHCAPVCC
     */
    @Throws(IOException::class)
    fun begin() {
        this.begin(constants.SSD1306_SWITCHCAPVCC.toInt())
    }

    /**
     * Begin with specified VCC mode (can be SWITCHCAPVCC or EXTERNALVCC)
     * @param vccState VCC mode
     * @see constants.SSD1306_SWITCHCAPVCC
     *
     * @see constants.SSD1306_EXTERNALVCC
     */
    @Throws(IOException::class)
    fun begin(vccState: Int) {
        this.vccState = vccState
        this.reset()
        this.initDisplay()
        this.command(constants.SSD1306_DISPLAYON.toInt())
        this.clear()
        this.display()
    }

    /**
     * Pulls reset pin high and low and resets the main.com.herrild.espiresso.getDisplay
     */
    fun reset() {
        if (this.hasRst) {
            try {
                this.rstPin!!.setState(true)
                Thread.sleep(1)
                this.rstPin!!.setState(false)
                Thread.sleep(10)
                this.rstPin!!.setState(true)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Sends the buffer to the main.com.herrild.espiresso.getDisplay
     */
    @Synchronized
    fun display() {
        this.command(constants.SSD1306_COLUMNADDR.toInt())
        this.command(0)
        this.command(this.width - 1)
        this.command(constants.SSD1306_PAGEADDR.toInt())
        this.command(0)
        this.command(this.pages - 1)

        this.data(this.buffer)
    }

    /**
     * Clears the buffer by creating a new byte array
     */
    fun clear() {
        this.buffer = ByteArray(this.width * this.pages)
    }

    /**
     * Sets the main.com.herrild.espiresso.getDisplay contract. Apparently not really working.
     * @param contrast Contrast
     */
    fun setContrast(contrast: Byte) {
        this.command(constants.SSD1306_SETCONTRAST.toInt())
        this.command(contrast.toInt())
    }

    /**
     * Sets if the backlight should be dimmed
     * @param dim Dim state
     */
    fun dim(dim: Boolean) {
        if (dim) {
            this.setContrast(0.toByte())
        } else {
            if (this.vccState == constants.SSD1306_EXTERNALVCC.toInt()) {
                this.setContrast(0x9F.toByte())
            } else {
                this.setContrast(0xCF.toByte())
            }
        }
    }

    /**
     * Sets if the main.com.herrild.espiresso.getDisplay should be inverted
     * @param invert Invert state
     */
    fun invertDisplay(invert: Boolean) {
        if (invert) {
            this.command(constants.SSD1306_INVERTDISPLAY.toInt())
        } else {
            this.command(constants.SSD1306_NORMALDISPLAY.toInt())
        }
    }

    /**
     * Sets one pixel in the current buffer
     * @param x X position
     * @param y Y position
     * @param white White or black pixel
     * @return True if the pixel was successfully set
     */
    fun setPixel(x: Int, y: Int, white: Boolean): Boolean {
        if (x < 0 || x > this.width || y < 0 || y > this.height) {
            return false
        }

        if (white) {
            this.buffer!![x + y / 8 * this.width] = this.buffer!![x + y / 8 * this.width] or (1 shl (y and 7)).toByte()
        } else {
            this.buffer!![x + y / 8 * this.width] = this.buffer!![x + y / 8 * this.width] and (1 shl (y and 7)).inv().toByte()
        }

        return true
    }

    /**
     * Copies AWT image contents to buffer. Calls main.com.herrild.espiresso.getDisplay()
     * @see SSD1306_I2C_Display.display
     */
    @Synchronized
    fun displayImage() {
        val r = this.image.raster

        for (y in 0..this.height - 1) {
            for (x in 0..this.width - 1) {
                this.setPixel(x, y, r.getSample(x, y, 0) > 0)
            }
        }
    }

    /**
     * Sets internal buffer
     * @param buffer New used buffer
     */
    private fun setBuffer(buffer: ByteArray) {
        this.buffer = buffer
    }

    /**
     * Sets one byte in the buffer
     * @param position Position to set
     * @param value Value to set
     */
    private fun setBufferByte(position: Int, value: Byte) {
        this.buffer!![position] = value
    }

    /**
     * Sets internal AWT image to specified one.
     * @param img BufferedImage to set
     * @param createGraphics If true, createGraphics() will be called on the image and the result will be saved
     * to the internal Graphics field accessible by getGraphics() method
     */
    fun setImage(img: BufferedImage, createGraphics: Boolean) {
        this.image = img

        if (createGraphics) {
            this.graphics = img.createGraphics()
        }
    }

    fun clearImage() {
        this.graphics.background = Color(0, 0, 0, 0)
        this.graphics.clearRect(0, 0, image.width, image.height)
    }

    /**
     * Displays a string at the given coordinates
     * @param data
     * @param line
     */
    fun addString(vararg data: String, x: Int = 0, y: Int = 0, size: Int = constants.STRING_HEIGHT, v_offset: Int = constants.STRING_HEIGHT) {
        graphics.font = graphics.font.deriveFont(size.toFloat())
        for (i in data.indices) {
            graphics.drawString(data[i], x, y + (v_offset * (i + 1)))
        }
    }

    /**
     * Displays a horizontal line at the requested x coordinate
     */
    fun horizontalLine(position: Int) {
        for (i in 0 until width - 1) {
            setPixel(i, position, true)
        }
    }

    /**
     * Displays a vertical line at the requested y coordinate
     */
    fun verticalLine(position: Int) {
        for (i in 0 until height - 1) {
            setPixel(position, i, true)
        }
    }

    /**
    * Displays a horizontal line segment
    */
    fun horizontalSegment(y: Int, x1: Int, x2: Int) {
        for (i in x1 until x2 - 1) {
            setPixel(y, i, true)
        }
    }

    /**
     * Displays a vertical line segment
     */
    fun verticalSegment(x: Int, y1: Int, y2: Int) {
        for (i in y1 until y2) {
            setPixel(x, i, true)
        }
    }

    private fun i2cWrite(register: Int, value: Int) {
        var value = value
        value = value and 0xFF
        I2C.wiringPiI2CWriteReg8(this.fd, register, value)
    }

}
/**
 * Display object using I2C communication without a reset pin
 *
 * @param width   Display width
 * @param height  Display height
 * @param gpio    GPIO object
 * @param i2c     I2C object
 * @param address Display address
 * @see SSD1306_I2C_Display.Display
 * @see GpioFactory.getInstance
 * @see com.pi4j.io.i2c.I2CFactory.getInstance
 */