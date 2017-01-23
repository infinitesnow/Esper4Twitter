#!/bin/bash

### PC
git checkout master
rm -r ./pc/config
cp -r ../config ./pc/
tar -cvzf Esper4Twitter.tar.gz pc/

### RPI
git checkout rpi
rm -r ./rpi/config
cp -r ../config ./rpi/
tar -cvzf rpi.tar.gz rpi/

git checkout master
