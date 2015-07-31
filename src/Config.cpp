//
// Created by Ryan Null on 7/30/15.
//

#include "../inc/Config.h"

Config::Config() {

}

void Config::setAccelerometer(std::string accel) {
    accelerometer = accel;
}

std::string Config::getAccelerometer() {
    return accelerometer;
}
