/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boi;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Erik
 */
public class Producent extends Liv{
    
    private double biomassa; //just nu gäller att 1 biomassa är det som mättar en växtätare
    public double biomassa(){return this.biomassa;}
    private double maximalbiomassa;

    private int tillväxthastighet; //kanske ska kunna vara bråktal (double), dock problem i Mall då
    
    private static boolean[][] upptagnaPlatser = new boolean[Ekosystem.sidaX()][Ekosystem.sidaY()];
    public static void resetUpptagnaPlatser(){
        upptagnaPlatser = new boolean[Ekosystem.sidaX()][Ekosystem.sidaY()];
        for (int i =0; i<upptagnaPlatser.length; i++){
            for (int j = 0; j<upptagnaPlatser[i].length; j++){
                upptagnaPlatser[i][j] = false;
            }
        }
    }
    
    private Producent(int maximalbiomassa, int tillväxthastighet, int posX, int posY){
        super(-1);
        this.maximalbiomassa = maximalbiomassa;
        this.tillväxthastighet = tillväxthastighet;
        this.biomassa = (int)(maximalbiomassa*Math.random()); //så att början av simuleringen ungefär lika med resten
        //this.biomassa = 0; //detta ger linjärt samband!!!
        this.posX = posX;
        this.posY = posY;
    }
    
    public static Producent skapaPlanta(int maximalbiomassa, int tillväxthastighet){ //för att två inte ska hamna på samma ställe
        int posX = (int)(Ekosystem.sidaX()*Math.random());
        int posY = (int)(Ekosystem.sidaY()*Math.random());
        if (upptagnaPlatser[posX][posY]){
            return null;
        }
        upptagnaPlatser[posX][posY] = true;
        return new Producent(maximalbiomassa, tillväxthastighet, posX, posY);
    }
    
    //körs aldrig
    @Override
    protected void spawn(){ 
        //om man inför olika sorters mark kan man göra växter som bara trivs på vissa ställen
        this.posX = (int)(Ekosystem.sidaX()*Math.random());
        this.posY = (int)(Ekosystem.sidaY()*Math.random());
    }
    
    //otestad
    public void väx(){ 
        //nuär tillväxthastighet definierad som mängd biomassa som skapas per dag. 1000 motsvarar 1, 1020 motsvarar 1 + 2%chans för 2
        //2201 motsvarar 2 + en 20.1% chans för 3
        int fortsätt = tillväxthastighet;
        while (fortsätt >= 1000){
            this.biomassa++;
            fortsätt -= 1000;
        }
        if (1000*Math.random() < fortsätt) this.biomassa++;
        if (biomassa > maximalbiomassa) biomassa = maximalbiomassa;
    }
    
    public boolean värdAttÄtas(){
        return (biomassa>= 1);
    }
    
    public void bliÄten(){
        //just nu gäller att alla äter exakt 1 biomassa
        //växtätare som äter mer än 1 skulle kunna införas senare om man grejar lite här
        biomassa--;
    }
    
    @Override
    public void rita(Graphics g, int skala){
        //olika färger för olika tillväxtnivåer?
        //kvadratiska?
        int x = posX*skala + Ekosystem.origoX;
        int y = posY*skala + Ekosystem.origoY;
        
        int storlek = 1; //senare det som ska bli plats
        
        //färg senare med switch(biomassa)
        g.setColor(Color.GREEN);
        if (biomassa < 1) g.setColor(Color.LIGHT_GRAY);
        
        g.fillRect(x-(storlek/2)*skala, y-(storlek/2)*skala, storlek*skala, storlek*skala);
        
        //kant
        g.setColor(Color.BLACK);
        g.drawRect(x-(storlek/2)*skala, y-(storlek/2)*skala, storlek*skala, storlek*skala);
    }
    
}
