package Interface;

import Util.Utils;
import LGames.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 * This class implements the window frame that displays the language game dialogues.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class LanguageJComponent extends JComponent{

    private int s=-1;
    private int h=-1;
    private Meaning topicSpeaker=null;
    private Meaning topicHearer=null;
    private String utteranceS=null;
    private String utteranceH=null;
    private boolean success=false;
    private int LG=0;
    private Color c;
    private double [] ptype;
    private double [] fvSpeaker=null;
    private double [] fvHearer=null;
    private int w=0;
    private int iter=0;
    private String labels="RGBSXY";
    private char type = 'g';
    private int ts=-1;
    private int th=-1;
    private int aType=2;
    private Rules2 ruleS=null;
    private Rules2 ruleH=null;
    private int layerS=0;
    private int layerH=1;
    private String newRuleS=new String();
    private String newRuleH=new String();
    private String [] utterancePartsS=null;
    private String [] utterancePartsH=null;


    public LanguageJComponent(){
    }

    public void updateGame(int lg,Agent speaker,Agent hearer,int i,char t,int at){
	s=speaker.getID();
	h=hearer.getID();
	type=t;
	aType=at;
	topicSpeaker=speaker.getTOPIC();
	fvSpeaker=speaker.getFV();
	labels=speaker.getLabels();
	topicHearer=hearer.getTOPIC();
	fvHearer=hearer.getFV();
	utteranceS=speaker.getUtterance();
	utteranceH=hearer.getUtterance();
	if (aType==1){
	    layerS=((HolisticAgent2)speaker).getLayer();
	    layerH=((HolisticAgent2)hearer).getLayer();
	}
	if (aType==2){
	    ruleS=((CompositionalAgent2)speaker).getRule();
	    ruleH=((CompositionalAgent2)hearer).getRule();
	    newRuleH=((CompositionalAgent2)hearer).getNewRule();
	    utterancePartsS=((CompositionalAgent2)speaker).getParts();
	    utterancePartsH=((CompositionalAgent2)hearer).getParts();
	    newRuleS=((CompositionalAgent2)speaker).getNewRule();
	}
	iter=i;
	ts=speaker.getTopic();
	th=hearer.getTopic();
	if (hearer.getUtterance()!=null && th == ts)
	    success=true;
	else success=false;
	LG=lg;
    }

    public void updateNewGame(){
	LG=0;
    }

    private Color setColor(int i){
	switch (i){
	case 0:
	    return Color.red;
	case 1:
	    return Color.green;
	case 2:
	    return Color.blue;
	case 3:
	    return Color.pink;
	case 4:
	    return Color.white;
	case 5:
	    return Color.orange;
	case 6:
	    return Color.yellow;
	default:
	    return Color.lightGray;
	}
    }

    public void paintComponent(final Graphics tGraphics){
	tGraphics.drawString("Iteration "+iter+", language game "+LG+
			     ", type="+type,80,20);
	if (LG>0){
	tGraphics.drawString("Speaker A"+s+" topic="+ts,20,40);
	tGraphics.drawString("Hearer A"+h+" topic="+th,200,40);
	if (topicSpeaker!=null){
	    if (aType==0)
		tGraphics.drawString("Meaning: M"+topicSpeaker.getID(),20,60);
	    else if (aType==1)
		tGraphics.drawString("Meaning: M"+topicSpeaker.getID()+" l="+layerS,20,60);
	    else if (ruleS!=null)//atype=2
		tGraphics.drawString(newRuleS+ruleS.niceString(),20,60);
	    else tGraphics.drawString("no rule S",20,60);
	}

	if (topicHearer!=null){
	    if (aType==0)
		tGraphics.drawString("Meaning: M"+topicHearer.getID(),200,60);
	    else if (aType==1)
		tGraphics.drawString("Meaning: M"+topicHearer.getID()+" l="+layerH,200,60);
	    else if (ruleH!=null)
		tGraphics.drawString(newRuleH+ruleH.niceString(),200,60);
	    else tGraphics.drawString("no rule H",200,60);
	}

	if (utteranceS!=null){
	    if (aType==2){
		if (success)
		    tGraphics.drawString("expr. S:`"+
					 Utils.printStringArray(utterancePartsS)+"', H:`"+
					 Utils.printStringArray(utterancePartsH)+", LG succeeds.",
					 40,220);
		else tGraphics.
			 drawString("expr. S `"+
				    Utils.printStringArray(utterancePartsS)+"', H:`"+
					 Utils.printStringArray(utterancePartsH)+"', LG fails.",
				    40,220);
	    }
	    else{
		if (success)
		    tGraphics.
			drawString("expr. S:`"+
				   utteranceS+"', H:`"+utteranceH+", LG succeeds.",
				   40,220);
		else tGraphics.
			 drawString("expr. S `"+
				    utteranceS+"', LG fails.",
				    40,220);
	    }
	}
	else tGraphics.drawString("No expr., LG fails.",
				  40,220);

	if (fvSpeaker!=null)
	    w=(int)(160.0/(double)fvSpeaker.length);

	if (topicSpeaker!=null){
	    ptype=topicSpeaker.getPrototype();
	    tGraphics.drawString("1",5,85);
	    tGraphics.drawString("0",5,185);
	    tGraphics.drawLine(15,80,20,80);
	    tGraphics.drawLine(15,180,20,180);

	    for (int i=0;i<ptype.length;i++){
		c=setColor(i);
		tGraphics.setColor(c);
		tGraphics.fillRect(20+i*w,180-(int)(100.0*ptype[i]),
				   w,(int)(100.0*ptype[i]));
	    }
	}
	else tGraphics.drawString("Discr. game fails",20,60);


	if (topicHearer!=null){
	    ptype=topicHearer.getPrototype();
	    //System.out.println(topicHearer);
	    
	    for (int i=0;i<ptype.length;i++){
		c=setColor(i);
		tGraphics.setColor(c);
		tGraphics.fillRect(200+i*w,180-(int)(100.0*ptype[i]),
				   w,(int)(100.0*ptype[i]));
	    }
	}
	else{
	    tGraphics.setColor(Color.black);
	    tGraphics.drawString("Discr. game fails",200,60);
	}

	if (fvSpeaker!=null){
	    tGraphics.setColor(Color.black);
	    for (int i=0;i<fvSpeaker.length;i++){
		tGraphics.drawRect(20+i*w,180-(int)(100.0*fvSpeaker[i]),
				   w,(int)(100.0*fvSpeaker[i]));
		tGraphics.drawRect(200+i*w,180-(int)(100.0*fvHearer[i]),
				   w,(int)(100.0*fvHearer[i]));
		tGraphics.drawString(labels.substring(i,i+1),20+i*w,200);
		tGraphics.drawString(labels.substring(i,i+1),200+i*w,200);
	    }
	}
	}
    }

}


