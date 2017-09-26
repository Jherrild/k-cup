package main.kotlin

import com.pi4j.io.gpio.GpioFactory
import main.kotlin.display.Display
import main.kotlin.power.Boiler
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import kotlin.concurrent.fixedRateTimer

/**
 * @author jestenh@gmail.com
 * Created on 8/22/17
 */
var gpio = GpioFactory.getInstance()

var boiler = Boiler()
var display = Display(gpio = gpio)
var powerState = false

fun main(args: Array<String>) {
    boiler.init()
    display.init()
    fixedRateTimer("Display Update", true, 0.toLong(), 200.toLong()) {
        display.write("Set: " + boiler.pid.setTemp.toString(), 5)
        display.write("Temp: " + boiler.pid.currentTemp.toString(), 69)
        display.vSegment(64, 0, 18)
        display.hLine(18)
        display.update()
    }

    embeddedServer(Netty, 50505) {
        routing {
            get("/api/temperature/brew/target") {
                call.respondText("${boiler.pid.setTemp}", ContentType.Text.Html)
            }
            get("/api/temperature/brew/target/{temp}") {
                val newTemp = call.parameters["temp"]?.toInt()
                boiler.pid.setTemp = newTemp!!
                call.respondText("Set temperature to ${boiler.pid.setTemp}", ContentType.Text.Html)
            }
            get("/api/temperature/brew/current") {
                call.respondText("${boiler.pid.currentTemp}", ContentType.Text.Html)
            }
            get("/api/power") {
                if (powerState) {
                    call.respondText("On", ContentType.Text.Html)
                } else {
                    call.respondText("Off", ContentType.Text.Html)
                }
            }
            get("/api/power/{state}") {
                if (call.parameters["state"]?.toLowerCase() == "on") {
                    powerState = true
                } else if (call.parameters["state"]?.toLowerCase() == "off") {
                    powerState = false
                }

                if (powerState) {
                    call.respondText("On", ContentType.Text.Html)
                } else {
                    call.respondText("Off", ContentType.Text.Html)
                }
            }
            get("/api/display") {
                display.write("Hello world")
                display.update()
                call.respondText("Hello world", ContentType.Text.Html)
            }
            get("/api/display/{content}") {
                val content = call.parameters["content"].toString()
                display.write(content)
                //display.update()
                call.respondText(content, ContentType.Text.Html)
            }
            get("/api/display/clear") {
                display.clear()
                //display.update()
                call.respondText("Cleared Display", ContentType.Text.Html)
            }
            get("/api/display/vline/{index}") {
                val value = call.parameters["index"]!!.toInt()
                call.respondText("Attempted to draw a line at '${value}'", ContentType.Text.Html)
                display.vLine(value)
                //display.update()
            }
            get("/api/display/hline/{index}") {
                val value = call.parameters["index"]!!.toInt()
                call.respondText("Attempted to draw a line at '${value}'", ContentType.Text.Html)
                display.hLine(value)
                //display.update()
            }
            get("/api/display/update") {
                call.respondText("Updating screen", ContentType.Text.Html)
                display.write("Set: " + boiler.pid.setTemp.toString(), 5)
                display.write("Temp: " + boiler.pid.currentTemp.toString(), 69)
                display.vSegment(64, 0, 18)
                display.hLine(18)
                display.update()
            }
        }
    }.start(wait = true)

}
