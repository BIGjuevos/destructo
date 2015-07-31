#include <iostream>
#include <thread>

#include "inc/Config.h"
#include "inc/Databus.h"

#include "inc/inih/cpp/INIReader.h"
#include "inc/spdlog/spdlog.h"

using namespace std;

int main() {
    namespace spd = spdlog;

    auto console = spd::stdout_logger_mt("console");

    console->info("destructo");
    console->info("by Ryan Null <ryan.null@gmail.com>");
    console->info("Licensed under the MIT License, so go build cool shit!");

    INIReader configReader("./config.ini");
    Config config;

    if ( configReader.ParseError() < 0 ) {
        console->critical("Unable to read test file, probably missing (config.ini) or a syntax error");
        return 1;
    } else {
        console->info("Loading configuration");

        config.setAccelerometer(configReader.Get("external", "accelerometer", "UNKNOWN"));

        console->info("Done loading configuration");
        console->info("Accelerometer model: " + config.getAccelerometer() );
    }

    Databus databus(config, console);

//    std::thread databus(&Databus::)

    return 0;
}