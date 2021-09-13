#!/bin/sh


# Linking ld-linux-x86-64.so.2 to the lib/ location (Update accordingly)
ln -s /lib/lib64/* /lib

ln -s /lib/libnsl.so.2 /usr/lib/libnsl.so.1
ln -s /lib/libc.so /usr/lib/libresolv.so.2

#Added the changes in DockerFile