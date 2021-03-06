package com.herrild.espiresso

import com.herrild.espiresso.display.Display
import com.herrild.espiresso.enums.SwitchType
import com.herrild.espiresso.input.RelaySwitch
import com.herrild.espiresso.input.TemperatureSwitch
import com.herrild.espiresso.input.Thermocouple
import com.herrild.espiresso.input.ToggleSwitch
import com.herrild.espiresso.output.PwmRelay
import com.herrild.espiresso.output.ToggleRelay
import com.herrild.espiresso.power.Boiler
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiPin
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.slf4j.LoggerFactory
import kotlin.concurrent.fixedRateTimer

/**
 * @author jestenh@gmail.com
 * Created on 8/22/17
 */
var logger = LoggerFactory.getLogger("Controller")
var gpio = GpioFactory.getInstance()
var temp_sensor = Thermocouple(gpio, RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_10)
var display = Display(gpio = gpio)
//TODO: Check pins against hardware for temp up and down switches, and relays
var pumpRelay = ToggleRelay("Pump Relay", gpio, RaspiPin.GPIO_24)
var boilerRelay = PwmRelay("Boiler Relay", gpio, RaspiPin.GPIO_26, 26)
var brewSwitch = ToggleSwitch(gpio, RaspiPin.GPIO_29, "BrewSwitch")
var shotSwitch = RelaySwitch(gpio, RaspiPin.GPIO_28, "ShotSwitch", pumpRelay)
var boiler = Boiler(temp_sensor = temp_sensor, brew_switch = brewSwitch)
var upButton = TemperatureSwitch(gpio, RaspiPin.GPIO_27, "TempUpButton", boiler, SwitchType.UP)
var downButton = TemperatureSwitch(gpio, RaspiPin.GPIO_25, "TempDownButton", boiler, SwitchType.DOWN)
// TODO: Maybe read pins into hashmap from configuration file?
// var pinMap = HashMap<String, RaspiPin>()
//TODO: Should create POWER switch to override remote power state change on a hardware circuit. This should prevent
// the boiler from being turned on, even if the boiler is "on" - this is a hardware change, and will not be reflected
// here

fun main(args: Array<String>) {
    temp_sensor.init()
    brewSwitch.init()
    shotSwitch.init()
    boiler.init()
    upButton.init()
    downButton.init()
    display.init()
    boilerRelay.init()

    fixedRateTimer("Display Update Timer", true, 0.toLong(), 200.toLong()) {
        updateScreen()
    }

    fixedRateTimer("Boiler Update Timer", true, 0.toLong(), 100.toLong()) {
        updateBoiler()
        controlPump()
    }

    embeddedServer(Netty, 50505) {
        routing {
            get("/api/temperature/brew/target") {
                call.respondText("${boiler.pid.setTemp}", ContentType.Text.Html)
            }

            get("/api/temperature/brew/target/{temp}") {
                val newTemp = call.parameters["com/herrild/espiresso/tempherrild/espiresso/temp"]?.toInt()
                boiler.pid.setTemp = newTemp!!
                call.respondText("Set temperature to ${boiler.pid.setTemp}", ContentType.Text.Html)
            }

            get("/api/temperature/brew/current") {
                call.respondText("${boiler.currentTemp}", ContentType.Text.Html)
            }

            get("/api/power") {
                if (boiler.power_state) {
                    call.respondText("On", ContentType.Text.Html)
                } else {
                    call.respondText("Off", ContentType.Text.Html)
                }
            }

            get("/api/power/{state}") {
                if (call.parameters["state"]?.toLowerCase() == "on") {
                    boiler.power_state = true
                } else if (call.parameters["state"]?.toLowerCase() == "off") {
                    boiler.power_state = false
                }

                if (boiler.power_state) {
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
                display.write("Temp: " + boiler.currentTemp.toString(), 69)
                display.vSegment(64, 0, 18)
                display.hLine(18)
                display.update()
            }
        }
    }.start(wait = true)
}


fun updateScreen() {
    //TODO: Write centered text method in Display/SSD1306
    display.clear()
    display.write("Set: " + boiler.pid.setTemp.toString(), 5, 0, 15)

    // Brew mode
    if (brewSwitch.state) {
        display.write("Brew", 85, 0, 12, 15)
    }else { // Steam mode
        display.write("Steam", 85, 0, 12, 15)
    }

    // Not pulling a shot
    if (!shotSwitch.state) {
        display.write(boiler.currentTemp.toInt().toString(), 52, 24, 25)
    }else { // Pulling a shot
        display.write( ((System.currentTimeMillis() - shotSwitch.last_modified) / 1000).toInt().toString(), 52, 24, 25)
    }

    display.updateText()
    display.hLine(18)
    display.update()
}

fun updateBoiler() {
    logger.info("Current temperature is: " + boiler.updateTemperature() + " C")

    // Runs pid function, maps output onto the range of the Boiler Relay's input, and then updates the relay
    boilerRelay.update(boiler.pid.positiveMap(boiler.runPid(), 10.toFloat(), boilerRelay.range))

    if (brewSwitch.state) {
        boiler.pid.setTemp = boiler.brew_temp
    }else {
        boiler.pid.setTemp = boiler.steam_temp
    }
}

fun controlPump() {
    if(shotSwitch.state && brewSwitch.state) {
        pumpRelay.on()
    }else {
        pumpRelay.off()
    }
}
