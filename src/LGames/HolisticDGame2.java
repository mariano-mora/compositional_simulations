
package LGames;

import java.util.List;

import Util.IntArray;
import Util.Utils;
/**
 * The class HolisticDGame2 implements the discrimination games for HolisticAgent2. I.e. for agents
 * with a hierarchical layering of meanings.<br>
 * The function playGame is used to control the game.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */
public class HolisticDGame2 extends DGame{

    private Meaning TOPIC;
    private final double thresholdPType = 0.2;
    protected int iMeaning=1;
    protected int maxMeanings=350;
    private char uType='a';
    private List ontology;
    private int maxLayer=1;
    private IntArray density = new IntArray(1);
    private int dim=0;

   /**
     * Empty constructor
     */
    public HolisticDGame2(){
    }
    /**
     * Constructor that initialises a pointer to the agents ontology
     *
     * @param o the ontology
     */
    public HolisticDGame2(List o){
	ontology=o;
    }
    /**
     * Sets the number of dimensions of the conceptual space
     */
    public void setDim(final int d){
	dim=d;
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
     * context using the 1 nearest neighbourhood search on a given layer
     *
     * @param cxt The context
     * @param layer The hierarchical layer of the current set of discrimination games
     */
    private void categorize(Context cxt,final int layer){

	double d;
	double minD;
	int cat;
	Meaning m=null;
	for (int i=0;i<cxt.categoryLength();i++){
	    cxt.setCategory(i,-1);
	    minD=100000.0;
	    for (int j=1;j<ontology.size();j++){
		m=(Meaning)ontology.get(j);
		if (m.getLayer()==layer){
		    d=Utils.distance(((Meaning)ontology.get(j)).getPrototype(),
				     (double [])cxt.featureVector[i]);
		    if (d<minD){
			minD=d;
			cxt.setCategory(i,j);
		    }
		}
	    }
	}
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
     * @param ontology the ontology
     * @param topic the index of the topic pointing to the <i>topic</i>-th 
     * element of the cxt list.
     * @param layer the hierarchical at which the discrimination game is played
     * @param adapt sets the learning/adaptation on (true) or off (false)
     *
     * @return Meaning the meaning of the topic, null if the discrimination game fails.
     */
    public int playGame(Context cxt,final int topic,final int layer,final boolean adapt){

	int retval=-1;
	boolean SUCCESS = true;

	//first categorise
	categorize(cxt,layer);

	//if the topic has no category...
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

	if (SUCCESS)//update the context representation
	    cxt.setMeaning((Meaning)ontology.get(cxt.getCategory(topic)),topic);
	else{
	    //if allowed...
	    if (adapt && ontology.size()<maxMeanings){
		//the ontology is expanded if the current layer did not reach its limit yet.
		if (density.get(layer-1)<Math.pow(dim,layer)){
		    Meaning newMeaning = new Meaning((double [])cxt.
						     featureVector[topic],
						     iMeaning,layer);
		    boolean flag=true;
		    //if the new meaning is truly new...
		    for (int i=0;i<ontology.size() && flag;i++){
			if (newMeaning.comparePType(((Meaning)ontology.get(i)).
						    getPrototype())){
			    flag=false;
			}
		    }
		    if (flag){//then the meaning actually added.
			retval=ontology.size();
			ontology.add(newMeaning);		
			iMeaning++;
			density.increment(layer-1);
			cxt.setMeaning(newMeaning,topic);
		    }
		}
		else if (maxLayer==layer){//if we reached the deepest layer, allow to go to the next
		    maxLayer++;
		    density.add(0);
		}
	    }
	}
	return retval;
    }

    /**
     * This function makes sure that when forgetting a meaning, the density of the layer is adapted accordingly
     */
    public void forget(final int n){//if a meaning is pruned, the density of the layer has to be decreased
	int layer=((Meaning)ontology.get(n)).getLayer();
	if (density.get(layer-1)>0)
	    density.decrement(layer-1);
    }
    /**
     * @return the 'deepest' layer that has been exploited up to now.
     */
    public int getMaxLayer(){
	return maxLayer;
    }
}



