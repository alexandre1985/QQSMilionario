import java.util.*;
import java.net.URL;
import org.apache.commons.net.ftp.FTPClient;
import java.io.*;

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
    final String ftpURL = "ftp.byethost7.com";
    final String ficRecordes = "recordes.txt";
    FTPClient client;
    
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
    
    public boolean eRecorde(int nivel) throws Exception
    {
        openFTPSession();
        downloadRecordes();
        Scanner s = new Scanner(new File(ficRecordes));
        String ultimo = null;
        int numLinhas=0;
        while(s.hasNext()) {
            ultimo = s.next();
            numLinhas++;
        }
        s.close();
        if(numLinhas < 10)
            return true;
        String palavra[] = ultimo.split(" ",2);
        int ultimoNivel = Integer.parseInt(palavra[0]);
        if(nivel >= ultimoNivel)
            return true;
        return false;
    }
    
    public void openFTPSession()
    {
        try {
            client = new FTPClient();
            client.connect(ftpURL);
            client.login("b7_15960406", "qqsmilionario");
            client.changeWorkingDirectory("htdocs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void closeFTPSession()
    {
        try {
            client.logout();
            client.disconnect();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void downloadRecordes() throws Exception
    {
        FileOutputStream fos = new FileOutputStream(ficRecordes);
        client.retrieveFile(ficRecordes, fos);
        fos.close();
    }
    
    public void uploadRecordes()
    {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(ficRecordes);
            
            // Store file to server
            client.storeFile(ficRecordes, fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void guardarRecorde(int nivel, String nick) throws Exception
    {
        Map<Integer, String> map = new TreeMap<Integer, String>(Collections.reverseOrder());
        
        Scanner s = new Scanner(new File(ficRecordes));
        String linha = null;
        while(s.hasNext()) {
            linha = s.next();
            String palavra[] = linha.split(" ",2);
            map.put(Integer.parseInt(palavra[0]), palavra[1]);
        }
        map.put(new Integer(nivel), nick);
        s.close();
        int i=0;
        for(Map.Entry<Integer,String> entry : map.entrySet()) {
            if(i<10)
                System.out.println(entry.getKey() + " " + entry.getValue());
            i++;
        }
    }
    
    public void deleteRecordes() throws Exception
    {
        openFTPSession();
        client.deleteFile(ficRecordes);
        closeFTPSession();
    }
}
