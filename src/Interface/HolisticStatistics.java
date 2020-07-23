package Interface;

import Util.*;
import LGames.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.lang.Integer;
import javax.swing.JComponent;

/**
 * Implements the statistics window frame.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class HolisticStatistics extends Statistics{

    private double [] cs;
    private double [] ds;
    private int game=1;
    private final int maxTrace = 50;
    private int mid=175;
    private int end=350;
    private HolisticStats pS = null;
    private double pCoherence;
    private double iCoherence;
    private boolean update=false;

    /**
     * Constructs the frame with a pointer to the class where the 
     * actual statistics are maintained. It appeared to be more
     * easy to keep these two apart.
     *
     * @param pStats the actual statistics
     */
    public HolisticStatistics(){}

    public HolisticStatistics(final Stats pStats){
	//pS=(HolisticStats)pStats;
	
	ds=pStats.getDS();
	cs=pStats.getCS();
	game=pStats.getGame();
	mid=pStats.getMid();
	end=pStats.getEnd();
	setOpaque(false);	
    }

    /** After the statistics have been calculated, these have to be updated
     * for the window frame.
     *
     * @param pStats the actual statistics
     */
    public void update(final Stats pStats){
	if (pStats!=null){
	    //HolisticStats pH = (HolisticStats)pStats;
	    //   pStats.print();
	    ds=pStats.getDS();
	    cs=pStats.getCS();
	    game=pStats.getGame();
	    mid=pStats.getMid();
	    end=pStats.getEnd();
	    pCoherence=pStats.getPCoh();
	    iCoherence=pStats.getICoh();
	    update=pStats.getUpdate();    
	    //    System.out.println(game+" "+mid+" "+end);
	}	 
    }

    public void  paintComponent(final Graphics tGraphics){


	tGraphics.setColor(Color.white);
	tGraphics.fillRect(0,0,400,270);
	tGraphics.setColor(Color.black);
	//	System.out.println("updating statistics");

	tGraphics.drawLine(30,10,30,210);
	tGraphics.drawLine(30,210,380,210);
	tGraphics.drawLine(28,10,30,10);
	tGraphics.drawLine(28,30,30,30);
	tGraphics.drawLine(28,50,30,50);
	tGraphics.drawLine(28,70,30,70);
	tGraphics.drawLine(28,90,30,90);
	tGraphics.drawLine(28,110,30,110);
	tGraphics.drawLine(28,130,30,130);
	tGraphics.drawLine(28,150,30,150);
	tGraphics.drawLine(28,170,30,170);
	tGraphics.drawLine(28,190,30,190);
	tGraphics.drawLine(28,210,30,210);
	tGraphics.drawLine(30,210,30,212);
	tGraphics.drawLine(205,210,205,212);
	tGraphics.drawLine(380,210,380,212);
	tGraphics.drawString("1.0",5,15);
	tGraphics.drawString("0.8",5,55);
	tGraphics.drawString("0.6",5,95);
	tGraphics.drawString("0.4",5,135);
	tGraphics.drawString("0.2",5,175);
	tGraphics.drawString("0.0",5,215);
	tGraphics.drawString("0",28,230);
	tGraphics.drawString((new Integer(mid)).toString(),193,230);
	tGraphics.drawString((new Integer(end)).toString(),358,230);

	tGraphics.setColor(Color.red);
	tGraphics.drawString("- Discr. success",275,190);
	tGraphics.setColor(Color.blue);
	tGraphics.drawString("- Comm. success",275,200);

	//	cs=pS.getCS();
	//ds=pS.getDS();
	for (int i=1;i<game;i++){
	    tGraphics.setColor(Color.blue);
	    tGraphics.drawLine(30+i-1,210-(int)(200.0*cs[i-1]),
			       30+i,210-(int)(200.0*cs[i]));
	    tGraphics.setColor(Color.red);
	    tGraphics.drawLine(30+i-1,210-(int)(200.0*ds[i-1]),
			       30+i,210-(int)(200.0*ds[i]));
	}

	if (update){
	    tGraphics.setColor(Color.black);
	    tGraphics.drawString("pCoherence="+pCoherence,100,190);
	    tGraphics.drawString("iCoherence="+iCoherence,100,200);
	}
    }

}





