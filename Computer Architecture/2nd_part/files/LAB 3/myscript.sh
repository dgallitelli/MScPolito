#!bin/sh

cd ./automotive/basicmath/
rm -rf m5out/
mkdir m5out
/opt/gem5/build/ALPHA/gem5.opt ../../mygem5script.py -c basicmath_small
cd ../..
cd ./automotive/bitcount/ 
rm -rf m5out/
mkdir m5out
/opt/gem5/build/ALPHA/gem5.opt ../../mygem5script.py -c bitcnts -o "7500"
cd ../..
cd ./automotive/qsort/ 
rm -rf m5out/
mkdir m5out
cp input_small.dat ./m5out/input_small.dat
/opt/gem5/build/ALPHA/gem5.opt ../../mygem5script.py -c qsort_small -o "input_small.dat > output.txt"
cd ../..
cd ./automotive/susan/ 
rm -rf m5out/
mkdir m5out
cp input_small.pgm ./m5out/input_small.pgm 
/opt/gem5/build/ALPHA/gem5.opt ../../mygem5script.py -c susan -o "input_small.pgm out.pgm"
cd ../..
