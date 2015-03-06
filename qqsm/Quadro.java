import javax.swing.*;
import java.awt.*;

/**
 * Esta classe cria e mexe com quadro das etapas do jogo
 * 
 * @author Daniel Santos
 * @version 0.1
 */
public final class Quadro extends JFrame
{
    JLabel etapa[];
    Premios premios;
    
    /**
     * Cria o quadro das etapas.
     * 
     */
    public Quadro()
    {
        super("Etapas");
        premios = new Premios();
        etapa = new JLabel[15];
        makeFrame();
        mudaNivel(1);
    }
    
    private void makeFrame()
    {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        JPanel contentPane = (JPanel) getContentPane(); 

        JPanel grid = new JPanel(new GridLayout(0,1));
        for(int i=15; i > 0; i--) {
            etapa[i-1] = new JLabel(premios.getPremio(i));
            if(i%5 != 0) {
                etapa[i-1].setFont(etapa[i-1].getFont().deriveFont(Font.PLAIN));
            }
            etapa[i-1].setHorizontalAlignment(JLabel.CENTER);
            etapa[i-1].setOpaque(true);
            //etapa[i-1].setBackground(new Color(140,171,226));
            grid.add(etapa[i-1]);
        }
        contentPane.add(grid);
        
        pack();
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width/2 + GUI.getLargura()/2, d.height/2 + GUI.getAltura()/2 - getHeight());
        setVisible(true);
    }
    
    /**
     * Põe a negrito a etapa correspondente ao nível
     * @param nivel Nivel da etapa (vai de 1 a 15)
     */

    public void mudaNivel(int nivel)
    {
        if(nivel >= 1 && nivel <= 15) {
            setVisible(true);
            if (nivel != 1) {
                //põe a cor normal no nivel anterior
                Color corNormal = etapa[nivel-1].getBackground();
                etapa[nivel-2].setBackground(corNormal);
            }
            etapa[nivel-1].setBackground(Color.YELLOW);
        }
    }
}
