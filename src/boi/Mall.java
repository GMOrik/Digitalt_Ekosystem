/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boi;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author 03erpeon
 */
public class Mall {
    Sort sort; //specialvariabel
    int[] heltalsvariabler;
    static String[] variabelnamn = {"Plats i näringskedjan","Antal" ,"Storlek", "Snabbhet", "Energi", "Avsökningsavstånd", 
    "Standardgraviditetstid","Reproduktionschans" ,"Standardhunger", "Könsmogen", "Maximiålder"};
    static String[] standardvärden = {"0","100", "1", "8", "1", "10", "5", "850","8", "10", "60"}; //vad som ska stå i rutan från början
    //ev. liknande lista för gränsvärden

    
    static String[] variabelnamnPlanta = {"Plats i näringskedjan","Antal", "Maximal biomassa", "Tillväxthastighet"};
    static String[] standardvärdenPlanta = {"-1","500", "23", "1500"};
    
    //ev. namn eller ID för mallarna, som sedan ärvs av livlistan. Blir också det namn man sparar filen som, och i menyn visaLiv() så står namnet + (sort.toString())
    //namnet måste isf också vara en del av skapaLista() och skapelseFönstret().
    public String namn;
    
    private Mall(Sort sort, int[] heltalsvariabler, String namn){
        this.sort = sort;
        this.heltalsvariabler = heltalsvariabler;
        this.namn = namn;
    }
    
    public static Mall skapaMall(Sort sort, int[] heltalsvariabler, String namn){
        if (sort == null) return null;
        if (sort == Sort.ROVDJUR && heltalsvariabler[0] < 1) return null;
        for (int i = 1; i<heltalsvariabler.length; i++){
            if (heltalsvariabler[i] < 0) return null;
        }
        
        return new Mall(sort, heltalsvariabler, namn);
    }
    
    public Livlista skapaLista(){
        int antal = heltalsvariabler[1];
        List<Konsument> lista = new ArrayList<>(); //dessa två listor kan egentligen definieras i switch-satsen
        List<Producent> listaVäxt = new ArrayList<>();
        switch(heltalsvariabler[0]){
            case -1: //växt
                for (int i = 0; i<antal; i++){
                    Producent p = Producent.skapaPlanta(heltalsvariabler[2], heltalsvariabler[3]);
                    if (p != null)listaVäxt.add(p);
                }
                Livlista returlista = new Livlista(listaVäxt, this);
                return returlista;
            default:
                for (int i = 0; i<antal; i++){
                //notera att raden nedan måste uppdateras varje gång en ny variabel tillkommer
                Konsument ind = new Konsument(heltalsvariabler[2], heltalsvariabler[3], heltalsvariabler[4], heltalsvariabler[0], heltalsvariabler[5],
                heltalsvariabler[6], heltalsvariabler[7], heltalsvariabler[8], heltalsvariabler[9], heltalsvariabler[10]);
                //test för nullvärde? behövs egentligen bara om man ger individ en privat konstruktor och en annan metod för att skapa objekt
                lista.add(ind);
                }
                Livlista returlista2 = new Livlista(lista, this);
                return returlista2;
        }
    }
    
    @Override
    public String toString(){
        String s = "";
        String[] aktuellavariabelnamn;
        if (sort == Sort.PRODUCENT) aktuellavariabelnamn = variabelnamnPlanta;
        else aktuellavariabelnamn = variabelnamn;
        s += namn + "\n";
        s += sort.toString() + "\n";
        for (int i = 0; i<aktuellavariabelnamn.length; i++){
            s += aktuellavariabelnamn[i] + ": " + heltalsvariabler[i] + "\n";
        }
        return s;
    }
    
    public static Mall importeraMall(String mall){
        //borde anpassa toString()-metoden kanske?
        String[] rader = mall.split("\n");
        String namn = rader[0];
        Sort sort = Sort.hämtaSort(rader[1]);
        
        int antalvariabler;
        if (sort == Sort.PRODUCENT) antalvariabler = variabelnamnPlanta.length;
        else antalvariabler = variabelnamn.length;
        if (rader.length != antalvariabler + 2) return null; //felaktigt format
        
        int[] heltalsvärden = new int[antalvariabler];
        for (int i = 2; i<rader.length; i++){
            String aktuelltvärde = rader[i].substring(rader[i].indexOf(":")+2); //första tecknet borde vara första siffran
            int värde = Integer.valueOf(aktuelltvärde); //try/catch?
            heltalsvärden[i-2] = värde;
        }
        Mall m = new Mall(sort, heltalsvärden, namn);
        return m;
    }
    
    //allt nedan är grafiska saker
    
    /**
     * Metod som används för att placera en komponent på en given plats i en JPanel med GridBagLayout (vanligtvis Skapelsefönstret).
     * @param p
     * @param c
     * @param gridx
     * @param gridy 
     */
    public static void placeraKomponent(JPanel p, Component c, int gridx, int gridy){
        //System.out.println(gridx + " " + gridy);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 20;
        gbc.weighty = 20;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.insets = new Insets(6,10,6,10); //top, left, bottom, right
        if (c instanceof JLabel){
            Font font = new Font("Helvetica", Font.PLAIN, 12);
            c.setFont(font);
        }
        p.add(c, gbc);
    }
    
    static JTextField[] txf = new JTextField[0];
    static JLabel[] lbl = new JLabel[0];
    
    public static void rensaDialogruta(JPanel p, JDialog d, Sort sort){
        for (int i = 0; i<txf.length; i++){
            p.remove(txf[i]);
            p.remove(lbl[i]);
        }
        switch (sort){
            case PRODUCENT:
                lbl = new JLabel[variabelnamnPlanta.length];
                txf = new JTextField[variabelnamnPlanta.length];
                        
                for (int i = 0; i<variabelnamnPlanta.length; i++){
                    lbl[i] = new JLabel(variabelnamnPlanta[i]);
                    txf[i] = new JTextField(standardvärdenPlanta[i]);
                    if (i == 0) txf[i].setEnabled(false);
                    placeraKomponent(p, lbl[i], 0, i+1);
                    placeraKomponent(p, txf[i], 1, i+1);
                }
                        
                p.revalidate();
                d.pack();
                break;
            case VÄXTÄTARE:
                int antalelement = variabelnamn.length;
                txf = new JTextField[antalelement];
                lbl = new JLabel[antalelement];
        
                for (int i = 0; i<antalelement; i++){
                    lbl[i] = new JLabel(variabelnamn[i]);
                    txf[i] = new JTextField(standardvärden[i]);
                    if (i == 0) txf[i].setEnabled(false);
                    placeraKomponent(p, lbl[i], 0, i+1); //måste stå i+1 på sista parametern om man har med en cbx
                    placeraKomponent(p, txf[i], 1, i+1);
                }
                p.revalidate();
                d.pack();
                break;
            case ROVDJUR:
                antalelement = variabelnamn.length;
                txf = new JTextField[antalelement];
                lbl = new JLabel[antalelement];
        
                for (int i = 0; i<antalelement; i++){
                    lbl[i] = new JLabel(variabelnamn[i]);
                    txf[i] = new JTextField(standardvärden[i]);
                    placeraKomponent(p, lbl[i], 0, i+1); //måste stå i+1 på sista parametern om man har med en cbx
                    placeraKomponent(p, txf[i], 1, i+1);
                }
                txf[0].setText("1");
                p.revalidate();
                d.pack();
                break;
        }
            
    }
    
    public static void skapelseFönstret(){
        JDialog d = new JDialog(new JFrame(), "Skapelsefönstret"); 
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        
        JLabel lblSort = new JLabel("Sort");
        placeraKomponent(p, lblSort, 0, 0);
        JComboBox cbxSort = new JComboBox(Sort.values());
        placeraKomponent(p, cbxSort, 1, 0);
        cbxSort.setSelectedIndex(1);
        
        rensaDialogruta(p, d, Sort.VÄXTÄTARE);
        
        JLabel lblNamn = new JLabel("Namn (Frivilligt)");
        placeraKomponent(p, lblNamn, 0, lbl.length+1);
        Font font = new Font("Helvetica", Font.ITALIC, 12);
        lblNamn.setFont(font);
        JTextField txfNamn = new JTextField();
        placeraKomponent(p, txfNamn, 1, lbl.length+1);
        
        JButton btnVerkställ = new JButton();
        btnVerkställ.setText("Verkställ");
        btnVerkställ.addActionListener(new java.awt.event.ActionListener() {
            //gör som i ändraListmall istället kanske?
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //Vad som händer
                Sort sort;
                
                int[] heltalsvariabler; //samma namn som instansvariablen (OBS!)
                int antalelement;
                sort = (Sort)cbxSort.getSelectedItem();
                if (sort == Sort.PRODUCENT) antalelement = variabelnamnPlanta.length;
                else antalelement = variabelnamn.length;
                heltalsvariabler = new int[antalelement];
                for (int i = 0; i<antalelement; i++){
                    try {
                        heltalsvariabler[i] = Integer.valueOf(txf[i].getText());
                    } catch (NumberFormatException numberFormatException) {
                        JOptionPane.showMessageDialog(null, "\"" + txf[i].getText() + "\" är en inmatning av fel datatyp. Vänligen försök igen", "Felaktig inmatning", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                String namn = txfNamn.getText();
                Mall l = Mall.skapaMall(sort, heltalsvariabler, namn); //sista är plats
                if (l == null){
                    JOptionPane.showMessageDialog(null, "Vänligen ange positiva heltal i samtliga rutor", "Fel", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //OBS!!!
                boi.Ekosystem.aktivaListmallar.add(l);
                
                //Stänger fönstret
                d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
            }
        });
        
        cbxSort.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt){
                Sort sort = (Sort)cbxSort.getSelectedItem();
                //rensa alla gamla
                for (int i = 0; i<txf.length; i++){
                    p.remove(txf[i]);
                    p.remove(lbl[i]);
                }
                rensaDialogruta(p, d, sort);
                
            }
        });
        
        placeraKomponent(p, btnVerkställ, 3, lbl.length+1);
         
        d.add(p);
        d.pack();
        //d.setSize(500, (etiketter.length+1)*50);
        d.setModal(true);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    
    Mall gammalListmall = null;
    
    //kanske return boolean för att se om någon ändring skedde
    //den här metoden behöver fixas för att allt med växter ska vara rätt
    private void ändraMall(){
        gammalListmall = this;
        JDialog d = new JDialog(new JFrame(), "Ändra i listmall"); 
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        
        JLabel lblSort = new JLabel("Sort");
        placeraKomponent(p, lblSort, 0, 0);
        JComboBox cbxSort = new JComboBox(Sort.values());
        placeraKomponent(p, cbxSort, 1, 0);
        cbxSort.setSelectedIndex(sort.värde()-1); //-1 eftersom sort använder 1, 2, 3 istället för -1, 0, 1
        cbxSort.setEnabled(false);
        //platsINäringskedjan kanske inte heller ska kunna ändras
        
        rensaDialogruta(p, d, sort);
        for (int i = 0; i<txf.length; i++){
            txf[i].setText(Integer.toString(heltalsvariabler[i]));
        }
        
        JLabel lblNamn = new JLabel("Namn (Frivilligt)");
        placeraKomponent(p, lblNamn, 0, lbl.length+1);
        Font font = new Font("Helvetica", Font.ITALIC, 12);
        lblNamn.setFont(font);
        JTextField txfNamn = new JTextField(namn);
        placeraKomponent(p, txfNamn, 1, lbl.length+1);
        
        JButton btnSparaÄndringar = new JButton("Spara ändringar");
        btnSparaÄndringar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //try?
                Mall l;
                int[] heltalsvariabler = new int[txf.length];
                for (int i = 0; i<txf.length; i++){
                    try {
                        heltalsvariabler[i] = Integer.valueOf(txf[i].getText());
                    } catch (NumberFormatException numberFormatException) {
                        JOptionPane.showMessageDialog(null, "\"" + txf[i].getText() + "\" är en inmatning av fel datatyp. Vänligen försök igen", "Felaktig inmatning", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                String namn = txfNamn.getText();
                
                l = skapaMall(sort, heltalsvariabler, namn);
                if (l == null){
                    JOptionPane.showMessageDialog(null, "Vänligen ange positiva heltal i samtliga rutor", "Fel", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                
                //this hänvisar här till den annonyma inre klassen, ej listan!!!
                //därav gammalListmall
                
                //OBS!!!
                //insert?
                int i = Ekosystem.aktivaListmallar.indexOf(gammalListmall);
                Ekosystem.aktivaListmallar.set(i, l);
                //boi.Ekosystem.aktivaListmallar.remove(gammalListmall);
                //boi.Ekosystem.aktivaListmallar.add(l);
                
                d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
            }
        });
        
        
        placeraKomponent(p, btnSparaÄndringar, 3, txf.length + 1);
        
        d.add(p);
        d.pack();
        d.setModal(true);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    
    //font och dyl. på labels?
    
    static File latestDir = null;
    
    public static void visaListor(List<Mall> mallar){
        if (mallar.isEmpty()){
            JOptionPane.showMessageDialog(null, "Det finns för närvarande inget liv i ekosystemet", "Vad trodde du?", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JDialog d = new JDialog(new JFrame(), "Ändra i listmall"); 
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        
        for(int i = 0; i<mallar.size(); i++){
            Mall l = mallar.get(i);
            JLabel lbl = new JLabel(l.namn + " (" + l.sort.toString() + ")");
            
            JButton btnInfo = new JButton("Info");
            btnInfo.addActionListener(new java.awt.event.ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                   String s = l.toString();
                   JOptionPane.showMessageDialog(null, s, "Information om lista", JOptionPane.INFORMATION_MESSAGE);
                } 
            });
            
            JButton btnÄndra = new JButton("Ändra");
            btnÄndra.addActionListener(new java.awt.event.ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    l.ändraMall();
                    d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
                }
            });
            
            JButton btnTaBort = new JButton("Ta bort");
            btnTaBort.addActionListener(new java.awt.event.ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    Object[] options = {"Ja","Nej"};
                    int val = JOptionPane.showOptionDialog(null, "Är du säker på att du vill radera listan? Den går inte att återställa.", "Radera lista", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, "Nej");
                    if (val == JOptionPane.YES_OPTION){
                        mallar.remove(l);
                        
                        //eventuellt så att d bara uppdateras, dock jobbigt
                        d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
                        //behöver fixa knapparna när den sista listan tas bort
                    }
                    if (val == JOptionPane.NO_OPTION){
                        
                    }
                    if (val == JOptionPane.CLOSED_OPTION){ //CLOSED_OPTION vs CANCELED_OPTION?
                        
                    }
                    
                }
            });
            
            JButton btnSpara = new JButton("Spara");
            btnSpara.addActionListener(new java.awt.event.ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    JFileChooser filechooser = new JFileChooser();
                    filechooser.setDialogTitle("Spara lista");
                    if (latestDir != null) filechooser.setCurrentDirectory(latestDir.getParentFile());
                    filechooser.setSelectedFile( new File(l.namn + ".txt")); //fungerar
                    if (filechooser.showSaveDialog(p) == JFileChooser.APPROVE_OPTION){
                        File file = filechooser.getSelectedFile();
                        latestDir = file;
                        try {
                            //save to file
                            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                            out.write(l.toString());
                            out.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Ekosystem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            
            placeraKomponent(p, lbl, 0, i);
            placeraKomponent(p, btnInfo, 1, i);
            placeraKomponent(p, btnÄndra, 2, i);
            placeraKomponent(p, btnTaBort, 3, i);
            placeraKomponent(p, btnSpara, 4, i);
        }
        d.add(p);
        d.pack();
        d.setModal(true);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    
}
