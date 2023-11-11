cmake_minimum_required(VERSION 3.14)
project(sha3lib C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_C_EXTENSIONS OFF)

add_library(sha3 STATIC sha3.c)

target_include_directories(sha3 PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

# Oprava: Přidání aliasu pro cíl "sha3"
add_library(sha3lib::sha3 ALIAS sha3)