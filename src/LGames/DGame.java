
package LGames;

import java.util.*;
import Util.Utils;
/**
 * The class DGame implements the discrimination games.
 * The function playGame is used to control the game.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */
public class DGame{

    private Meaning TOPIC;
    private final double thresholdPType = 0.2;
    protected int iMeaning=1;
    protected int maxMeanings=350;
    private char uType='a';
    private int dim=0;
    private List ontology=null;

    /**
     * Empty constructor
     */
    public DGame(){
    }

    /**
     * Constructor with a limit on the number of meanings
     *
     * @param M the maximum number of meanings that are allowed in the agent's memory
     */
    public DGame(final int M){
	maxMeanings=M;
    }
    /**
     * Constructor that initialises a pointer to the agents ontology
     *
     * @param o the ontology
     */

    public DGame(List o){
	ontology=o;
    }
    /**
     * Sets the limit on the number of meanings#
     *
     * @param M the maximum number of meanings that are allowed in the agent's memory
     */
    
    public void setMaxMeanings(final int M){
	maxMeanings=M;
    }

    /** This function implements the categorisation of all the featureVector in the
     * context using the 1 nearest neighbourhood search.
     *
     * @param cxt The context
     */
    private void categorize(Context cxt){

	double d;
	double minD;
	int cat;
	for (int i=0;i<cxt.categoryLength();i++){
	    cxt.setCategory(i,-1);
	    minD=100000.0;
	    for (int j=1;j<ontology.size();j++){
		d=Utils.distance(((Meaning)ontology.get(j)).getPrototype(),
				 (double [])cxt.featureVector[i]);
		if (d<minD){
		    minD=d;
		    cxt.setCategory(i,j);
		}
	    }
	}
    }

    /**
     * Sets the dimensionality of the conceptual space
     *
     * @param d the dimension
     */
    public void setDim(final int d){
	dim=d;
    }
    /**
     * @return the dimension of the conceptual space
     */
    public int getDim(){
	return dim;
    }

    /**
     * playGame controls a discrimination game. The function first calls a
     * categorisation function where it categorises the featureVector of the context
     * in relation to the ontology.
     * It then tries to discriminate the topic from the rest of the context. If
     * this fails, a new category is added to the ontology, otherwise the 
     * category is shifted towards the topic and possibly merged with another
     * category if it comes too close.
     *
     * @param cxt the Context
     * @param topic the index of the topic pointing to the <i>topic</i>-th 
     * element of the cxt list.
     * @param adapt sets the learning/adaptation on (true) or off (false)
     *
     * @return Meaning the meaning of the topic, null if the discrimination game fails.
     */
    public int playGame(Context cxt,final int topic,final boolean adapt){

	int retval=-1;
	boolean SUCCESS = true;

	//first we categorise all objects
	categorize(cxt);

	//the topic is not categorised
	if (cxt.getCategory(topic)<=0)
	    SUCCESS=false;

	//check if the topic's category is distinctive
	retval=cxt.getCategory(topic);
	for (int i=0;i<cxt.categoryLength() && SUCCESS;i++){
	    if (i!=topic)
		if (cxt.getCategory(topic) == cxt.getCategory(i)){
		    SUCCESS = false;
		    retval=-1;
		}
	}

	if (SUCCESS)//it is distinctive, tell it the context representation
	    cxt.setMeaning((Meaning)ontology.get(cxt.getCategory(topic)),topic);
	else{//construct a new meaning (if adaptation is on and the memory size is not reached yet)
	    if (adapt && ontology.size()<maxMeanings){
		Meaning newMeaning = new Meaning((double [])cxt.
						 featureVector[topic],
						 iMeaning);
		boolean flag=true;
		//and add the new meaning to the ontology if it does not compare to another prototype
		for (int i=0;i<ontology.size() && flag;i++)
		    if (newMeaning.comparePType(((Meaning)ontology.get(i)).
						getPrototype()))
			flag=false;
		if (flag){
		    ontology.add(newMeaning);		
		    iMeaning++;
		}
	    }
	}
	return retval;
    }
}



