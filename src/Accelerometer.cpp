//
// Created by Ryan Null on 7/27/15.
//

#include "../inc/Accelerometer.h"

// Accelerometers that are supported by this class
const std::string Accelerometer::ADXL_345 = "adxl345";

std::string Accelerometer::getName() {
    return name;
}
