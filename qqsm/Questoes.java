import java.util.ArrayList;
import java.util.Random;

/**
 * Class que contém as perguntas e as respostas do "Quem Quer Ser Milionário"
 * 
 * @author Daniel Santos
 * @version 0.1
 */
public class Questoes
{
    //todas as perguntas consoante o nivel. Há 15 níveis.
    ArrayList<ArrayList<String>> perguntas;
    ArrayList<ArrayList<String[]>> respostas;
    ArrayList<ArrayList<Integer>> respostasCorrectas;
    //variavel para saber qual a resposta a dar à pergunta.
    int numeroDaPergunta;
    //variavel para saber qual o nível da resposta para dar à pergunta. Vai de 1 a 15
    int nivel;
    
    public Questoes()
    {
        perguntas = new ArrayList<ArrayList<String>>();
        for(int i=0; i < 15; i++) {
            perguntas.add(new ArrayList<String>());
        }
        respostas = new ArrayList<ArrayList<String[]>>();
        for(int i=0; i < 15; i++) {
            respostas.add(new ArrayList<String[]>());
        }
        respostasCorrectas = new ArrayList<ArrayList<Integer>>();
        for(int i=0; i < 15; i++) {
            respostasCorrectas.add(new ArrayList<Integer>());
        }
    }
    
    public String getPergunta(int nivel)
    {
        if(nivel >= 1 && nivel <= 15)
        {
            //cria gerador de randoms para ir buscar a pergunta
            Random rand = new Random();
            
            this.nivel = nivel;
            numeroDaPergunta = rand.nextInt(perguntas.get(nivel-1).size());
            return perguntas.get(nivel-1).get(numeroDaPergunta);
        }
        return null; //caso o nivel seja fora do intervalo
    }
    
    /**
     * Define a pergunta consoante o nivel.
     * @param nivel é o nível (etapa) do jogo a que a pergunta pertence
     */
    public void setPergunta(int nivel, String pergunta)
    {
        if(nivel >= 1 && nivel <= 15)
        {
            this.nivel = nivel;
            perguntas.get(nivel-1).add(pergunta);
        }
    }
    
    public void setResposta(String[] quatroRespostas)
    {
        respostas.get(nivel-1).add(quatroRespostas);
    }
    
    public String[] getResposta()
    {
        return respostas.get(nivel-1).get(numeroDaPergunta);
    }
    
    /**
     * Define qual é a resposta correcta
     * @param numeroDaResposta é o número da resposta correcta e vai desde 1 até 4
     */
    public void setRespostaCorrecta(int numeroDaResposta)
    {
        if(numeroDaResposta >= 1 && numeroDaResposta <= 4)
        {
            respostasCorrectas.get(nivel-1).add(numeroDaResposta);
        }
    }
    
    public int getRespostaCorrecta()
    {
        return respostasCorrectas.get(nivel-1).get(numeroDaPergunta);
    }
    
    /**
     * Retorna o numero de perguntas de um nível
     */
    public int getSize(int nivel)
    {
        return perguntas.get(nivel-1).size();
    }
    
    /**
     * Retorna a pergunta segundo o nivel e o numero da pergunta
     * @param nivel Nivel do jogo (1 a 15)
     * @param numero Nº da pergunta (de 0 a ...)
     */
    public String getPergunta(int nivel, int numero)
    {
        return perguntas.get(nivel-1).get(numero);
    }
}
