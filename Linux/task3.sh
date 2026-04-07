#!/bin/bash
echo "Enter any number"
read number
if [ $number -ge 10 ]
then echo "The number $number is greater than or equal to 10"
else echo "The number $number is smaller than 10"
fi

if [ $((number % 2)) -eq 0 ]
then echo "The number $number is even!"
else echo "Also the number $number is odd!"
fi
