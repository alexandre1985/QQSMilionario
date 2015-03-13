import java.util.*;
import java.net.URL;
import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import java.nio.file.*;

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
        client = new FTPClient();
    }
    
    public String getPergunta()
    {
        return questoes.get().getPergunta(nivel);
    }
    
    public String[] getRespostas()
    {
        return questoes.get().getResposta();
    }
    
    /**
     * Devolve a resposta correcta. Vai de 1 a 4
     */
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
    
    public int getNivelEtapa()
    {
        if(nivel <= 5)
            return 0;
        else if(nivel <= 10)
            return 5;
        else if(nivel <= 15)
            return 10;
        return -1;
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
    
    public boolean eRecorde()
    {
        openFTPSession();
        //se não houver internet
        if(!client.isConnected())
            return false;
        downloadRecordes();
        Scanner s = null;
        try {
            s = new Scanner(new File(ficRecordes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String ultimo = null;
        int numLinhas=0;
        while(s.hasNext()) {
            ultimo = s.nextLine();
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
    
    private void openFTPSession()
    {
        try {
            client.connect(ftpURL);
            client.login("b7_15960406", "qqsmilionario");
            client.changeWorkingDirectory("htdocs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void closeFTPSession()
    {
        try {
            client.logout();
            client.disconnect();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void downloadRecordes()
    {
        try {
            FileOutputStream fos = new FileOutputStream(ficRecordes);
            client.retrieveFile(ficRecordes, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void uploadRecordes()
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
    
    /**
     * Este metodo guarda o ficheiro dos recordes no servidor.
     */
    public void guardarRecorde(int nivel, String nick)
    {
        final String recordesTMP = "recordes_tmp.txt";
        File recordes = new File(ficRecordes);
        if(!client.isConnected())
            openFTPSession();
        //se não há internet
        if(!client.isConnected())
            return;
        if(!recordes.exists()) { //se o fic não existe, faz o download do fic
            downloadRecordes();
        }
        Scanner s = null;
        PrintWriter writer = null;
        String linha = null;
        try {
            s = new Scanner(recordes);
            writer = new PrintWriter(recordesTMP, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int numLinha = 0;
        boolean jaEscrevi = false;
        while(s.hasNext()) {
            numLinha++;
            linha = s.nextLine();
            if(numLinha < 10) {
                String palavra[] = linha.split(" ",2);
                if(Integer.parseInt(palavra[0]) <= nivel && !jaEscrevi) {
                    writer.println(nivel + " " + nick);
                    jaEscrevi = true;
                }
                writer.println(linha);
            }
        }
        s.close();
        //se o ficheiro estiver vazio ou nao tenha escrito no fic
        if(!jaEscrevi) {
            writer.println(nivel + " " + nick);
        }
        writer.close();
        //rename o recordes_tmp.txt para recordes.txt
        try {
            Files.deleteIfExists(Paths.get(ficRecordes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        File tmp = new File(recordesTMP);
        tmp.renameTo(new File(ficRecordes));
        if(!client.isConnected())
            openFTPSession();
        uploadRecordes();
        closeFTPSession();
    }
    
    public void apagarRecordesServidor() throws Exception
    {
        openFTPSession();
        client.deleteFile(ficRecordes);
        closeFTPSession();
    }
    
    public String recordesString()
    {
        Premios premios = new Premios();
        File recordes = new File(ficRecordes);
        if(!recordes.exists()) { //se o fic não existe, faz o download do fic
            if(!client.isConnected())
                openFTPSession();
            downloadRecordes();
        }
        Scanner s = null;
        try {
            s = new Scanner(recordes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = "";
        String linha = null;
        for(int i = 1; s.hasNext(); i++) {
            linha = s.nextLine();
            String palavra[] = linha.split(" ",2);
            string += i+"º) "+palavra[1] +"  "+ premios.getPremio(Integer.parseInt(palavra[0]))+"\n";
        }
        s.close();
        try {
            Files.deleteIfExists(Paths.get(ficRecordes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }
}
