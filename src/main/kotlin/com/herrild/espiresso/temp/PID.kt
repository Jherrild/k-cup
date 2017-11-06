package com.herrild.espiresso.temp

/**
 * @author jestenh@gmail.com
 * Created on 9/24/17
 */
class PID(var setTemp: Int, var p: Float = 2.toFloat(), var i: Float = 0.toFloat(), var d: Float = 0.toFloat(), var bias: Float = 0.toFloat()) {

    var t1 = System.currentTimeMillis()
    var t2 = System.currentTimeMillis()
    var integral = 0.toFloat()
    var derivative = 0.toFloat()
    var error = 0.toFloat()

    var maxOut = 0.toFloat()

    fun set(temp: Int) {
        setTemp = temp
    }

    fun reset() {
        integral = 0.toFloat()
        derivative = 0.toFloat()
        error = 0.toFloat()
        t1 = System.currentTimeMillis()
        t2 = System.currentTimeMillis()
    }

    //TODO: Write an AutoTune function - this should run some number of iterations of the execute/tune functions until an optimal set of values are found for P, I, D, and bias
    fun tune(deltaP: Float = 0.toFloat(), deltaI: Float = 0.toFloat(), deltaD: Float = 0.toFloat()) {
        p += deltaP
        i += deltaI
        d += deltaD
    }

    fun execute(temp: Float) : Float {
        //Set error and previous error
        val pError = error
        error = setTemp - temp

        //Set time, previous time, and delta T
        t1 = t2
        t2 = System.currentTimeMillis()
        val deltaT = (t2 - t1)

        //Calculations
        integral += (integral * deltaT)
        derivative = (error - pError) / deltaT
        val out = (p * error) + (i * integral) + (d * derivative) + bias
        maxOut = Math.max(out, maxOut)
        return out
    }

    /** Maps one range onto another - meant to positiveMap the output of a PID function onto the range of the SSR's
     *  This implementation assumes that both ranges start at 0, making this a simple scaling function
     *  @arg input - Value to be mapped
     *  @arg maxOne - maximum value from first range (range to be mapped from)
     *  @arg maxTwo - maximum value from second range (range to be mapped)
     */
    fun positiveMap(input: Float, maxOne: Float, maxTwo: Int) : Int {
        //Set negative values to 0
        var adjustedInput = input
        if(adjustedInput < 0) {
            adjustedInput = 0.toFloat()
        }

        val rangeIn = Math.max(maxOne, maxOut)
        val rangeOut = maxTwo.toFloat()

        val conversion = rangeOut / rangeIn
        return (adjustedInput * conversion).toInt()
    }
}