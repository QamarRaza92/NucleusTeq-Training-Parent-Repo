#!/bin/bash
home=$HOME
echo "The home directory is $home"
read -p "Enter username: " name
read -p "Print number whose table is to be printed: " table
read -p "Enter your age: " age

echo "Table of $table:"
for i in {1..10}
do echo "$table X $i = $((i * table))"
done

if [ $age -ge 18 ]
then echo "$name can vote :)"
else echo "$name can not vote :("
fi

echo "Also the user name is: $(whoami)"




