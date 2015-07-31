#include <iostream>
#include <thread>

#include "inc/Databus.h"

#include "inc/spdlog/spdlog.h"

using namespace std;

int main() {
    namespace spd = spdlog;

    auto console = spd::stdout_logger_mt("console");

    console->info("destructo");
    console->info("by Ryan Null <ryan.null@gmail.com>");
    console->info("Licensed under the MIT License");

    Databus databus;

//    std::thread databus(&Databus::)

    return 0;
}