#! /bin/bash

echo "enter starting IP address:"
read firstIP

echo "enter last octet of last IP address:"
read lastIPoctet

echo "enter port number you want to scan:"
read port

#outputting to /dev/null discards info without terminal output
if [ $port -lt 0 ] then
        nmap $firstIP-$lastIPoctet >/dev/null -oG scanSQLout
#else
#        nmap -sT $firstIP-$lastIPoctet -p $port >/dev/null -oG ~/devel/src/hack/scanSQLout
fi
cat ~/devel/src/hack/scanSQLout
