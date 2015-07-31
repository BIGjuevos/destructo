cmake_minimum_required(VERSION 3.2)
project(destructo)

##########################
# BASE COMPILATION STUFF #
##########################
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

#########################
# CONFIGURATION PARSING #
#########################
set(INI_FILES inc/inih/cpp/INIReader.cpp inc/inih/cpp/INIReader.h inc/inih/ini.c inc/inih/ini.h)

##################
# ACCELEROMETERS #
##################
set(ACCEL_CORE src/Accelerometer.cpp inc/Accelerometer.h)
set(ACCEL_ADXL345 ${ACCEL_CORE} src/Accelerometers/D_ADXL345.cpp inc/Accelerometers/D_ADXL345.h)
set(ACCEL_FILES ${ACCEL_ADXL345})

####################
# CORE APPLICATION #
####################
set(SOURCE_FILES main.cpp)

add_executable(destructo ${SOURCE_FILES} ${INI_FILES} ${ACCEL_FILES} src/Databus.cpp inc/Databus.h src/Config.cpp inc/Config.h)