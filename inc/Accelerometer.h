//
// Created by Ryan Null on 7/27/15.
//

#ifndef DESTRUCTO_ACCELEROMETER_H
#define DESTRUCTO_ACCELEROMETER_H

#include <string>

class Accelerometer {
  private:
    static std::string name;

  public:
    static const std::string ADXL_345;
    std::string getName();
};


#endif //DESTRUCTO_ACCELEROMETER_H
