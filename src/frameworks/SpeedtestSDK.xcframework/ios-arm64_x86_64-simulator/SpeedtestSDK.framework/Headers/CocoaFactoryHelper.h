//
// Created by David Hedbor on 1/24/20.
//


#pragma once
#include <OoklaSuite/Http/IFactory.h>
// Wrap objective C factories in a C++ interface.
namespace Speedtest::Sdk::Internal::Cocoa {
    extern Ookla::Http::IFactoryPtr createHttpFactory();
    extern Ookla::IThreadFactoryPtr createThreadFactory();
    extern Ookla::ISystemClockPtr createSystemClock();
}



