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

if [ ! -f $bindir/serv499 ]
then
    echo "$bindir/serv499 is missing"
    exit 1
fi


$bindir/serv499 $port Hi 1.deck >/dev/null 2>/dev/null &
spid=$!

sleep 5

#nc localhost $port <ser1.a.in >/dev/null 2>/dev/null&
nc localhost $port -w 2 <ser1.a.in >/dev/null 2>/dev/null
#apid=$!

#nc localhost $port <ser1.b.in >/dev/null 2>/dev/null&
#bpid=$!
nc localhost $port -w2 <ser1.b.in >/dev/null 2>/dev/null

#nc localhost $port < ser1.c.in >/dev/null 2>/dev/null&
#cpid=$!
nc localhost $port -w2< ser1.c.in >/dev/null 2>/dev/null

echo "Taking a little nap"
sleep 5

#kill $apid $bpid $cpid 2> /dev/null 
#wait $apid $bpid $cpid 2> /dev/null

$bindir/client499 d g $port > $res/$name.out
result=$?

kill $spid 2> /dev/null
wait $spid 2> /dev/null

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

echo OK
exit 0
