#!/bin/sh

/opt/gem5/build/ALPHA/gem5.opt /opt/gem5/configs/example/se.py --cpu-type=detailed --caches -c qsort_small -o "input_small.dat > output.txt"
