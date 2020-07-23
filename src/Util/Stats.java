package Util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.lang.Integer;
import javax.swing.JComponent;

import LGames.*;

import java.io.PrintWriter;

/** Implementation of the statistics.

 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
*/

public abstract class Stats{

    protected double [] cs = new double [350];
    protected double [] ds = new double [350];
    protected int game = 1;
    protected int mid=175;
    protected int end=350;
    protected double pCoherence=0.0;
    protected double iCoherence=0.0;
    protected boolean update=false;

    public Stats(){
	//ds[0]=0.0;
	//cs[0]=0.0;
    }

    abstract int rescale();


    /** This function reinitialises the statistics at the start of
     * each iteration in the iterated learning model and when a new
     * game is started.
     *
     * @param c ='n' if a new game is started.
     */
    public abstract void reinitialize(char c);


    /** Updates the statistics at the end of each language game. The agents
     * are used to check whether the game was successful or not.
     *
     * @param S the speaker Agent
     * @param H the hearer Agent
     */

    public abstract void update(Agent S,Agent H,int t);

    /** This function writes the statistics of each game to the logfile.
     *
     * @param outfile the logfile.
     */
    public abstract void logFile(PrintWriter outfile);

    public void print(){}

    public double [] getCS(){
	return cs;
    }
    public double [] getDS(){
	return ds;
    }
    public int getGame(){
	return game;
    }
    public int getMid(){
	return mid;
    }
    public int getEnd(){
	return end;
    }

    public double getPCoh(){
	return pCoherence;
    }

    public double getICoh(){
	return iCoherence;
    }

    public boolean getUpdate(){
	return update;
    }

    public abstract void updateCoherence(final double x,final double y);


}
