package display

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */
class SSD1306_Constants {
    val SSD1306_I2C_ADDRESS: Short = 0x3C
    val SSD1306_SETCONTRAST: Short = 0x81
    val SSD1306_DISPLAYALLON_RESUME: Short = 0xA4
    val SSD1306_DISPLAYALLON: Short = 0xA5
    val SSD1306_NORMALDISPLAY: Short = 0xA6
    val SSD1306_INVERTDISPLAY: Short = 0xA7
    val SSD1306_DISPLAYOFF: Short = 0xAE
    val SSD1306_DISPLAYON: Short = 0xAF
    val SSD1306_SETDISPLAYOFFSET: Short = 0xD3
    val SSD1306_SETCOMPINS: Short = 0xDA
    val SSD1306_SETVCOMDETECT: Short = 0xDB
    val SSD1306_SETDISPLAYCLOCKDIV: Short = 0xD5
    val SSD1306_SETPRECHARGE: Short = 0xD9
    val SSD1306_SETMULTIPLEX: Short = 0xA8
    val SSD1306_SETLOWCOLUMN: Short = 0x00
    val SSD1306_SETHIGHCOLUMN: Short = 0x10
    val SSD1306_SETSTARTLINE: Short = 0x40
    val SSD1306_MEMORYMODE: Short = 0x20
    val SSD1306_COLUMNADDR: Short = 0x21
    val SSD1306_PAGEADDR: Short = 0x22
    val SSD1306_COMSCANINC: Short = 0xC0
    val SSD1306_COMSCANDEC: Short = 0xC8
    val SSD1306_SEGREMAP: Short = 0xA0
    val SSD1306_CHARGEPUMP: Short = 0x8D
    val SSD1306_EXTERNALVCC: Short = 0x1
    val SSD1306_SWITCHCAPVCC: Short = 0x2

    val SSD1306_ACTIVATE_SCROLL: Short = 0x2F
    val SSD1306_DEACTIVATE_SCROLL: Short = 0x2E
    val SSD1306_SET_VERTICAL_SCROLL_AREA: Short = 0xA3
    val SSD1306_RIGHT_HORIZONTAL_SCROLL: Short = 0x26
    val SSD1306_LEFT_HORIZONTAL_SCROLL: Short = 0x27
    val SSD1306_VERTICAL_AND_RIGHT_HORIZONTAL_SCROLL: Short = 0x29
    val SSD1306_VERTICAL_AND_LEFT_HORIZONTAL_SCROLL: Short = 0x2A

    val LCD_WIDTH_128 = 128
    val LCD_WIDTH_96 = 96
    val LCD_HEIGHT_64 = 64
    val LCD_HEIGHT_32 = 32
    val LCD_HEIGHT_16 = 16

    val STRING_HEIGHT = 16
}