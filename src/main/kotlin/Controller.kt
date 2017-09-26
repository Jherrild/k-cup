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
fun main(args: Array<String>) {
    var gpio = GpioFactory.getInstance()

    var pidController = PID(200, 0)
    var boilerController = Boiler()
    var machineController = Machine()
    var displayController = Display(gpio = gpio)
    var powerState = false


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
            get("/api/display/test") {
                displayController.displayTest("Hello world")
                call.respondText("Hello world", ContentType.Text.Html)
            }
            get("/api/display/{content}") {
                val content = call.parameters["content"].toString()
                displayController.displayTest(content)
                call.respondText(content, ContentType.Text.Html)
            }
        }
    }.start(wait = true)

}
