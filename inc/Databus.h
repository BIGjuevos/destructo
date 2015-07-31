//
// Created by Ryan Null on 7/30/15.
//

#ifndef DESTRUCTO_DATABUS_H
#define DESTRUCTO_DATABUS_H

#include "Config.h"
#include "Accelerometer.h"
#include "spdlog/spdlog.h"

class Databus {
  private:
    Config config;
    Accelerometer accelerometer;
    std::shared_ptr<spdlog::logger> console;

  public:
    Databus(Config, std::shared_ptr<spdlog::logger>);

};


#endif //DESTRUCTO_DATABUS_H
