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
    private final static String LINK = "https://dl.dropboxusercontent.com/u/100283103/qqsmilionario.jar";
    private final static String SHA1_LINK = "https://raw.githubusercontent.com/alexandre1985/QQSMilionario/master/updater/SHA1.txt";
    
    public static void main(String args[]) throws Exception
    {
        try {
            FileUtils.copyURLToFile(new URL(SHA1_LINK), new File("SHA1.txt"));
            if(!sameSha1()) {
                JOptionPane.showMessageDialog(null, "Existe uma nova versão do jogo.\n" + 
                "O download vai começar depois desta mensagem.\nAguarde um pouco", "Nova versão", JOptionPane.INFORMATION_MESSAGE);
                FileUtils.copyURLToFile(new URL(LINK), new File(PROG));
            }
        } catch (Exception e) {
            System.out.println("Internet está desligada");
        }
        //delete SHA1 file
        Files.deleteIfExists(Paths.get("SHA1.txt"));
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
    
    private static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
    
    public static void showSha1() throws Exception
    {
        System.out.println(sha1(PROG));
    }
    
    private static boolean sameSha1() throws Exception
    {
        Scanner s = new Scanner(new File("SHA1.txt"));
        String sha1Txt = s.useDelimiter("\\Z").next();
        
        String sha1File = sha1(PROG);
        
        s.close();
        
        return sha1File.equals(sha1Txt);
    }
}