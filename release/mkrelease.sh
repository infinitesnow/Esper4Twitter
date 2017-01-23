#!/bin/bash
./clean.sh

### PC
git checkout master
cp -r ../config ./pc/
ant -f ./build.xml 
tar -cvzf Esper4Twitter.tar.gz pc/

### RPI
git checkout rpi
cp -r ../config ./rpi/
ant -f ./build.xml 
tar -cvzf rpi.tar.gz rpi/

git checkout master
