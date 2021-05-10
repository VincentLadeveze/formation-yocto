#!/bin/sh

# Set wlan0 interface with static IP address, should be unique
ifconfig wlan0 192.168.1.70

# Change SSID and PASSWD to 
wpa_passphrase Freebox-9B920B jocasse3-adsignandi\!-admittebat-cuppes? > /etc/wpa_supplicant.conf

# Router IP address
route add default gw .254

# Launch interface wlan0 with config
wpa_supplicant -B -i   wlan0    -c    /etc/wpa_supplicant.conf

echo “nameserver 8.8.8.8” >> /etc/resolv.conf
echo “nameserver 192.168.1.254” >> /etc/resolv.conf

exit 0
