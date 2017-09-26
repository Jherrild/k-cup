package main.kotlin

import com.pi4j.io.gpio.GpioFactory
import main.kotlin.display.Display
import main.kotlin.power.Boiler
import main.kotlin.power.Machine
import main.kotlin.temp.PID
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing

/**
 * @author jestenh@gmail.com
 * Created on 8/22/17
 */
var gpio = GpioFactory.getInstance()

var pidController = PID(200, 0)
var boilerController = Boiler()
var machineController = Machine()
var displayController = Display(gpio = gpio)
var powerState = false

fun main(args: Array<String>) {
    displayController.init()
    displayController.write("Set: " + pidController.setTemp.toString(), 5)
    displayController.write("Temp: " + pidController.currentTemp.toString(), 69)
    displayController.vSegment(64, 0, 18)
    displayController.hLine(18)
    displayController.update()

    embeddedServer(Netty, 50505) {
        routing {
            get("/api/temperature/brew/target") {
                call.respondText("${pidController.setTemp}", ContentType.Text.Html)
            }
            get("/api/temperature/brew/target/{temp}") {
                val newTemp = call.parameters["temp"]?.toInt()
                pidController.setTemp = newTemp!!
                call.respondText("Set temperature to ${pidController.setTemp}", ContentType.Text.Html)
            }
            get("/api/temperature/brew/current") {
                call.respondText("${pidController.currentTemp}", ContentType.Text.Html)
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
                displayController.write("Hello world")
                displayController.update()
                call.respondText("Hello world", ContentType.Text.Html)
            }
            get("/api/display/{content}") {
                val content = call.parameters["content"].toString()
                displayController.write(content)
                displayController.update()
                call.respondText(content, ContentType.Text.Html)
            }
            get("/api/display/clear") {
                displayController.clear()
                displayController.update()
                call.respondText("Cleared Display", ContentType.Text.Html)
            }
            get("/api/display/vline/{index}") {
                val value = call.parameters["index"]!!.toInt()
                call.respondText("Attempted to draw a line at '${value}'", ContentType.Text.Html)
                displayController.vLine(value)
                displayController.update()
            }
            get("/api/display/hline/{index}") {
                val value = call.parameters["index"]!!.toInt()
                call.respondText("Attempted to draw a line at '${value}'", ContentType.Text.Html)
                displayController.hLine(value)
                displayController.update()
            }
            get("/api/display/update") {
                call.respondText("Updating screen", ContentType.Text.Html)
                displayController.write("Set: " + pidController.setTemp.toString(), 5)
                displayController.write("Temp: " + pidController.currentTemp.toString(), 69)
                displayController.vSegment(64, 0, 18)
                displayController.hLine(18)
                displayController.update()
            }
        }
    }.start(wait = true)

}
