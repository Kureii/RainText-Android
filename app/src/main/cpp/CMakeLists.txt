# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22)


# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("raintext")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib.cpp)

message(${ANDROID_ABI})

add_subdirectory(sha3)

include_directories( ${CMAKE_CURRENT_SOURCE_DIR}/include ${CMAKE_CURRENT_SOURCE_DIR}/sha3)

if((${ANDROID_ABI} STREQUAL "arm64-v8a") OR (${ANDROID_ABI} STREQUAL "x86_64"))
    set(CPU_FEATURES_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libcpu_features.a)
    set(NDK_COMPAT_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libndk_compat.a)

    add_library(Cpu_features STATIC IMPORTED)
    set_target_properties(Cpu_features PROPERTIES IMPORTED_LOCATION ${CPU_FEATURES_LIB_PATH})

    add_library(Ndk_compat STATIC IMPORTED)
    set_target_properties(Ndk_compat PROPERTIES IMPORTED_LOCATION ${NDK_COMPAT_LIB_PATH})
endif()

set(ARGON2_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libArgon2.a)
set(BLAKE2_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libBlake2.a)
set(RAIN_TEXT_CORE_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libRainTextCore.a)
set(SCRYPT_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libscrypt.a)
set(CRYPTOPP_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libcryptopp.a)
set(INTRINSICS_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/lib/${ANDROID_ABI}/libIntrinsics.a)


add_library(Argon2 STATIC IMPORTED)
set_target_properties(Argon2 PROPERTIES IMPORTED_LOCATION ${ARGON2_LIB_PATH})

add_library(Blake2 STATIC IMPORTED)
set_target_properties(Blake2 PROPERTIES IMPORTED_LOCATION ${BLAKE2_LIB_PATH})

add_library(Intrinsics STATIC IMPORTED)
set_target_properties(Intrinsics PROPERTIES IMPORTED_LOCATION ${INTRINSICS_LIB_PATH})

add_library(Cryptopp STATIC IMPORTED)
set_target_properties(Cryptopp PROPERTIES IMPORTED_LOCATION ${CRYPTOPP_LIB_PATH})

add_library(Scrypt STATIC IMPORTED)
set_target_properties(Scrypt PROPERTIES IMPORTED_LOCATION ${SCRYPT_LIB_PATH})

add_library(RainTextCore STATIC IMPORTED)
set_target_properties(RainTextCore PROPERTIES IMPORTED_LOCATION ${RAIN_TEXT_CORE_LIB_PATH})

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
if((${ANDROID_ABI} STREQUAL "arm64-v8a") OR (${ANDROID_ABI} STREQUAL "x86_64"))
    target_link_libraries(${CMAKE_PROJECT_NAME}
            # List libraries link to the target library
            android
            log
            RainTextCore
            sha3lib::sha3
            Argon2
            Blake2
            Cpu_features
            Ndk_compat
            Intrinsics
            Cryptopp
            Scrypt)
else()
    target_link_libraries(${CMAKE_PROJECT_NAME}
            # List libraries link to the target library
            android
            log
            RainTextCore
            sha3lib::sha3
            Argon2
            Blake2
            Intrinsics
            Cryptopp
            Scrypt)
endif()