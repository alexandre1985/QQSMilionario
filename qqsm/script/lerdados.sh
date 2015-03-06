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
      for (( c=1; c<5; c++ ))
      do
        resposta="$(echo $name | cut -d";" -f$c)"
        resposta="$(echo -e "${resposta}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
        respostas[$((c-1))]=$resposta
      done
      echo "questoes.setResposta(new String[]{\"${respostas[0]}\",\"${respostas[1]}\",\"${respostas[2]}\",\"${respostas[3]}\"});"
    fi
    if [ $i -eq 4 ]
    then
      echo "questoes.setRespostaCorrecta($name);"
      i=0
    fi
    i=$((i+1))
done < $1

#nivel (de 1 a 15)
#pergunta
#resposta1,resposta2,reposta3,reposta4
#numero da resposta correcta (de 1 a 4)
