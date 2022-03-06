/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boi;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 03erpeon
 */
public class Konsument extends Liv{
    private int storlek;
    
    //variabler för överlevnad/rörelsebegränsningar
    private int energi;
    private int hunger = 3; 
    private int standardhunger; 
    
    private int graviditetstid = 0;
    private int standardgraviditetstid; //hur länge individen är gravid när den blir det
    private int reproduktionschans; //i promille, tal mellan 0 och 1000 för att undvika behov av doublevariabler
    
    private int ålder = 0;
    public int ålder(){return this.ålder;}
    private int könsmogen; //efter denna ålder kan individen skaffa barn
    private boolean könsmogen(){return (ålder>=könsmogen);}
    private int maximiålder;
    
    boolean hane = false;
    boolean ärHane(){return this.hane;}
    
    
    private int snabbhet; 
    private double riktning; //moturs
    
    private final int avsökningsavstånd;
    
    
    /**
     * 
     * @param storlek
     * @param snabbhet
     * @param energi
     * @param platsINäringskedjan 
     * @param avsökningsavstånd 
     * @param standardgraviditetstid 
     * @param reproduktionschans
     * @param standardhunger 
     * @param könsmogen 
     * @param maximiålder 
     */
    public Konsument(int storlek, int snabbhet, int energi, int platsINäringskedjan, int avsökningsavstånd, int standardgraviditetstid,
            int reproduktionschans, int standardhunger, int könsmogen, int maximiålder){
        super(platsINäringskedjan);
        this.storlek = storlek;
        this.snabbhet = snabbhet;
        this.energi = energi;
        this.avsökningsavstånd = avsökningsavstånd;
        this.standardgraviditetstid = standardgraviditetstid;
        this.standardhunger = standardhunger;
        this.hunger = (int)(standardhunger*Math.random() + 0.5);
        this.reproduktionschans = reproduktionschans;
        this.könsmogen = könsmogen;
        this.maximiålder = maximiålder;
        this.ålder = (int)(maximiålder * Math.random() + 0.5);
        spawn();
    }
    
    @Override
    protected void spawn(){
        this.posX = (int)(Ekosystem.sidaX()*Math.random());
        this.posY = (int)(Ekosystem.sidaY()*Math.random());
        this.hane = (Math.random() < 0.5);
        
        double vinkel = ((360*Math.random()));
        if (vinkel == 360) vinkel = 0;
        this.riktning = vinkel;
    }
    
    public synchronized void flytta(List<Liv> totalLista){
        int ursprungligsnabbhet = this.snabbhet;
        int originalenergi = energi;
        ändraVinkel();
        
        while(energi>0){
            double vinkelIRad = 2*Math.PI*riktning/360;
            int hoppX = Math.abs((int)Math.round((snabbhet*Math.cos(vinkelIRad)))); 
            int hoppY = Math.abs((int)Math.round((snabbhet*Math.sin(vinkelIRad))));
            
            if (riktning <90){
            }
            else if (riktning < 180){
                hoppX = -hoppX;
            }
            else if (riktning < 270){
                hoppX = -hoppX;
                hoppY = -hoppY;
            }
            else if (riktning < 360){
                hoppY = -hoppY;
            }
            
            if (hoppX > 0){
                if (posX + hoppX < Ekosystem.sidaX()) posX += hoppX;
                else posX = Ekosystem.sidaX();
            }
            else{
                if (posX + hoppX > 0) posX += hoppX;
                else posX = 0;
            }
            if (hoppY < 0){
                if (posY + hoppY > 0) posY += hoppY;
                else posY = 0;
            }
            else{
                if (posY + hoppY < Ekosystem.sidaY()) posY += hoppY;
                else posY = Ekosystem.sidaY();
            }
            
            this.interagera(totalLista);
            this.snabbhet = ursprungligsnabbhet;
            energi--;
        }
        energi = originalenergi;
        
        hunger--;
        if (hunger <= 0){
            this.dö("Svält");
        }
        
        if (graviditetstid > 0 && !hane && könsmogen()){
            graviditetstid--;
            if (graviditetstid == 0){
                //föröka
                if (1000*Math.random() < reproduktionschans) { 
                    Konsument avkomma = new Konsument(storlek, snabbhet, energi, platsINäringskedjan, avsökningsavstånd, 
                    standardgraviditetstid, reproduktionschans, standardhunger, könsmogen, maximiålder);
                    avkomma.posX = posX;
                    avkomma.posY = posY; 
                    //ev. något om generationstal
                    avkomma.geSammaIDSom(this);
                    List<Konsument> l = (List<Konsument>) this.hämtaLista().lista;
                    avkomma.hunger = (int)(standardhunger); //för att undvika att de ät på ett ställe och bara kör sin grej. könsmogen kan dock stoppa lite
                    avkomma.ålder = 0;
                    l.add(avkomma);
                }
            }
        }
        
        ålder++;
        if (ålder > maximiålder && Math.random()<(double)((ålder-maximiålder)/5.0)) this.dö("Ålder");
        //ganska enkel modell, men så här fungerar den. För varje år över maximiåldern ökar dödschansen med 20%
        //om maximiålder = 10 & ålder = 11 är dödschansen 20%
        //vid ålder = 12 är dödschansen 40% och den totala chansen att individen har dött av ålder (något med båda sannolikheter)
    }
    
    public void interagera (List<Liv> lista){
        OUTER:
        for (Liv a: lista){
            if (a.ärDöd()) continue;
            
            int sökavstånd;
            if (a instanceof Producent) sökavstånd = storlek; //plantor har storleken 0
            else{
                Konsument o = (Konsument)a;
                sökavstånd = storlek + o.storlek;
            }
            
            double avstånd = Math.sqrt(Math.pow(a.posX-posX, 2) + Math.pow(a.posY-posY, 2));
            if (avstånd < sökavstånd){
                int relation = this.compareTo(a);
                switch (relation) {
                    case 1:
                        
                        if (a instanceof Producent){
                            Producent p = (Producent)a;
                            if (platsINäringskedjan == 0){
                                //jag är växtätare och har träffat på en växt
                                while(p.värdAttÄtas() && hunger <= standardhunger){
                                    p.bliÄten();
                                    hunger++;
                                }
                                break OUTER;
                            }
                            break;
                        }
                        //chans att överleva? växtätare starka nog att slå tillbaka?
                        
                        //borde varit if(hunger < standardhunger) här, men lite sent att ändra kanske
                        //borde dessutom vara hunger = standardhunger, inte +=
                        
                        //if (hunger < standardhunger){
                        a.dö("Predation");
                        this.hunger += standardhunger;
                        break OUTER;
                        //}
                        
                        //break;
                    case -1:
                        //this.dö("Predation");
                        //satsen ovan avgör om gröna kan dö av att hoppa in i röda eller ej
                        //utfördInteraktion = true;
                        break;
                    case 0:
                        Konsument i = (Konsument)a;
                        if (i.hämtaListID() != hämtaListID()) break;
                        if (hane == i.hane) break; //medför även att man inte kan para sig med sig själv
                        if (!könsmogen() || !(i.könsmogen())) break;
                        if (graviditetstid > 0 || i.graviditetstid > 0) break;
                        if (hunger<(int)(standardhunger*0.3 + 0.5)) break; //hungervillkoret är viktigt, annars sker förökning på ren slump
                        
                        if (!hane){
                            graviditetstid = standardgraviditetstid + 1; //+1 kompenserar för -1 som kommer direkt i move()
                        }
                        else{
                            i.graviditetstid = standardgraviditetstid + 1;
                        }
                        
                        break OUTER;
                    default:
                        break;
                }
            }
        }
    }
    
    private synchronized void ändraVinkel(){
        
        //1. Slumpmässigt riktningsbyte för att de inte ska vara ping-pong-bollar
        riktning = (360*Math.random());
        
        //2. Trycker bort individen från väggarna
        if (posX == Ekosystem.sidaX()){ //höger vägg
        riktning = (90 + 180*Math.random());
        }
        if (posX == 0){ //vänster vägg
        riktning = (270 + 180*Math.random());
        }
        if (posY == Ekosystem.sidaY()){ //golv
        riktning = (180 + 180*Math.random());
        }
        if (posY == 0){ //tak
        riktning = (0 + 180*Math.random());
        }
        
        //vinklarna verkar gå moturs av någon anledning
        
        ArrayList<Liv> närområde = närområde();
        ArrayList<Konsument> matlista = new ArrayList<>();
        ArrayList<Konsument> hotlista = new ArrayList<>();
        ArrayList<Konsument> parningslista = new ArrayList<>();
        ArrayList<Producent> växtlista = new ArrayList<>();
        for (Liv a: närområde){
            if (!(a instanceof Producent)) {
                Konsument i = (Konsument)a;
                if (i.platsINäringskedjan > platsINäringskedjan) {
                    hotlista.add(i);
                } else if (platsINäringskedjan > i.platsINäringskedjan) {
                    matlista.add(i);
                } else if (platsINäringskedjan == i.platsINäringskedjan && i.hämtaListID() != hämtaListID()) { //för att hindra parning av två olika växtätararter
                    parningslista.add(i);
                }
            }
            else växtlista.add((Producent)a);
        }
        //2.5: parning, skrivs över om hot/mat är i närheten (gällande mat krävs också att man är hungrig
        if (!parningslista.isEmpty() && graviditetstid <= 0 && hunger > (int)(standardhunger*0.3 + 0.5) && könsmogen()){ //para sig om mer än 30% hungrig
            hoppaMotNärmstaIndivid(parningslista, true);
            //kanske måste bli närmsta fertil individ (någon som redan är gravid är inget att ha)
        }
        
        //3. En individ med lägre plats i näringskedjan syns (finns i synfält)
        if (!matlista.isEmpty() && hunger <= (int)(standardhunger*0.3 + 0.5)) { //sista villkoret för att ingen ska få typ hunger = 50
            hoppaMotNärmstaIndivid(matlista, false);
        }
        
        //samma nivå som ovan fast för växtätare, en växt är i närområdet och man är hungrig
        if (!växtlista.isEmpty() && platsINäringskedjan == 0 && hunger <= (int)(standardhunger*0.3 + 0.5)){ 
            hoppaMotNärmstaIndivid(växtlista, false);
        }

        
        //4. En individ med högre plats i näringskedjan syns
        if (!hotlista.isEmpty()){
            hoppaBortFrånAllaHot(hotlista);
        }
                
        if (riktning >= 360) riktning -= 360;
        //System.out.println("id: " + id + " posX: " + posX + " posY: " + posY + " Riktning: " + riktning + " Plats i N: " + platsINäringskedjan);
    }
    
    //ev. grow och reproduce
    
    private ArrayList<Liv> närområde(){
        ArrayList<Liv> lista = new ArrayList<>();
        
        //väldigt lik det som står i interact()
        for (Livlista storlista: Ekosystem.listaÖverListor) {
            for (Liv a : (ArrayList<Konsument>)storlista.lista) { //individlistan kan användas om man bara kör inre satsen
                if (a.ärDöd()) { //metoden equals fungerar inte riktigt för att kolla om man inte hittar sig själv - om hunger, gravid och ålder är lika är två objekt lika
                    continue;
                }
                double avstånd = Math.sqrt(Math.pow(a.posX - posX, 2) + Math.pow(a.posY - posY, 2));
                if (avstånd <= avsökningsavstånd && avstånd != 0) { //om avståndet är noll ligger individen i sin egen lista --> skumt med parning
                    lista.add(a);
                }
            }
        }
        
        return lista;
    }
    
    private void hoppaMotNärmstaIndivid(ArrayList<? extends Liv> lista, boolean parning){ //vid parning ska man bara hoppa mot motsatt kön som är nära
        Vektor2D minst = Vektor2D.MAXVEKTORN;
        for (Liv a : lista) {
            if (a instanceof Producent){
                Producent p = (Producent)a;
                if (!p.värdAttÄtas()) continue;
            }
            
            if (parning){
                Konsument i = (Konsument)a;
                if (hane == i.hane || !i.könsmogen()) continue;
                if (hane && i.graviditetstid != -1) continue;
            }
            Vektor2D v = new Vektor2D(posX, posY, a.posX, a.posY);
            if (v.compareTo(minst) == -1) {
                minst = v;
            }
        }
                    
        if (minst != Vektor2D.MAXVEKTORN) {
            riktning = minst.vinkel();
            if (minst.längd() < this.snabbhet) this.snabbhet = (int) (minst.längd() + 0.5);
        }
    }
    
    private void hoppaBortFrånAllaHot(ArrayList<Konsument> lista){
        Vektor2D[] hotvektorer = new Vektor2D[lista.size()];
            for (int i = 0; i<hotvektorer.length; i++){
                Konsument a = lista.get(i);
                Vektor2D v = new Vektor2D(posX, posY, a.posX, a.posY);
                Vektor2D hot = v.omvänd(10);
                hotvektorer[i] = hot;
            }
            Vektor2D totaltHot = Vektor2D.add(hotvektorer);
            Vektor2D riktningsvektor = totaltHot.komplementvektor();
            riktning = riktningsvektor.vinkel();
    }
    
    public synchronized void rita(Graphics g, int skala){ //alla individer är cirklar, har mittpunkt i posX, posY. //posX och posY kan ej ligga utanför, men delar av individen kan
        
        //if (ärDöd()) return;
        
        int x = posX*skala + Ekosystem.origoX;
        int y = posY*skala + Ekosystem.origoY;
        
        
        int radie = storlek*skala; //positionen måste också ändras med skalan, det som står nu bara för skala = 1
        double vinkelIRad = 2*Math.PI*riktning/360;
        
        //rita själva cirkeln med ett svart streck i riktningen den tittar åt.
    
        switch(platsINäringskedjan){
            case 0:
                g.setColor(Color.CYAN);
                //if (graviditetstid > -1) g.setColor(Color.CYAN);
                break;
            case 1:
                g.setColor(Color.RED);
                //if (graviditetstid > -1) g.setColor(Color.BLUE);
                break;
            case 2:
                g.setColor(Color.decode("#FF55FF")); //lila
                break;
            case 3:
                g.setColor(Color.decode("#FFFF55")); //gul
                break;
            default:
                g.setColor(Color.white); //höga konsumenter
        }
        
        switch(dödsOrsak){
            case "Predation":
                g.setColor(Color.LIGHT_GRAY);
                break;
            case "Svält":
                g.setColor(Color.DARK_GRAY);
                break;
            case "Ålder":
                g.setColor(Color.BLACK);
                break;
            default: 
        }
        
        
        g.fillOval(x-radie, y-radie, 2*radie, 2*radie);
        //ram för cirkeln
        g.setColor(Color.BLACK);
        //if (!hane) g.setColor(Color.MAGENTA); //för att skilja på könen får honor en annan färg på riktningsstreket
        
        //kant runt hela cirkeln
        g.drawOval(x-radie, y-radie, 2*radie, 2*radie);
        //riktningsstreck, går från mittpunkten ut till cirkelranden
        g.drawLine(x, y, x+(int)(radie*Math.cos(vinkelIRad) + 0.5), y+(int)(radie*Math.sin(vinkelIRad) + 0.5)); //problem med avrundning här?
        //System.out.println("paint(): id: " + id + " Vinkel: " + riktning);
        
        //id-siffra på varje så man kan skillja dem åt
        //g.setColor(Color.BLUE);
        //g.drawString(this.id + "", x, y);
        
    }

}
