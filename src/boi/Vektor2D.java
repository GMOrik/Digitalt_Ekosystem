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
public class Vektor2D implements Comparable {
    private double längd;
    public double längd() {return this.längd;}
    private void sättLängd(double längd) {this.längd = Math.abs(längd);}
    private double vinkel; //0 <= x < 360 grader
    public double vinkel() {return this.vinkel;}
    
    public static final Vektor2D NOLLVEKTORN = new Vektor2D(0, 0);
    public static final Vektor2D MAXVEKTORN = new Vektor2D(Double.MAX_VALUE, 0);
    
    public Vektor2D(double längd, double vinkel){
        this.längd = längd;
        this.vinkel = fixaVinkel(vinkel);
    }
    
    public Vektor2D(double x1, double y1, double x2, double y2){ //konstruktor med två punkter
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        this.längd = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        
        double radVinkel = Math.atan(Math.abs(deltaY/deltaX));
        double vinkel = 360*radVinkel/(2*Math.PI);
        if (deltaX < 0 && deltaY < 0) vinkel += 180;
        else if (deltaX < 0) vinkel = 180-vinkel;
        else if (deltaY < 0) vinkel = - vinkel;
        double färdigVinkel = fixaVinkel(vinkel);
        this.vinkel = färdigVinkel;
    }
    
    private double fixaVinkel(double vinkel){
        double v = vinkel;
        if (längd == 0) v = 0;
        if (0 <= v && v < 360){
            return v;
        }
        if (v > 360){
            while (v > 360){
                v -=360;
            }
            return v;
        }
        if(v < 0){
            while(v < 0){
                v += 360;
            }
            return v;
        }
        //något är fel om den kommer hit
        return Integer.MAX_VALUE;
    }
    
    private double vinkelIRadianer(){
        return 2*Math.PI*vinkel/360;
    }
    
    public double xKomp(){ //positiv = åt höger, negativ = åt vänster
        double xLängd = Math.abs(längd*Math.cos(vinkelIRadianer()));
        if (vinkel > 90 && vinkel < 270){
            xLängd = -xLängd;
        }
        return xLängd;
    }
    
    public double yKomp(){ //positiv = uppåt, negativ = nedåt. //notera att ritytans koordinatsystem har positiv nedåt
        double yLängd = Math.abs(längd*Math.sin(vinkelIRadianer()));
        if (vinkel > 180){
            yLängd = -yLängd;
        }
        return yLängd;
    }
    
    public Vektor2D add(Vektor2D v){ //notera att alla aproximationer som sker hela tiden inte är helt bra
        double xKomp1 = this.xKomp();
        double xKomp2 = v.xKomp();
        double x2 = xKomp1 + xKomp2;
        double yKomp1 = this.yKomp();
        double yKomp2 = v.yKomp();
        double y2 = yKomp1 + yKomp2;
        return new Vektor2D(0, 0, x2, y2);
    }
    
    public static Vektor2D add(Vektor2D[] fält){
        Vektor2D v = fält[0];
        for (int i = 1; i<fält.length; i++){
            v = v.add(fält[i]);
        }
        return v;
    }
    
    public void skalärmultiplikation(double skalär){
        längd *= skalär;
    }
    
    public Vektor2D komplementvektor(){ //v + komplementvektor = nollvektorn
        double x = this.xKomp();
        double y = this.yKomp();
        //System.out.println(x + " " + y);
        return new Vektor2D(0, 0, -x, -y);
    }
    
    /**
     * Till för den omvända proportionaliteten för "hotvektorerna"
     * @param sökavstånd
     * @return 
     */
    public Vektor2D omvänd(double sökavstånd){ //sökavståndet här bör vara samma som i närområde() i Individ. Eller kanske inte spelar någon roll
        Vektor2D omvänd = this;
        omvänd.sättLängd(sökavstånd/längd);
        return omvänd;
    }
    
    
    @Override
    public String toString(){
        String s = "Längd: " + längd + " Vinkel: " + vinkel + " xKomp: " + xKomp() + " yKomp: " + yKomp();
        return s;
    }

    @Override
    public int compareTo(Object o) {
        Vektor2D a = (Vektor2D)o;
        if (this.längd > a.längd) return 1;
        else if (this.längd < a.längd) return -1;
        else return 0;
    }
    
    public void paint(java.awt.Graphics g, int x0, int y0, boolean röd){
        g.setColor(Color.BLACK);
        if (röd) g.setColor(Color.RED);
        g.drawLine(x0, y0, (int)(x0+xKomp() + 0.5), (int)(y0+yKomp() + 0.5));
    }
}
