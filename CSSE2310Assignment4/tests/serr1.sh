#!/bin/bash

if [ $# != 4 ]
then
    echo "Usage: $0 binarydir port resultdir correct_dir" 
    exit 1
fi



bindir=$1
port=$2
res=$3
right=$4
name=$0

expected=1

if [ ! -f $bindir/serv499 ]
then
    echo "$bindir/serv499 is missing"
    exit 1
fi



$bindir/serv499 > $res/$name.out 2>$res/$name.err
result=$?

if [ $result != $expected ]
then
    echo "Expected $expected got $result"
    exit 1
fi

diff -q $res/$name.out $right/$name.out > /dev/null
if [ $? != 0 ]
then
   echo "Stdout mismatch"
   echo "diff $res/$name.out $right/$name.out"
   exit 2
fi

diff -q $res/$name.err $right/$name.err > /dev/null
if [ $? != 0 ]
then
   echo "Stderr mismatch"
   echo "diff $res/$name.err $right/$name.err"
   exit 3
fi

echo OK
exit 0

