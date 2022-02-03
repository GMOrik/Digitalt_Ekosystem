/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boi;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author 03erpeon
 */
public class Livlista implements Comparable{
    /**
     * En ArrayList som sparar alla individer i listan. Normalt är alla av samma art
     */
    public final List<? extends Liv> lista; //finns på vissa ställen omvandlingar till arrayList<Individ> som måste bort
    
    private final List<Liv> dödLista = new ArrayList<>(); //ej implementerad, borde gå generisk
    //eventuellt kan man skapa diagram med genomsnittlig ålder, dödsorsak, etc. (histogram/cirkeldiagram)
    
    /**
     * En ArrayList med storleken av populationen varje år i simuleringen. populationÖverTid.get(0) ger populationen år 0 etc.
     */
    private final List<Integer> populationÖverTid = new ArrayList<>();
    
    
    /**
     * Ett unikt nummer som används för att identifiera den aktuella biolistan. Alla individer i listan har samma ID som varandra och listan.
     */
    private final int ID;
    
    /**
     * Vilket ID-nummer nästa Biolista kommer att få. Bli alltid en större när en ny biolista skapas.
     */
    private static int staticID = 0;
    
    public final Mall mall; //fixa private/final? Isf ändra konstruktor
    public Mall hämtaMall() {return this.mall;}
    //namn?
    
    public Livlista(List<? extends Liv> lista, Mall mall){ //Fixa så att den skapas med listmall istället
        this.lista = lista;
        this.mall = mall; //notera att Sort både finns i listmall och biolista...
        this.ID = staticID;
        staticID++;
    }
    
    /**
     * Sparar listans aktuella populationsstorlek och totala mängd biomassa i populationÖverTid respektive biomassaÖverTid
     */
    public void sparaAktuellData(){
        if (mall.sort != Sort.PRODUCENT) populationÖverTid.add(lista.size());
        else{
            int totalBiomassa = 0;
            for (Liv l: lista){
                if (l.ärDöd()) continue;
                Producent v = (Producent)l;
                totalBiomassa += v.biomassa();
            }
            populationÖverTid.add(totalBiomassa);
        }
    }
    /**
     * Returnerar hur stor populationen var vid slutet av det angivna året
     * @param år
     * @return 
     */
    public int hämtaPopulation(int år){
        return populationÖverTid.get(år);
    }
    
    /**
     * Returnerar sort samt biomassa och population vid slutet av det angivna året. Mest för att skriva utdata till textarean.
     * @param år
     * @return 
     */
    public String skrivUtdata(int år){
        String s = mall.sort.toString() + ": " + hämtaPopulation(år) + "\n"; //ev. + biomassa eller något annat
        return s;
    }
    
    /**
     * Returnerar antalet individer i listan det år då det var som störst. Främst till för att anpassa y-axelns längd i ett diagram.
     * @return 
     */
    public int störstaPopulation(){
        return Collections.max(populationÖverTid);
    }
    
    /**
     * Returnerar hur många år simuleringen har kört. Främst till för att anpassa x-axelns längd i ett diagram
     * @return 
     */
    public int antalÅr(){
        return populationÖverTid.size();
    }
    
    public int hämtaID(){
        return this.ID;
    }
    
    public static int hämtaStaticID(){
        return staticID;
    }
    
    /**
     * Återställer staticID, d.v.s. gör så att nästa Biolista som skapas får ID = 0, efter den ID = 1 o.s.v. Bör endast köras när programmet återställs
     */
    public static void resetStaticID(){
        staticID = 0;
    }
    
    /**
     * Returnerar den biolista i Ekosystem.listaÖverListor som har den eftersökta siffran
     * @param listaÖverListor
     * @param id
     * @return 
     */
    public static Livlista getByID(int id, List<Livlista> listaÖverListor){
        for (Livlista i: listaÖverListor){
            if (i.hämtaID() == id){
                return i;
            }
        }
        return null;
    }
    
    
    public <T extends Liv> void dö(T a, String dödsorsak){ //kanske allt detta i Art istället
        if (!(lista.contains(a))) return;
        //a.die(dödsorsak);
        //ev. sats för att ta bort producenter ordentligty
        /*if (a instanceof Producent){
        standard.Ekosystem.abiotiskaFaktorer.taBortProducent((Producent) a);
        }*/
        lista.remove(a);
        dödLista.add(a);
        //annat om döden (t.ex. ålder vid död etc)
        //kan ev.sättas i dö i Art
    }
    
    //tillfällig sak
    public String dödsandel(){
        
        double predation = 0;
        double svält = 0;
        double ålder = 0;
        
        for (Liv a: dödLista){
            switch(a.dödsOrsak){
                case "Predation":
                    predation++;
                    break;
                case "Svält":
                    svält++;
                    break;
                case "Ålder":
                    ålder++;
                    break;
                case "N/A": throw new Error();
            }
        }
        double total = predation + svält + ålder;
        double predationsandel = (predation/total);
        double svältandel = (svält/total);
        double ålderandel = (ålder/total);
        
        String s = "Predation: " + predationsandel + "\nÅlder: " + ålderandel + "\nSvält: " + svältandel + "\n";
        s += predation + " " + ålder + " " + svält;
        return s;
        
    }
    
    static File latestDir = null;
    
    public void sparaStatistik(){
        JFileChooser filechooser = new JFileChooser();
        filechooser.setDialogTitle("Spara lista");
        if (latestDir != null) filechooser.setCurrentDirectory(latestDir.getParentFile());
        String namn = mall.namn;
        if (namn.equals("")) namn = mall.sort.toString();
        
        filechooser.setSelectedFile( new File(namn + ".txt")); //fungerar
        if (filechooser.showSaveDialog(new JPanel()) == JFileChooser.APPROVE_OPTION){
            File file = filechooser.getSelectedFile();
            latestDir = file;
            try {
                //save to file
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                out.write(mall.toString() + "\n|||||");
                out.write(dödsandel() + "\n|||||");
                for (int i = 0; i<populationÖverTid.size(); i++){
                    out.write(i + "," + populationÖverTid.get(i) + "\n");
                }
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Ekosystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    @Override
    public int compareTo(Object o) {
        Livlista l = (Livlista)o;
        if (this.mall.sort.compareTo(l.mall.sort) > 0) return 1;
        else if (this.mall.sort.compareTo(l.mall.sort) < 0) return -1;
        else return 0;
    }
    
}
