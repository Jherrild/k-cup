package com.herrild.espiresso.temp

/**
 * @author jherrild@expedia.com
 * Created on 9/24/17
 */
class PID(var setTemp: Int, var currentTemp: Float, var p: Float = 2.toFloat(), var i: Float = 0.toFloat(), var d: Float = 0.toFloat()) {

    fun set(temp: Int) {
        setTemp = temp
    }

    fun tune(deltaP: Float = 0.toFloat(), deltaI: Float = 0.toFloat(), deltaD: Float = 0.toFloat()) {
        p += deltaP
        i += deltaI
        d += deltaD
    }

    fun execute() {
        //TODO: Implement a single iteration of the pid function
    }

    fun getOutput(set: Int, actual: Int) {

    }
}