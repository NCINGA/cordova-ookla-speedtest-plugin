//
// Created by David Hedbor on 2023-05-31.
//
#pragma once

#include "ErrorTypeJsonAPI.hpp"

namespace Speedtest::Sdk::Internal::Cocoa {
    extern Result::ErrorType errorTypeFromErrorCode(int64_t code, std::string &msg);
}
