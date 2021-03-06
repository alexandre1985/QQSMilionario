import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
//import java.awt.event.*;
//import javax.swing.event.*;
import java.util.Random;
import java.util.concurrent.*;
import java.net.URI;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;
import org.jfree.util.PublicCloneable;

/**
 * Esta é a GUI do jogo Quem Quer Ser Milionário.
 * 
 * @author Daniel Santos
 * @version 1.0
 */
public class GUI extends JFrame
{
    private static final String VERSAO = "1.0";
    //Altura e largura da GUI Frame
    private static final int HEIGHT=200;
    private static final int WIDTH=520;
    OJogo dados;
    Premios premios;
    Random rand;
    
    JLabel pergunta;
    JButton[] botão;
    JButton aj5050;
    JButton ajTelefone;
    JButton ajPublico;
    JButton botãoSair;
    JButton botãoSobre;
    Quadro quadro;
    ChartFrame graphFrame;
    
    
    public static void main(String[] args)
    {
        GUI gui = new GUI();
    }
    
    public GUI()
    {
        super("Quem quer ser Milionário?");
        rand = new Random();
        dados = new OJogo();
        premios = new Premios();
        botão = new JButton[4];
        quadro = new Quadro();
        makeFrame();
    }
    
    private void makeFrame()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel contentPane = (JPanel) getContentPane();
        //contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        contentPane.setLayout(new BorderLayout(10,10));
        
        JPanel painelPergunta = new JPanel();
        {
            painelPergunta.setLayout(new FlowLayout());
            pergunta = new JLabel(linhas(dados.getPergunta()));
            painelPergunta.add(pergunta);
        }
        contentPane.add(painelPergunta, BorderLayout.CENTER);
        
        JPanel botões = new JPanel();
        {
            botões.setLayout(new GridLayout(2,2));
            String[] respostas = dados.getRespostas();
            String[] letra = new String[]{"A: ","B: ", "C: ", "D: "};
            //int[] atalho = new int[]{KeyEvent.VK_A,KeyEvent.VK_B,KeyEvent.VK_C,KeyEvent.VK_D};
            for(int i=0; i < 4; i++) {
                botão[i] = new JButton(letra[i] + respostas[i]);
                final int j = i;
                botão[i].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        verificaResposta(j+1);
                        if( graphFrame != null) {
                            graphFrame.setVisible(false);
                            graphFrame.dispose();
                        }
                    }
                });
                botão[i].setHorizontalAlignment(SwingConstants.LEFT);
                botões.add(botão[i]);
            }
        }
        contentPane.add(botões, BorderLayout.SOUTH);
        
        JPanel opcoesPanel = new JPanel();
        {
            opcoesPanel.setLayout(new BorderLayout());
            JPanel botõesEsquerda = new JPanel();
            {
                aj5050 = new JButton("50:50");
                aj5050.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int[] respostasErradas = dados.ajuda5050();
                        botão[respostasErradas[0]-1].setEnabled(false);
                        botão[respostasErradas[1]-1].setEnabled(false);
                        aj5050.setEnabled(false);
                    }
                });
                botõesEsquerda.add(aj5050);
                
                ajTelefone = new JButton("Telefone");
                ajTelefone.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ajudaTelefone();
                        ajTelefone.setEnabled(false);
                    }
                });
                botõesEsquerda.add(ajTelefone);
                
                ajPublico = new JButton("Público");
                ajPublico.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ajudaPublico();
                        ajPublico.setEnabled(false);
                    }
                });
                botõesEsquerda.add(ajPublico);
            }
            opcoesPanel.add(botõesEsquerda, BorderLayout.WEST);
            
            JPanel botõesDireita = new JPanel();
            {
                botãoSair = new JButton("Desistir");
                botãoSair.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        desisto();
                    }
                });
                botõesDireita.add(botãoSair);
                
                botãoSobre = new JButton("Sobre");
                botãoSobre.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        sobre();
                    }
                });
                botõesDireita.add(botãoSobre);
            }
                
            opcoesPanel.add(botõesDireita, BorderLayout.EAST);
        }
        contentPane.add(opcoesPanel, BorderLayout.NORTH);
        
        setSize(WIDTH,HEIGHT);
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width/2 - getWidth()/2, d.height/2 - getHeight()/2);
        setVisible(true);
    }
    
    private void verificaResposta(int resposta)
    {
        if(resposta == dados.getRespostaCorrecta()) {
            passaDeNivel();
        } else {
            respostaErrada();
        }
    }
    
    private void respostaErrada()
    {
        efeitoRespostaErrada();
        int nivel = dados.getNivelEtapa();
        String euros = premios.getPremio(nivel);
        String mensagem;
        if(euros.equals("0 €")) {
            mensagem = "Não ganhaste dinheiro.";
        }
        else {
            mensagem = "Ganhaste " + euros;
        }
        JOptionPane.showMessageDialog(this,
                        "A resposta está errada...\n" + mensagem, "Resposta Errada",
                        JOptionPane.PLAIN_MESSAGE);
        if(nivel != 0) {
            if(dados.eRecorde()) {
                dialogGravarRecorde(nivel);
            }
        }
        dialogMostrarRecordes();
        System.exit(0);
    }
    
    private void passaDeNivel()
    {
        if(dados.acabou()) {
            JOptionPane.showMessageDialog(this,
                "Acabaste o jogo! Muitos Parabéns!\nGanhaste " +
                premios.getPremio(dados.getNivel()) + ".", "Parabéns",
                JOptionPane.PLAIN_MESSAGE);
            dialogGravarRecorde(dados.getNivel());
            dialogMostrarRecordes();
            System.exit(0);
        }
        
        if(dados.eNivelPatamar()) {
            JOptionPane.showMessageDialog(this,
            "Atingiste um novo patamar!\n" + premios.getPremio(dados.getNivel()) + " já são teus.",
            "Informação", JOptionPane.INFORMATION_MESSAGE);
        }
        
        dados.incrementaNivel();
        setVisible(false);
        mudaPergunta();
        mudaBotões();
        for(int i=0; i < 4;i++) {
            botão[i].setEnabled(true);
        }
        //pack();
        quadro.mudaNivel(dados.getNivel());
        setVisible(true);
        //requestFocusInWindow(); //não funciona não sei porquê
    }
        
    private void mudaPergunta()
    {
        pergunta.setText(linhas(dados.getPergunta()));
    }
    
    private void mudaBotões()
    {
        String[] respostas = dados.getRespostas();
        String[] letra = new String[]{"A: ","B: ", "C: ", "D: "};
        for(int i=0; i < 4; i++) {
            botão[i].setText(letra[i] + respostas[i]);
        }
    }
    
    private void desisto()
    {
        if(dados.getNivel() != 1) {
            JOptionPane.showMessageDialog(this,
                        "Parabéns!\nGanhaste " + premios.getPremio(dados.getNivel()-1) + ".",
                        "Sair",
                        JOptionPane.PLAIN_MESSAGE);
            if(dados.eRecorde()) {
                dialogGravarRecorde(dados.getNivel()-1);
            }
            dialogMostrarRecordes();
        }
        System.exit(0);
    }
    
    private void sobre()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel ,BoxLayout.PAGE_AXIS));
        JLabel texto = new JLabel("<html>Quem Quer Ser Milionário, versão " + VERSAO +
        "<br>Autor:<br></html>");
        panel.add(texto);
        panel.add(autorLabel("Daniel Santos", "dannysantos1985@gmail.com"));
        JOptionPane.showMessageDialog(this, panel , "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JLabel autorLabel(String nome, final String email)
    {
        JLabel label = new JLabel("<html>" + nome + " &lt<a href=\"\">" + email + "</a>&gt</html>");
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + email + "?subject=QQSMilionario"));
                } catch (Exception ex) {
                    //It looks like there's a problem
                }
            }
        });
        return label;
    }
    
    public static int getAltura()
    {
        return HEIGHT;
    }
    
    public static int getLargura()
    {
        return WIDTH;
    }
    
    private String linhas(String linha)
    {
        final int MAXLINHA = 65;
        String formatada = new String();
        int tamanho = linha.length();
        int i=0, j=MAXLINHA;
        while(j < tamanho) {
            if(linha.substring(i,j).contains("\n")) {
                j = i + linha.substring(i,j).indexOf("\n");
            }
            else {
                while(linha.charAt(j) != ' ') {
                    j--;
                }
            }
            formatada = formatada + linha.substring(i,j) + "<br>";
            j++;
            i = j;
            j = j + MAXLINHA;
        }

        if(i==0) {
            while(linha.substring(i,tamanho).contains("\n")) {
                j = i + linha.substring(i,tamanho).indexOf("\n");
                formatada = formatada + linha.substring(i,j) + "<br>";
                i = j+1;
            }
        }
        formatada = formatada + linha.substring(i,tamanho);
        return "<html>" + formatada + "</html>";
    }
    
    private void ajudaTelefone()
    {
        int a = rand.nextInt(3)+1;
        if (a != 2) { a = 1; }
        final int PROB_ACERTAR = 80 - (dados.getNivel()*2*a);
        int prob = rand.nextInt(100) + 1;
        String mensagem;
        if(prob < PROB_ACERTAR) {
            mensagem = "De certeza que é: '" + dados.getRespostas()[dados.getRespostaCorrecta()-1]
            + "'";
        }
        else {
            mensagem = "Não sei...";
        }
        JOptionPane.showMessageDialog(this, mensagem, "Telefone", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void ajudaPublico()
    {
        int votos[] = new int[4];
        respostasPublico(votos);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(votos[0], "Percentagem", "A");
        dataset.addValue(votos[1], "Percentagem", "B");
        dataset.addValue(votos[2], "Percentagem", "C");
        dataset.addValue(votos[3], "Percentagem", "D");
        JFreeChart graf = ChartFactory.createBarChart("","","",dataset,PlotOrientation.VERTICAL,
        false, true, false);
        
        graphFrame = new ChartFrame("Votos Público", graf);

        //mudar para azul a cor das barras
        CategoryPlot plot = (CategoryPlot) graf.getPlot();
        BarRenderer barRenderer = (BarRenderer)plot.getRenderer();
        barRenderer.setSeriesPaint(0, Color.BLUE);
        
        graphFrame.setSize(200,200);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        graphFrame.setLocation(d.width/2 + GUI.getLargura()/2 - graphFrame.getWidth(),
        d.height/2 - GUI.getAltura()/2 - graphFrame.getHeight());
        graphFrame.setVisible(true);
    }
    
    private void respostasPublico(int[] votos)
    {
        final int PESSOAS_PLATEIA = 120;
        int PROB_ACERTAR = 50 - (dados.getNivel()*2);
        if(rand.nextInt(4) == 0) {
            //às vezes o público esta bem informado
            PROB_ACERTAR = 50 + rand.nextInt(10);
        } else if(rand.nextInt(4) == 0) {
            PROB_ACERTAR = 21 + rand.nextInt(10);
        } else if(dados.getNivel() > 11 && rand.nextInt(3) < 2){
            PROB_ACERTAR = 25 + rand.nextInt(21);
        }
        for(int i=0; i < PESSOAS_PLATEIA; i++) {
            int prob = rand.nextInt(100);
            if(prob < PROB_ACERTAR) {
                votos[dados.getRespostaCorrecta()-1]++;
            }
            else {
                votos[umaRespostaErrada()]++;
            }
        }
        for(int i=0; i < 4; i++) {
            votos[i] = (votos[i]*100)/PESSOAS_PLATEIA;
        }
    }
    
    /**
     * Devolve um numero de uma resposta errada (vai de 0 a 3)
     */
    private int umaRespostaErrada()
    {
        int a;
        do {
            a = rand.nextInt(4);
        } while(a == dados.getRespostaCorrecta()-1);
        return a;
    }
    
    private void dialogMostrarRecordes()
    {
        String pontuacoes = dados.recordesString();
        //se tem internet
        if(!pontuacoes.equals(""))
            JOptionPane.showMessageDialog(this, dados.recordesString(), "Recordes",
                    JOptionPane.PLAIN_MESSAGE);
    }
    
    private void dialogGravarRecorde(int nivel)
    {
        String name;
        do {
            name = JOptionPane.showInputDialog("Bateste um recorde!\nEscreve o teu nick/nome " +
        "para ir para a tabela de recordes: ");
        } while(name != null && name.equals(""));
        
        if(name != null) {
            dialogUploadWaiting(nivel, name);
        }
    }
    
    private void dialogUploadWaiting(final int nivel, final String name)
    {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JTextArea msgLabel;
        final JDialog dialogDeEspera;
        JPanel panel;

        msgLabel = new JTextArea("A gravar o recorde no servidor...");
        msgLabel.setEditable(false);


        panel = new JPanel(new BorderLayout(5, 5));
        panel.add(msgLabel, BorderLayout.PAGE_START);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));

        dialogDeEspera = new JDialog(Frame.getFrames()[0], "A fazer o upload", true);
        dialogDeEspera.getContentPane().add(panel);
        dialogDeEspera.setResizable(false);
        dialogDeEspera.pack();
        dialogDeEspera.setSize(300, dialogDeEspera.getHeight());
        dialogDeEspera.setLocationRelativeTo(null);
        dialogDeEspera.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialogDeEspera.setAlwaysOnTop(true);     
        msgLabel.setBackground(panel.getBackground());
        
        SwingWorker worker = new SwingWorker() {

            @Override
            protected void done() {
                // Close the dialog
                dialogDeEspera.dispose();
            }

            @Override
            protected Void doInBackground() {
                // Do the long running task here
                // Call "publish()" to pass the data to "process()"
                // return something meaningful
                dados.guardarRecorde(nivel, name);
                return null;
            }
        };

        worker.execute();
        dialogDeEspera.setVisible(true);
    }
    
    private void efeitoRespostaErrada()
    {
        final int resposta = dados.getRespostaCorrecta();
        botão[resposta-1].setOpaque(true);
        botão[resposta-1].setBackground(Color.YELLOW);
        Timer blinkTimer = new Timer(500, new ActionListener() {
            private int count = 0;
            private int maxCount = 8;
            private boolean on = false;
        
            public void actionPerformed(ActionEvent e) {
                if (count >= maxCount) {
                    ((Timer) e.getSource()).stop();
                } else {
                    botão[resposta-1].setBackground( on ? Color.YELLOW : null);
                    on = !on;
                    count++;
                }
            }
        });
        blinkTimer.start();
    }
}
