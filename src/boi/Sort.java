/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boi;
import java.awt.Color;
/**
 *
 * @author Erik
 */
public enum Sort{
    PRODUCENT(1, "Producenter", Color.GREEN), VÄXTÄTARE(2, "Växtätare", Color.CYAN), ROVDJUR(3, "Rovdjur", Color.RED);
    private final int värde;
    private final String text;
    private final Color färg; 
    
    private Sort(int värde, String text, Color färg){
        this.värde = värde;
        this.text = text;
        this.färg = färg;
    }
    
    public int värde(){
        return this.värde;
    }
    
    @Override
    public String toString(){
        return this.text;
    }
    
    public static Sort hämtaSort(String s){
        switch(s){
            case "Producenter":
                return PRODUCENT;
            case "Växtätare":
                return VÄXTÄTARE;
            case "Rovdjur":
                return ROVDJUR;
            default: return null;
        }
    }
    
    public Color färg(){
        return this.färg;
    }
    
}
