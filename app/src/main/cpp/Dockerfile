FROM ubuntu:latest

LABEL maintainer="ad.tomik@seznam.cz"
LABEL version="0.1.0"

RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    git \
    android-tools-adb \
    android-tools-fastboot

COPY . /app

WORKDIR /app

# RUN mkdir build && cd build && cmake .. && make