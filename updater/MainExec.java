import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import java.nio.file.*;
import javax.swing.JOptionPane;

/**
 * Classe principal do updater.
 * 
 * @author Daniel Santos
 * @version 0.2
 */
public class MainExec
{
    private final static String PROG = "qqsm.jar";
    private final static String LINK = "https://dl.dropboxusercontent.com/u/100283103/qqsmilionario/qqsm.jar";
    private final static String SHA1_LINK = "https://dl.dropboxusercontent.com/u/100283103/qqsmilionario/SHA1.txt";
    
    public static void main(String args[]) throws Exception
    {
        try {
            //read sha1 file from url
            URL url = new URL(SHA1_LINK);
            Scanner s = new Scanner(url.openStream());
            String sha1Url = s.next();
            s.close();
            File file = new File(PROG);
            if(!file.exists() || !sameSha1(sha1Url)) {
                JOptionPane.showMessageDialog(null, "Existe uma nova versão do jogo.\n" + 
                "O download vai começar depois desta mensagem.\nAguarde um pouco",
                "Nova versão", JOptionPane.INFORMATION_MESSAGE);
                FileUtils.copyURLToFile(new URL(LINK), new File(PROG));
            }
        } catch (Exception e) {
            System.out.println("Internet está desligada");
        }
        runCommand();
    }
    
    private static void runCommand()
    {
        try {
            // using the Runtime exec method:
            Runtime.getRuntime().exec("java -jar " + PROG);
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private static String sha1(String file) throws NoSuchAlgorithmException, IOException
    {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(file);
  
        byte[] data = new byte[1024];
        int read = 0; 
        while ((read = fis.read(data)) != -1) {
            sha1.update(data, 0, read);
        };
        byte[] hashBytes = sha1.digest();
  
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
          sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        fis.close();
        return sb.toString();
    }
    
    public static void createFileSha1() throws Exception
    {
        String directory = System.getProperty("user.home")+File.separatorChar+"Dropbox"+
        File.separatorChar+"Public"+File.separatorChar+"qqsmilionario"+File.separatorChar;
        FileUtils.writeStringToFile(new File(directory + "SHA1.txt"), sha1(directory + "qqsm.jar"));
    }
    
    private static boolean sameSha1(String sha1Url) throws Exception
    {
        String sha1File = sha1(PROG);
        return sha1File.equals(sha1Url);
    }
}