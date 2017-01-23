git checkout master
rm *.tar.gz
cd ./pc
rm -r config logs total.log jarpackage.jar
cd ..
git checkout rpi
rm *.tar.gz
cd ./rpi
rm -r config logs total.log jarpackage.jar
cd ..
git checkout master
