import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.channels.*;

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
    final String ficRecordes = "recordes.txt";
    final String siteURL = "http://dacs.esy.es/";
    
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
        downloadRecordes();
        Scanner s = null;
        try {
            s = new Scanner(new File(ficRecordes), "UTF-8");
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
    
    public void downloadRecordes()
    {
        FileOutputStream fos = null;
        try {
            URL url = new URL(siteURL + ficRecordes);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            fos = new FileOutputStream(ficRecordes);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void uploadRecordes()
    {
        final String CrLf = "\r\n";
        URLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            URL url = new URL(siteURL + "upload.php");
            //System.out.println("url:" + url);
            conn = url.openConnection();
            conn.setDoOutput(true);

            String postData = "";

            InputStream imgIs = getClass().getResourceAsStream("/" + ficRecordes);
            byte[] imgData = new byte[imgIs.available()];
            imgIs.read(imgData);
            imgIs.close();
            
            String message1 = "";
            message1 += "-----------------------------4664151417711" + CrLf;
            message1 += "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" +
            ficRecordes + "\"" + CrLf;
            message1 += "Content-Type: text/txt" + CrLf;
            message1 += CrLf;

            // the image is sent between the messages in the multipart message.

            String message2 = "";
            message2 += CrLf + "-----------------------------4664151417711--"
                    + CrLf;

            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=---------------------------4664151417711");
            // might not need to specify the content-length when sending chunked
            // data.
            conn.setRequestProperty("Content-Length", String.valueOf((message1
                    .length() + message2.length() + imgData.length)));

            //System.out.println("open os");
            os = conn.getOutputStream();

            //System.out.println(message1);
            os.write(message1.getBytes());

            // SEND THE IMAGE
            int index = 0;
            int size = 1024;
            do {
                //System.out.println("write:" + index);
                if ((index + size) > imgData.length) {
                    size = imgData.length - index;
                }
                os.write(imgData, index, size);
                index += size;
            } while (index < imgData.length);
            //System.out.println("written:" + index);

            //System.out.println(message2);
            os.write(message2.getBytes());
            os.flush();

            //System.out.println("open is");
            is = conn.getInputStream();

            char buff = 512;
            int len;
            byte[] data = new byte[buff];
            do {
                //System.out.println("READ");
                len = is.read(data);

                if (len > 0) {
                    //System.out.println(new String(data, 0, len));
                }
            } while (len > 0);

            //System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.out.println("Close connection");
            try {
                os.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (Exception e) {
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

        if(!recordes.exists()) { //se o fic não existe, faz o download do fic
            downloadRecordes();
        }
        Scanner s = null;
        PrintWriter writer = null;
        String linha = null;
        try {
            s = new Scanner(recordes, "UTF-8");
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
        uploadRecordes();
    }
    
    public String recordesString()
    {
        Premios premios = new Premios();
        File recordes = new File(ficRecordes);
        if(!recordes.exists()) { //se o fic não existe, faz o download do fic
            downloadRecordes();
        }
        Scanner s = null;
        try {
            s = new Scanner(recordes, "UTF-8");
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
