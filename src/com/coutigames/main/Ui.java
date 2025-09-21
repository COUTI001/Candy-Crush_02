package com.coutigames.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.coutigames.main.Game;

public class Ui {
	
	public static int seconds = 0;
	public static int minutes = 0;
	public static int frames = 0;
	
	public void tick() {
		frames++;
		if(frames == 60) {
			//Passou 1 segundo.
			frames = 0;
			seconds++;
			if(seconds == 60) {
				seconds = 0;
				minutes++;				
					}
				}
			}
		

	public void render(Graphics g) {
		//vida
		//g.setColor(Color.red);
		//g.fillRect(10, 10, 200, 20);
		//g.setColor(Color.green);
		//modelo para perder vida utilizando o rand no player
	    //g.fillRect(10, 10,(int)((Player.lifePlayer/100) *200), 20);
	    //estrelas
	    //g.setColor(Color.orange);
	    //g.drawRect(10, 10, 200, 20);
	    //g.setFont(new Font("Arial",Font.ITALIC,30));
	    //g.drawString("Estrelas: " + Player.getStar + "/" + Player.maxStar,Game.WIDTH + 108, 30);
	    //pedras
	    g.setColor(Color.black);
	    g.setFont(new Font("arial",Font.ITALIC,30));
	    g.drawString("Pontos: " + (int)Game.points,50,30);
	    
	    String formatTime = "";
		if(minutes < 10) {
			formatTime+="0"+minutes+":";
		}else {
			formatTime+=minutes+":";
		}
		
		if(seconds < 10) {
			formatTime+="0"+seconds;
		}else {
			formatTime+=seconds;
		}
		
		g.setFont(new Font("arial",Font.BOLD,23));
		g.drawString(formatTime, 220, 30);
	    
	}
	
}
