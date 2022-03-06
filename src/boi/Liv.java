/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boi;

/**
 *
 * @author Erik
 */
public abstract class Liv implements Comparable {
    protected int posX;
    public int posX(){return this.posX;}
    protected int posY;
    public int posY(){return this.posY;}
    
    protected final int platsINäringskedjan;
    public int platsINäringskedjan() {return this.platsINäringskedjan;}
    
    //för listflytter och dyl. nedan
    private int listID = -1;
    public int hämtaListID() {return this.listID;}
    public <T extends Liv> void geSammaIDSom(T förälder){this.listID = förälder.hämtaListID();}
    public Livlista hämtaLista(){return Livlista.getByID(this.listID, Ekosystem.listaÖverListor);}
    
    //dödvariabler
    private boolean död = false;
    public boolean ärDöd() {return this.död;}
    protected String dödsOrsak = "N/A";
    public String dödsOrsak() {return this.dödsOrsak;}
    
    synchronized void dö(String dödsorsak){
    //if (Ekosystem.individlista.isEmpty()) throw new Error();
    this.död = true;
    this.dödsOrsak = dödsorsak;
    this.hämtaLista().dö(this, dödsorsak);
    }
    
    //konstruktor
    public Liv(int platsINäringskedjan){
        this.platsINäringskedjan = platsINäringskedjan;
        this.listID = Livlista.hämtaStaticID();
    }
    
    protected abstract void spawn();
    abstract void rita(java.awt.Graphics g, int skala);
    
    @Override
    public int compareTo(Object o) {
        Liv a = (Liv)o;
        if (this.platsINäringskedjan > a.platsINäringskedjan){
            return 1;
        }
        else if (this.platsINäringskedjan < a.platsINäringskedjan){
            return -1;
        }
        else{
            return 0;
        }
    }
}
