#!/bin/bash
i=1
while read line
do
    name=$line
    if [ "$name" = "" ]
    then
      continue;
    fi
    if [ $i -eq 1 ]
    then
      num=$name
    fi
    if [ $i -eq 2 ]
    then
      echo "questoes.setPergunta($num, \"$name\");"
    fi
    if [ $i -eq 3 ]
    then
      printf "questoes.setResposta(new String[]{\"${name}\","
    fi
    if [ $i -eq 4 ]
    then
      printf "\"${name}\","
    fi
    if [ $i -eq 5 ]
    then
      printf "\"${name}\","
    fi
    if [ $i -eq 6 ]
    then
      printf "\"${name}\"});\n"
    fi
    if [ $i -eq 7 ]
    then
      echo "questoes.setRespostaCorrecta($name);"
      i=0
    fi
    i=$((i+1))
done < $1

