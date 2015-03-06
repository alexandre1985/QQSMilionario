
/**
 * Classe que contém os prémios em euros para cada nivel
 * 
 * @author Daniel
 * @version 0.1
 */
public class Premios
{
    int[] premios;
    
    public Premios()
    {
        premios = new int[15];
        premios[0] = 50;
        premios[1] = 100;
        premios[2] = 200;
        premios[3] = 300;
        premios[4] = 500;
        premios[5] = 700;
        premios[6] = 1200;
        premios[7] = 2000;
        premios[8] = 3000;
        premios[9] = 4000;
        premios[10] = 6000;
        premios[11] = 12000;
        premios[12] = 20000;
        premios[13] = 30000;
        premios[14] = 100000;
    }
    
    /**
     * Devolve o valor em euros do prémio correspondente ao nível.
     * @param nivel é o nível, vai de 1 a 15
     */
    public String getPremio(int nivel)
    {
        if(nivel >= 1 && nivel <= 15)
        {
            return premiosString(premios[nivel-1]);
        }
        return null; //caso o nivel seja fora do intervalo
    }
    
    /**
     * Escolhe este método se o concorrente errou na pergunta.
     * Devolve o correspondente prémio em euros consoante o nível.
     * @param nivel é o nível, vai de 1 a 15
     */
    public String getPremioEtapa(int nivel)
    {
        if(nivel >= 1 && nivel <= 15)
        {
            if(nivel <= 5) {
                return premiosString(0);
            } else if(nivel <= 10) {
                return premiosString(premios[4]);
            } else if(nivel <= 15) {
                return premiosString(premios[9]);
            }
        }
        return null; //caso o nivel seja fora do intervalo
    }
    
    private String premiosString(int euros)
    {
        String euros1 = "" + euros;
        String formatado = new String();
        int i=euros1.length(), j=i-3;
        for(; j > 0; i=i-3) {
            formatado = "." + euros1.substring(j,i) + formatado;
            j=j-3;
        }
        formatado = euros1.substring(0,i) + formatado + " €";
        return formatado;
    }
}
