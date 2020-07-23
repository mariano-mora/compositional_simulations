package Util;

import java.io.PrintWriter;

import LGames.Agent;
import LGames.HolisticAgent;
import LGames.HolisticAgent2;

/** Implementation of the statistics.

 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
*/

public class HolisticStats extends Stats{

    
    private int [] csTrace = new int [50];
    private double [] dsTrace = new double [50];
    protected double [] cs = new double [350];
    protected double [] ds = new double [350];
    private double dsSum = 0.0;
    private int csSum = 0;
    private final int length = 350;
    private int trace = 0;
    private int scale = 1;
    protected int game = 1;
    private final int maxTrace = 50;
    private int lg=1;
    protected int mid=175;
    protected int end=350;
    private int n=1;
    private int game0=1;
    private double cs0=0.0;
    private double ds0=0.0;

    private Agent speaker=null;
    private Agent hearer=null;

    private boolean DS=false;
    private boolean CS=true;
    
   protected double pCoherence=0.0;
    protected double iCoherence=0.0;
    protected boolean update=false;

    /**
     * constructor
     */
    public HolisticStats(){
	ds[0]=0.0;
	cs[0]=0.0;
    }
    /**
     * This function rescales the arrays for the UI
     */
    protected int rescale(){

	for (int i=0;i<175;i++){
	    cs[i]=cs[2*i];
	    ds[i]=ds[2*i];
	}
	return 175;
    }

    /** This function reinitialises the statistics at the start of
     * each iteration in the iterated learning model and when a new
     * game is started.
     *
     * @param c ='n' if a new game is started.
     */
    public void reinitialize(char c){
	dsSum=0.0;
	csSum=0;
	trace=0;
	game0=1;
	if (c=='n'){
	    scale=1;
	    game=1;
	    lg=1;
	    mid=175;
	    end=350;
	    n=1;
	}
	for (int i=0;i<dsTrace.length;i++){
	    dsTrace[i]=0.0;
	    csTrace[i]=0;
	}
	update=false;
    }
    /**
     * This function updates the production coherence and interpretation coherence (aka accuracy) measures.
     */

    public void updateCoherence(final double x,final double y){
	pCoherence=x;
	iCoherence=y;
	update=true;
    }

    /** Updates the statistics at the end of each language game. The agents
     * are used to check whether the game was successful or not.
     *
     * @param S the speaker Agent
     * @param H the hearer Agent
     * @param t the agent's type

     */

    public void update(Agent S,Agent H,int t){

	DS=false;
	CS=false;
	if (t==0){
	    speaker=(HolisticAgent)S;
	    hearer=(HolisticAgent)H;
	}
	else{
	    speaker=(HolisticAgent2)S;
	    hearer=(HolisticAgent2)H;
	}

	dsSum-=0.5*dsTrace[trace];

	dsTrace[trace]=0;
	dsTrace[trace]+=S.getDS();
	dsTrace[trace]+=H.getDS();
	if (dsTrace[trace]>0.0) DS=true;
	dsSum+=0.5*dsTrace[trace];
	//System.out.println(dsSum+" "+dsTrace[trace]);

	int traceLength=Math.min(game0,maxTrace);
	ds[game]=dsSum/(double)traceLength;
	ds0=dsSum/(double)traceLength;

	csSum-=csTrace[trace];


	if (speaker.getUtterance()!=null && 
	    hearer.getUtterance()!=null &&
	    speaker.getTopic() == hearer.getTopic()){
	    csTrace[trace]=1;
	    CS=true;
	}
	else csTrace[trace]=0;
	csSum+=csTrace[trace];
	cs[game]=(double)csSum/(double)traceLength;
	cs0=(double)csSum/(double)traceLength;

	if (trace<maxTrace-1)
	    trace++;
	else trace=0;

	if (n == scale){
	    if (game < length-1)
		game ++;
	    else{
		game = rescale();
		scale=2*scale;
		mid=lg+1;
		end=2*mid;
	    }
	    n=1;
	}
	else n++;
	lg++;game0++;

	update=false;
    }

    /** This function writes the statistics of each game to the logfile.
     *
     * @param outfile the logfile.
     */
    public void logFile(PrintWriter outfile){

	if (lg==2){
	    outfile.println("#lg S topic mS-id mS-layer mS-ptype uS-form H topic mH-id mH-layer mH-ptype uH-form <DS> DS <CS> CS");
	    outfile.println("0 -1 -1 -1 -1 [-1] * -1 -1 -1 -1 [-1] * 0 false 0 false");
	}

        outfile.println(lg-1+" "+speaker.getGame()+" "+hearer.getGame()+
	    " "+Utils.doubleString(ds0,4)+" "+DS+" "+Utils.doubleString(cs0,4)+" "+CS);

	if (lg%10==0)
	    outfile.flush();
    }

    /**
     * Function to write the game information to the standard output
     */
    public void print(){
	if (speaker!=null && hearer!=null)
	    System.out.println("game "+(lg-1)+" "+speaker.getGame()+" "+hearer.getGame()+
			       " "+Utils.doubleString(ds0,4)+" "+DS+" "+
			       Utils.doubleString(cs0,4)+" "+CS);

	if (lg%10==0)
	    System.out.flush();
    }

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

}
