#!/bin/bash
if [ -d backup ]
then
echo "Directory already exists!"
else
mkdir backup
fi 

date=$(date +"%Y-%m-%d_%H-%M-%S")
cp *.txt backup/
echo "Backup completed"
