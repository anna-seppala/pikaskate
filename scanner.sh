#! /bin/bash

# this script is a test to find devices connected to network

if [ -d /sys/class/net/wlan0 ]; then
    echo "wlan0 is ";
    if cat /sys/class/net/wlan0/operstate | grep up; then
            nmap -O 192.168.11.1-10;
    fi
fi

