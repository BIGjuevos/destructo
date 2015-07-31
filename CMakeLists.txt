cmake_minimum_required(VERSION 3.2)
project(destructo)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(INI_FILES inc/inih/cpp/INIReader.cpp inc/inih/cpp/INIReader.h inc/inih/ini.c inc/inih/ini.h)

set(SOURCE_FILES main.cpp)
add_executable(destructo ${SOURCE_FILES} ${INI_FILES} src/Accelerometer.cpp inc/Accelerometer.h src/Databus.cpp inc/Databus.h src/Config.cpp inc/Config.h src/Accelerometers/D_ADXL345.cpp inc/Accelerometers/D_ADXL345.h)