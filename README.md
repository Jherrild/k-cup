# Project Motivation
This project was the result of my old "Gaggia Coffee" espresso machine's inconsistency. Occasionally I'd pull a fantastic shot, but most of the time I'd end up with something mediocre - I attributed this to temperature control. I looked at buying a pid, but I wanted a device which would do more than that if I was going to spend $50-100.

I finally gave in when my machine's analogue brew thermostat died. It was purchase a new one for $12 (before shipping), or replace the need for the part completely with a digital temperature probe, and finally finish my project.

------

## Software
### Software Choices:
+ I decided to write this application in Kotlin. This was partially because it's a JVM language and I'm very familiar with Java, and partially because I wanted to learn more about Kotlin, and I figured the easiest way was just to write an application.

## Hardware
### Parts:

+ Espresso machine (obviously)
+ Raspberry Pi 2b - you could probably use a different model, this is just the one I used
+ MAX31855 (Thermocouple controller)
+ Thermocouple (Type K)
+ Brass Threaded Hexagonal Standoff Spacer (M4x10mmx16mm)
+ SSD1306 (OLED Display)
+ Solid state relay with heatsink

### Hardware choices:
The hardware for this project went through several iterations, but I settled on the configuration I did for several reasons.

1. Thermocouples - these are simpler and smaller than thermistors. I started out attempting to make use of the pervasive ds18b20 thermistor, but it was large, and it's temperature range dangerously close to the max temperature I was trying to measure. Ultimately, I went with a K type thermistor because it was small enough that I was able to encase the end of the probe in thermally conductive epoxy inside of a small brass hexagonal motherboard standoff with the same thread size as the original brew thermostat. Brass isn't the most thermally conductive material, but it's almost [four times as conductive as steel](https://www.metalsupermarkets.com/which-metals-conduct-heat-best/), which is what my ds18b20 was encased in, so I figured it was good enough.
2. Raspberry Pi - originally I had decided to implement this project on a [pyboard](https://www.adafruit.com/product/2390), but after discovering that the pyboard didn't support true multithreading, and because of my greater familiarity with Java/Linux, I decided to work on a Raspberry Pi in Java/Kotlin instead.
3. OLED display - I used the SSD1306 because I had it lying around from a previous project. This is not important, and the Display class could be configured to use a separate display driver in order to utilize a different physical display.
4. SSR (Solid state relay)
