
import display.Oled
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import power.Boiler
import power.Machine
import temp.PID

/**
 * @author jestenh@gmail.com
 * Created on 8/22/17
 */
fun main(args: Array<String>) {
    var pidController = PID(200, 0)
    var boilerController = Boiler()
    var machineController = Machine()
    var display = Oled()
    var powerState = false

    embeddedServer(Netty, 8080) {
        routing {
            get("/api/temperature/target") {
               call.respondText("${pidController.setTemp}", ContentType.Text.Html)
            }
            get("/api/temperature/target/{temp}") {
                val newTemp = call.parameters["temp"]?.toInt()
                pidController.setTemp = newTemp!!
                call.respondText("Set temperature to ${pidController.setTemp}", ContentType.Text.Html)
            }
            get("/api/temperature/current") {
                call.respondText("${pidController.currentTemp}", ContentType.Text.Html)
            }
            get("/api/power") {
                if(powerState) {
                    call.respondText("On", ContentType.Text.Html)
                }else {
                    call.respondText("Off", ContentType.Text.Html)
                }
            }
            get("/api/power/{state}") {
                if(call.parameters["state"]?.toLowerCase() == "on") {
                    powerState = true
                }else if(call.parameters["state"]?.toLowerCase() == "off") {
                    powerState = false
                }

                if(powerState) {
                    call.respondText("On", ContentType.Text.Html)
                }else {
                    call.respondText("Off", ContentType.Text.Html)
                }
            }
            get("/api/display/test/{content}") {
                display.displayTest("Hello world")
            }
        }
    }.start(wait = true)
}
