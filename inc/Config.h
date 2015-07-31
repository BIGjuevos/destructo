//
// Created by Ryan Null on 7/30/15.
//

#ifndef DESTRUCTO_CONFIG_H
#define DESTRUCTO_CONFIG_H

#include <string>

class Config {
  private:
    std::string accelerometer;

  public:
    Config();
    void setAccelerometer(std::string);
    std::string getAccelerometer();
};


#endif //DESTRUCTO_CONFIG_H
