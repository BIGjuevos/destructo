//
// Created by Ryan Null on 7/30/15.
//

#include "../inc/Databus.h"
#include "../inc/Accelerometers/D_ADXL345.h"

Databus::Databus(Config c, std::shared_ptr<spdlog::logger> l) {
    config = c;
    console = l;

    // init our accelerometer
    if ( c.getAccelerometer() == Accelerometer::ADXL_345 ) {
        accelerometer = D_ADXL345();
        console->info("Initialized a new " + accelerometer.getName());
    } else {
        console->critical("Invalid Accelerometer specified.  Support not offered for " + c.getAccelerometer() );
    }
}
