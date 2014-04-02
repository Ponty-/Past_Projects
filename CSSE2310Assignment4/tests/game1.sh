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

expected=0


if [ ! -f $bindir/client499 ]
then
    echo "$bindir/client499 is missing"
    exit 1
fi


#The syntax for the original netcat is different (needs a -p before the port)
nc -l $port < game1.serv > /dev/null&
servpid=$!

$bindir/client499 tim timsgame $port > $res/$name.out 2>$res/$name.err < game1.cli
result=$?

kill $servpid 2> /dev/null
wait 2> /dev/null

if [ $result != $expected ]
then
    echo "Expected $expected got $result"
    exit 1
fi

diff -q $res/$name.out $right/$name.out > /dev/null
if [ $? != 0 ]
then
   echo "Stdout mismatch - to see differences run"
   echo "diff $res/$name.out $right/$name.out"
    exit 2
fi

diff -q $res/$name.err $right/$name.err > /dev/null
if [ $? != 0 ]
then
   echo "Stderr mismatch - to see differences run"
   echo "diff $res/$name.err $right/$name.err"
   exit 3
fi

echo OK
exit 0

