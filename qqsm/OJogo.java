import java.util.Random;

/**
 * Esta classe trata do nível e define todos métodos principais do jogo.
 * 
 * @author Daniel Santos
 * @version 0.1
 */
public class OJogo
{
    BancoDeDados questoes;
    int nivel=1;
    
    public OJogo()
    {
        questoes = new BancoDeDados();
    }
    
    public String getPergunta()
    {
        return questoes.get().getPergunta(nivel);
    }
    
    public String[] getRespostas()
    {
        return questoes.get().getResposta();
    }
    
    public int getRespostaCorrecta()
    {
        return questoes.get().getRespostaCorrecta();
    }
    
    public int getNivel() { return nivel; }
    public void incrementaNivel() { nivel++; }
    public boolean eNivelPatamar()
    {
        if(nivel%5 == 0)
            return true;
        return false;
    }
    
    /**
     * Indica quais as respostas a apagar. Vai desde 1 até 4.
     */
    public int[] ajuda5050()
    {
        Random rand = new Random();
        int[] resultado = new int[2];
        resultado[0] = -1;
        for(int i=0; i < 2; i++) {
            int num;
            do {
                num = rand.nextInt(4)+1;
            } while( num == questoes.get().getRespostaCorrecta() || num == resultado[0]);
            resultado[i] = num;
        }
        return resultado;
    }
    
    public boolean acabou()
    {
        if( nivel == 15)
            return true;
        return false;
    }
}
