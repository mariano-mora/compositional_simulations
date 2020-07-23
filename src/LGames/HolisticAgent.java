package LGames;

import Util.*;
import java.util.*;
import java.io.PrintWriter;

/**
 * The HolisticAgent class implements the structure and behaviours of the 
 * holistic agent with only one layer in the conceptual space.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class HolisticAgent extends Agent{

    protected List ontology = new ArrayList();
    protected List lexicon = new ArrayList();
    private DGame discriminationGame = new DGame(ontology);
    protected int maxMeanings=600;
    protected int maxNSymbols=600;
    protected int [][] lUse = null;
    protected double [][] lScore = null;
    protected int iSymbols = 0;
    protected int iMeaning = 0;
    protected int nMeanings = 1;
    protected int nSymbols = 1;
    protected int nrDGSuccess=0;
    protected int DG=0;
    protected int utteranceID=0;
    protected double eta=0.9;
    protected double etaS=0.95;
    protected double etaN=0.9;
    protected String mString = new String("-1 -1 -1 [-1]");
    private final double thresholdPType = 0.05;
    protected final double thresholdForgetting = 0.002;
    protected final double thresholdForgettingS = 0.02;
    protected int gamesPlayed=0;
    protected int lg=0;

    public HolisticAgent(){}

    /**
     * Constucts an agent with identity n
     *
     * @param n the identity of the agent
     * @param a the agent's `birthdate' (i.e. language game nr.)
     * @param e the standard learning rate eta
     * @param s the learning rate eta for the speakers in case we wish to distinguish
     * @param u the type score-update
     * @param nM the memory size for meanings
     * @param nS the memory size for symbols
     * @param fv the array of features that were selected
     */
    public HolisticAgent(int n,final int a,final double e,final double s,
		 final char u,final int nM, final int nS,final boolean [] fv){
	id = n;
	age = a;
	etaN=e;
	etaS=s;
	maxMeanings=nM;
	maxNSymbols=nS;
	ontology.add(new Meaning(iMeaning));iMeaning++;//creating dummy meaning
	lexicon.add(new Symbols(iSymbols,"dummy"));iSymbols++;
	if (u=='s')
	    lScore=new double[maxMeanings][maxNSymbols];
	else lUse=new int[maxMeanings][maxNSymbols];
	for (int i=0;i<maxMeanings;i++)
	    for (int j=0;j<maxNSymbols;j++){
		if (u=='s')
		    lScore[i][j]=0.0;
		else lUse[i][j]=0;
	    }
	dimension=0;
	for (int i=0;i<fv.length;i++)
	    if (fv[i]) dimension++;
	discriminationGame.setDim(dimension);
	discriminationGame.setMaxMeanings(maxMeanings);

    }


    /**
     * Sets some topic value, used in a test function.
     */

    public void setTopicID(final int t){
	topicID=cxt.getCategory(t);
	TOPIC=(Meaning)ontology.get(topicID);
    }
 
   /**
     * Sets some topic value, used in a test function.
     */

    public void setTOPICID(final int t){
	topicID=t;
	utteranceID=0;
    }

    /**
     * specifies which element in the context is the topic. Used in guessing.
     */
    public void setTopic(final int t){
	topic=t;
	if (topic<0) TOPIC=null;
    }

    /**
     * returns the topic's meaning.
     */
    public Meaning getTOPIC(){
	return TOPIC;
    }


    /** Merges two categories
     *
     * @param type type of games that are being played
     * @param newC ID of the new (remaining) category
     * @param old ID of the category that is merged with the new one
     */
    private void merge(char type,int newC, int old){
	int oldest=0;
	int newest=0;
	if (type=='u')
	    for (int i=0;i<lexicon.size();i++)
		lUse[newC][i]+=lUse[old][i];
	else{
	    for (int i=0;i<lexicon.size();i++){
		if (lScore[newC][i]<=lScore[old][i]) oldest++;
		else newest++;
		lScore[newC][i]=Math.max(lScore[newC][i],lScore[old][i]);
	    }
	    if (oldest>newest) ((Meaning)ontology.get(newC)).
				   setID(((Meaning)ontology.get(old)).getID());
	}
	for (int i=old;i<ontology.size()-1;i++){
	    if (type=='u')
		lUse[i]=lUse[i+1];
	    else lScore[i]=lScore[i+1];
	}
	ontology.remove(old);
    }
    /** This function implements the removal of categories or symbols
     * in case of forgetting.
     *
     * @param type type of the game that is being played
     * @param rc indicates whether it concerns a category (o) or symbol (otherwise)
     * @param n the index of the element to be removed
     */
    protected void forget(char type,char rc,int n){

	if (rc=='o'){//remove meaning
	    for (int i=n;i<ontology.size()-1;i++){
		if (type=='u')
		    lUse[i]=lUse[i+1];
		else lScore[i]=lScore[i+1];
	    }
	    ontology.remove(n);
	}
	else{//remove symbol
	    for (int i=n;i<lexicon.size()-1;i++){
		for (int j=0;j<ontology.size();j++){
		    if (type=='u')
			lUse[j][i]=lUse[j][i+1];
		    else lScore[j][i]=lScore[j][i+1];
		}
	    }
	    lexicon.remove(n);
	}
    }

    /**
     * Used by forget. Inserts score with value x in a sorted list
     */
    private void insert_sorted_score(DoubleArray s,IntArray ind,double x,int n){
	int i=0;
	while (i<s.size()){
	    if (x<=s.get(i)){
		s.add(i,x);
		ind.add(i,n);
		return;
	    }
	    i++;
	}
	s.add(i,x);
	ind.add(i,n);
    }

    /** This function implements the search for elements that can be forgotten
     * based on their effectiveness
     *
     * @param type type of game that is being played
     */

    public void forget(char type){
	boolean forget;
	int [] flUse = new int [lexicon.size()];
	int [] foUse = new int [ontology.size()];
	int fg=-1;
	if (type=='u'){
	    double product,minProduct=1000000.0;

	    if (ontology.size()>=maxMeanings-1){ //forget ontology element
		//first find 'worst' element, then remove it
		DoubleArray scores=new DoubleArray();
		IntArray indices=new IntArray();
		for (int i=1;i<ontology.size();i++){
		    product=0.0;
		    for (int j=1;j<lexicon.size() && lUse[i][0]>0;j++) 
			if (lUse[0][j]>0) product+=
					      (double)(lUse[i][j]*lUse[i][j])/
					      (double)(lUse[i][0]*lUse[0][j]);
		    insert_sorted_score(scores,indices,product,i);
		}
		int N=(int)Math.round(0.02*ontology.size());
		indices.sortd();
		for (int i=0;i<N;i++){
		    for (int j=1;j<lexicon.size();j++)
			lUse[0][i]-=lUse[indices.get(i)][i];
		    forget(type,'o',indices.get(i));
		}
	    }

	    minProduct=1000000.0;
	    fg=-1;

	    if (lexicon.size()>=maxNSymbols-1){ // forget lexical element
		//first find 'worst' element, then remove it

		
		double maxProduct=0.0;
		double sum=0.0;
		DoubleArray scores=new DoubleArray();
		IntArray indices=new IntArray();
		for (int i=1;i<lexicon.size();i++){
		    product=0.0;
		    for (int j=1;j<ontology.size() && lUse[0][i]>0;j++) 
			if (lUse[j][0]>0) 
			    product+=(double)(lUse[j][i]*lUse[j][i])/
				(double)(lUse[0][i]*lUse[j][0]);

		    insert_sorted_score(scores,indices,product,i);
		}
		int N=(int)Math.round(0.02*lexicon.size());
		indices.sortd();
		for (int i=0;i<N;i++){
		    for (int j=1;j<ontology.size();j++)
			lUse[j][0]-=lUse[j][indices.get(i)];
		    forget(type,'l',indices.get(i));
		}
	    }

	}
	else{
	    for (int i=1;i<ontology.size();i++){
		forget=true;
		for (int j=1;j<lexicon.size() && forget;j++)
		    if (lScore[i][j]>thresholdForgetting)
			forget=false;
		if (forget){
		    forget(type,'o',i);
		    i--;
		}
	    }
	    for (int j=1;j<lexicon.size();j++){
		forget=true;
		for (int i=1;i<ontology.size() && forget;i++)
		    if (lScore[i][j]>thresholdForgetting)
			forget=false;
		if (forget){
		    forget(type,'l',j);
		    j--;
		}
	    }
	}
    }

    /**
     * @return lexiconSize
     */
    public int getLexSize(){
	return lexicon.size();
    }

    /**
     * @return ontologySize
     */
    public int getOntSize(){
	return ontology.size();
    }

    /**
     * This function produces an utterance.
     *
     * @param probability the word-creation probability pWc
     * @param type the score-update type (s - score-based, u - usage-based)
     *
     * @return utterance the produced utterance
     */

    private Symbols produce_utterance(double probability,char type){
	double s=0.0;;
	double maxScore=0.0;
	int u=0;
	//search elements that match the topic's meaning specified by topicID
	for (int i=1;i<lexicon.size();i++){
	    if (type == 'u')
		if (lUse[0][i]>0)
		    s=(double)lUse[topicID][i]/(double)lUse[0][i];
		else s=0.0;
	    else s=lScore[topicID][i];
	    if (s>maxScore){
		maxScore=s;
		u=i;
	    }
	    else if (s==maxScore && s>0.0 && Math.random()>0.5)
		u=i;
	}
	//if we found an utterance u ...we're happy
	if (u>0){
	    utterance=(Symbols)lexicon.get(u);
	    utteranceID=u;
	}
	else if (Math.random()<= probability && lexicon.size()<
		 (maxNSymbols-1)){
	    //otherwise invent a new word
	    boolean exists=true;
	    while (exists){
		exists=false;
		utterance=new Symbols(iSymbols);iSymbols++;
		for (int i=1;i<lexicon.size() && !exists;i++)
		    if (utterance.getForm().
			equals(((Symbols)lexicon.get(i)).getForm()))
			exists=true;
	    }
	    utteranceID=lexicon.size();
	    if (type == 'u'){
		lUse[topicID][lexicon.size()]=0;
	    }
	    else lScore[topicID][lexicon.size()]=0.01;
	    lexicon.add(utterance);
	}
	else if (lexicon.size()>=maxNSymbols-1) forget(type);
	return utterance;
    }

    /**
     * This function produces an utterance for the speaker
     *
     * @param probability The word-creation probability
     * @param type The way the scores are updated ('s' for score-based, 'u' for usage-based)
     *
     * @return utterance representation of the utterance
     */
    public Symbols speak(double probability,char type){
	utterance = null;
	utteranceID=0;
	if (TOPIC != null)
	    produce_utterance(probability,type);
	return utterance;
    }

    /**
     * This function processes the interpretation of the hearer for the 
     * observational game.
     *
     * @param u The Symbols representation of the speaker's utterance
     * @return Symbols representation of the hearer's utterance (interpretatation)
     */
    public Symbols hear(Symbols u,char type){//obs. game
	utterance = null;
	utteranceID=0;
	boolean found = false;
	double s=0.0;
	if (TOPIC != null && u != null){
	    for (int i=1;i<lexicon.size() && !found;i++){
		if ((u.getForm()).equals(((Symbols)lexicon.get(i)).getForm())){
		    if (type=='s') s=lScore[topicID][i];
		    else s=(double)lUse[topicID][i];
		    if (s>0.0){
			utterance=(Symbols)lexicon.get(i);
			utteranceID=i;
		    }
		    found = true;
		}
	    }
	}
	gamesPlayed++;
	return utterance;
    }

    /**
     * This function processes the interpretation of the hearer for the 
     * guessing and selfish game.
     *
     * @param u The Symbols representation of the speaker's utterance
     * @param utype The way scores are adapted ('s' for score-based, 'u' for usage-based)
     * @return Symbols representation of the hearer's utterance (interpretatation)
     */

    public Symbols guess(Symbols u,char utype){
	utterance = null;
	utteranceID=0;
	boolean found = false;
	double maxScore=0.0;
	double s;
	topic=-1;
	topicID=0;
	TOPIC=null;
	if (u != null){
	    for (int i=1;i<lexicon.size() && !found;i++){
		if ((u.getForm()).equals(((Symbols)lexicon.get(i)).getForm())){
		    for (int j=0;j<cxt.categoryLength();j++){
			if (cxt.distinctive[j] && cxt.foa[j]){
			    if (utype=='s'){
				if (lScore[cxt.getCategory(j)][i]>maxScore){
				    maxScore=lScore[cxt.getCategory(j)][i];
				    topicID=cxt.getCategory(j);
				    topic=j;
				}
				else if (lScore[cxt.getCategory(j)][i]==maxScore &&
					 lScore[cxt.getCategory(j)][i]>0.0 && 
					 Math.random()>0.5){
				    maxScore=lScore[cxt.getCategory(j)][i];
				    topicID=cxt.getCategory(j);
				    topic=j;
				}
			    }
			    else{
				if (lUse[0][i]>0)
				    s=(double)lUse[cxt.getCategory(j)][i]/
					(double)lUse[0][i];
				else s=0.0;
				if (s>maxScore){
				    maxScore=s;
				    topicID=cxt.getCategory(j);
				    topic=j;
				}
				else if (s==maxScore && s>0.0&&Math.random()>0.5){
				    topicID=cxt.getCategory(j);
				    topic=j;
				}
			    }
			}
		    }
		    found = true;
		    if (topicID>0){
			utterance=(Symbols)lexicon.get(i);
			utteranceID=i;
			TOPIC=(Meaning)ontology.get(topicID);
		    }
		}
	    }
	}
	return utterance;
    }


    /** Function to implement the adaptation of association scores
     *
     * @param success boolean to indicate the success of a game.
    * @param utype score-update type
     * @param role the 'role' of the agent
     */

    private void updateScore(boolean success,char utype,char role){

	if (success){
	    if (utype=='s'){//score-based
		lScore[topicID][utteranceID]=eta*lScore[topicID][utteranceID]+
		    1.0-eta;
		for (int i=1;i<lexicon.size();i++)
		    if (i!=utteranceID)
			lScore[topicID][i]=eta*lScore[topicID][i];
		for (int i=1;i<nMeanings;i++)
		    if (i!=topicID)
			lScore[i][utteranceID]=eta*lScore[i][utteranceID];
	    }
	    else{//usage-based
		if (role == 's'){//update speaker (all games) and hearer (og and gg)
		    lUse[topicID][0]++;
		    lUse[0][utteranceID]++;
		    lUse[topicID][utteranceID]++;
		}
		else{//update hearer selfish game
		    for (int i=0;i<cxt.categoryLength();i++){
			if (cxt.distinctive[i] && cxt.foa[i]){
			    lUse[cxt.getCategory(i)][0]++;
			    lUse[0][utteranceID]++;
			    lUse[cxt.getCategory(i)][utteranceID]++;
			}
		    }
		}
	    }
	}
	else if (utype=='s') 
	    lScore[topicID][utteranceID]=eta*lScore[topicID][utteranceID];
	else{
	    lUse[topicID][0]++;
	    lUse[0][utteranceID]++;
	}
    }

    /** Function to check whether a category relates to an object in the
     * context
     *
     * @param id ID of the category to be checked.
     * @return true if category belongs to the context, false otherwise
     */
    protected boolean context_member(int id){

	for (int i=0;i<cxt.categoryLength();i++)
	    if (id==cxt.getCategory(i))
		return true;
	return false;
    }

    /** Update function for the selfish games that are controlled by 
     * association scores rather than by a use counter
     */
    private void updateScoreSGII(){

	for (int i=0;i<ontology.size();i++)
	    if (context_member(i))
		lScore[i][utteranceID]=eta*lScore[i][utteranceID]+1.0-eta;
	    else lScore[i][utteranceID]=eta*lScore[i][utteranceID];

	for (int i=0;i<cxt.categoryLength();i++){
	    if (cxt.distinctive[i] && cxt.foa[i]){
		for (int j=0;j<lexicon.size();j++)
		    if (j!=utteranceID)
			lScore[cxt.getCategory(i)][utteranceID]=
			    eta*lScore[cxt.getCategory(i)][utteranceID];
	    }
	}
    }

    /**
     * Adaptation function for the speaker
     *
     * @param u utterance of the hearer
     * @param T the topic
     * @param type the type of game
     * @param utype the score-update type
     */
    public void adaptLexiconSpeaker(Symbols u,int T,char type,char utype){
	eta=etaN;
	switch (type){
	case 'o':
	    if (u!=null)//LG success
		updateScore(true,utype,'s');
	    else if (TOPIC != null && utterance != null)
		updateScore(false,utype,'s');
	    break;
	case 'g':
	    if (u!=null && topic == T && utterance!=null && 
		TOPIC!=null)//LG success
		updateScore(true,utype,'s');
	    else if (TOPIC != null && utterance != null)
		updateScore(false,utype,'s');
	    break;
	case 's':
	    eta=etaS;
	    if (utterance != null)
		updateScore(true,utype,'s');//This 's' refers to speaker, not SG
	    break;
	case 'S':
	    eta=etaS;
	    if (utterance != null)
		;//updateScore(true);
	    break;
	}
    }
    /**
     * Adaptation function for the hearer
     *
     * @param u utterance of the speaker
     * @param T the topic
     * @param type the type of game
     */

    public void adaptLexiconHearer(Symbols u,int T,char type,char utype){
	eta=etaN;
	switch (type){
	case 'o':
	    if (utterance != null)//LG success
		updateScore(true,utype,'s');
	    else if (u != null && TOPIC != null){//Adopt form
		int wordIndex=lexicon.size();//get new index
		//in principle, the new word gets the index of current lexicon size...
		//unless the word already exists, the we only adopt the meaning
		for (int i=1;i<wordIndex;i++)
		    if ((u.getForm()).
			equals(((Symbols)lexicon.get(i)).getForm())){
			wordIndex=i;
		    }
		if (utype=='s')
		    lScore[topicID][wordIndex]=0.01;
		else{
		    lUse[topicID][wordIndex]=1;
		    lUse[topicID][0]++;
		    lUse[0][wordIndex]++;
		}
		if (wordIndex == lexicon.size()){
		    if (lexicon.size()<maxNSymbols-1){
			lexicon.add(new Symbols(iSymbols,u.getForm()));
			iSymbols++;
		    }
		    else{
			forget(utype);
			if (utype=='s')
			    lScore[topicID][wordIndex]=0.0;//reset initial score
			else{
			    lUse[topicID][wordIndex]=0;//reset initial usage
			    lUse[topicID][0]--;
			    lUse[0][wordIndex]--;
			}
		    }
		}
	    }
	    break;
	case 'g'://GG
	    boolean ready=false;
	    if (utterance != null && TOPIC!=null)
		if (T == topic){//LG success
		    updateScore(true,utype,'s');
		    ready=true;
		}
		else//mismatch in topic
		    updateScore(false,utype,'s');
	    if (!ready && u != null){
		if (cxt.distinctive[T] && cxt.foa[T]){//Adopt form
		    topicID=cxt.getCategory(T);
		    boolean exists=false;
		    for (int i=0;i<lexicon.size() && !exists;i++){
			if ((u.getForm()).equals(((Symbols)lexicon.get(i)).
						 getForm())){
			    exists=true;
			    if (utype=='s'){
				if (lScore[topicID][i]==0.0){
				//association does not exist
				    lScore[topicID][i]=0.01;
				}
			    }
			    else if (lUse[topicID][i]==0){
				lUse[topicID][i]++;
				lUse[topicID][0]++;
				lUse[0][i]++;
			    }
			}
		    }
		    if (!exists){
			if (lexicon.size()<maxNSymbols-1){
			    if (utype=='s')
				lScore[topicID][lexicon.size()]=0.01;
			    else{
				lUse[topicID][lexicon.size()]=1;
				lUse[topicID][0]++;
				lUse[0][lexicon.size()]++;
			    }
			    lexicon.add(new Symbols(iSymbols,u.getForm()));
			    iSymbols++;
			}
			else forget(utype);
		    }
		}
	    }
	    break;
	case 's'://SG
	    eta=etaS;
	    if (utterance!=null)
		if (utype=='u')
		    updateScore(true,utype,'h');
		else updateScoreSGII();
	    else if (u!=null){
		boolean exists=false;
		if (utype=='u'){//usage-based
		    for (int i=0;i<lexicon.size() && !exists;i++){
			if ((u.getForm()).equals(((Symbols)lexicon.get(i)).
						 getForm())){
			    for (int j=0;j<cxt.categoryLength();j++){
				if (cxt.distinctive[j] && cxt.foa[j]){
				    lUse[cxt.getCategory(j)][0]++;
				    lUse[0][i]++;
				    lUse[cxt.getCategory(j)][i]++;
				}
			    }
			    exists=true;
			}
		    }
		    if (!exists){
			if (lexicon.size()<maxNSymbols-1){
			    for (int j=0;j<cxt.categoryLength();j++){
				if (cxt.distinctive[j] && cxt.foa[j]){
				    lUse[cxt.getCategory(j)][0]++;
				    lUse[0][lexicon.size()]++;
				    lUse[cxt.getCategory(j)][lexicon.size()]=1;
				}
			    }
			    lexicon.add(new Symbols(iSymbols,u.getForm()));
			    iSymbols++;
			}
			else forget(utype);
		    }
		}
		else{//score-based
		    int saveUID=-1;
		    for (int i=0;i<lexicon.size() && !exists;i++){
			if ((u.getForm()).equals(((Symbols)lexicon.get(i)).
						 getForm())){
			    saveUID=utteranceID;
			    utteranceID=i;
			    exists=true;
			}
		    }
		    if (!exists){
			if (lexicon.size()<maxNSymbols-1){
			    saveUID=utteranceID;
			    utteranceID=lexicon.size();
			    lexicon.add(new Symbols(iSymbols,u.getForm()));
			    iSymbols++;
			}
			else forget(utype);
		    }
		    updateScoreSGII();
		    utteranceID=saveUID;
		}
	    }
	    break;
	}
    }

    /**
     * This function is not implemented...
     */
    public void playDGame(char type,char uType,boolean b){}
    /**
     * This function lets the agent play a discrimination game
     *
     * @param type not used
     * @param uType type of method with prototypes are shifted (default: centre-of-mass)
     * @param n language game number
     * @param adapt wether or not the learning is on or off.
     */
    public void playDGame(char type,char uType,int n,boolean adapt){
	lg=n;
	TOPIC=null;
	if (cxt == null) System.out.println("context null");
	if (ontology == null) System.out.println("ontol null");
	if (ontology.size()>=maxMeanings-1){
	    if (lUse!=null)
		forget('u');
	    else forget('s');
	}
	topicID=discriminationGame.playGame(cxt,topic,adapt);
	if (topicID>=0){
	    TOPIC=(Meaning)ontology.get(topicID);
	    TOPIC.shift_prototype(cxt.featureVector[topic],uType);
	    nrDGSuccess++;
	}
	DG++;
    }
    /**
     * This function implements the merging of meanings.
     *
     * @param dim number of dimensions
     * @param type score-update type
     *
     */

    public void merge(int dim,char type){

	double threshold=Math.sqrt((double)dim*thresholdPType*thresholdPType);

	for (int i=1;i<ontology.size()-1;i++)
	    for (int j=i+1;j<ontology.size();j++){
		if (Utils.distance(((Meaning)ontology.get(i)).getPrototype(),
				  ((Meaning)ontology.get(j)).getPrototype())<
		    threshold){
		    ((Meaning)ontology.get(i)).merge((Meaning)ontology.get(j));
		    merge(type,i,j);
		    j--;
		}
	    }

    }


    /**
     * @return DS rate of discriminative success in the current language game
     */
    public double getDS(){
	double retval=0.0;
	if (DG>0) retval=(double)nrDGSuccess/(double)DG;
	nrDGSuccess=0;
	DG=0;
	return retval;
    }

    /**
     * prints the final language game.
     */
    public void print(){
	System.out.println("A"+id);
	cxt.print(ontology);
	if (TOPIC!=null) System.out.println("Topic=fv["+topic+"] -> M"+TOPIC);
	else System.out.println("Topic=null");
	if (utterance!=null) System.out.println("utterance="+utterance);
	else System.out.println("utterance=null");
    }

    /** 
     * Constructs a string that can be written to the logfile
     */
    public String getGame(){
	String mStr,uStr,dStr;

	if (TOPIC!=null)
	    mStr=new String(TOPIC.toString());
	else
	    mStr=mString;

	if (utterance!=null)
	    uStr=utterance.getForm();
	else uStr = new String("*");

	if (topic>=0)
	    dStr = new String(cxt.getDString(topic));
	else dStr = new String("-1");

	return new String(String.valueOf(id)+" "+dStr+" "+mStr+" "+uStr);
    }

    /**
     * This function prints the ontology and lexicon of the agent to a file
     *
     * @param outfile The file to which the data is written
     * @param type score-update type
     */
    public void print(PrintWriter outfile,char type){

	outfile.println("A"+id+": nMeanings="+(ontology.size()-1)+
			" nSymbols="+(lexicon.size()-1));
	outfile.print("M F");
	for (int i=0;i<lexicon.size();i++)
	    outfile.print(" "+((Symbols)lexicon.get(i)).getForm());
	outfile.println();
	for (int j=0;j<ontology.size();j++){
	    ((Meaning)ontology.get(j)).print(outfile);
	    for (int i=0;i<lexicon.size();i++)
		if (type=='u')
		    outfile.print(" "+lUse[j][i]);
		else outfile.print(" "+lScore[j][i]);
	    outfile.println();
	}
	outfile.flush();
    }

    /**
     * prints the score of string u to the outfile
     */

    public void printScore(PrintWriter outfile,char type,final String u,int lg){

	outfile.print(lg);
	double x;
	for (int i=1;i<lexicon.size();i++)
	    if (u.equals(((Symbols)lexicon.get(i)).getForm()))
		for (int j=0;j<maxMeanings;j++)
		    if (type=='u'){
			if (lUse[j][0]>0 && lUse[j][i]>0){
			    x=(double)lUse[j][i]/(double)lUse[j][0];
			    outfile.print(" "+((Meaning)ontology.get(j)).getID()+": "+
					  Utils.doubleString(x,5)+" ");
			}
		    }
		    else if (lScore[j][i]>0.0)
			outfile.print(" "+((Meaning)ontology.get(j)).getID()+": "+
				      Utils.doubleString(lScore[j][i],5));
	outfile.println();
	outfile.flush();

    }

    /** This function is used to produce a bag of words that are used by the 
     * entire population
     */
    public void getWords(List bag){

	for (int i=1;i<lexicon.size();i++)
	    if (!bag.contains(((Symbols)lexicon.get(i)).getForm()))
		bag.add(new String(((Symbols)lexicon.get(i)).getForm()));
    }
    /**
     * Used to form the lexicon for the UI
     *
     * @return words all the words in the lexicon
     */

    public String [] getWords(){
	String [] retval = new String [lexicon.size()-1];
	System.out.println("lexiconsize="+lexicon.size());
	for (int i=1;i<lexicon.size();i++)
	    retval[i-1]=((Symbols)lexicon.get(i)).getForm();
	return retval;
    }

    /**
     * @return dimension the dimension of the conceptual space
     */
    public int getDim(){
	return dimension;
    }

    /** This function is used to find the meaning that best fits a given 
     * word.
     */
    public String getMeaning(String word,char type){
	int maxUse=0;
	double maxScore=0.0;
	int m=-1;
	for (int i=1;i<lexicon.size();i++)
	    if (word.equals(((Symbols)lexicon.get(i)).getForm())){
		//System.out.println("found word");
		m=-1;
		for (int j=1;j<ontology.size();j++)
		    if (type=='u'){
			if (lUse[j][i]>=maxUse){
			    maxUse=lUse[j][i];
			    m=j;
			}
		    }
		    else{
			if (lScore[j][i]>=maxScore){
			    maxScore=lScore[j][i];
			    m=j;
			}
		    }
		//System.out.println("found meaning");
		if (type=='u')
		    return new String(((Meaning)ontology.get(m)).string()
				      +" : "+lUse[m][i]);
		else{
		    String X=new String(String.valueOf(lScore[m][i]));
		    int x=Math.min(X.length(),4);
		    return new String(((Meaning)ontology.get(m)).string()
				      +" : "+X.substring(0,x));
		}
	    }
	return new String("null");
    }

    /**
     * Function used to fill the matrix constructed in getMeanings(String w,char t)
     *
     * @param ret the matrix to be filled
     * @param n the index of the word
     * @param t the score-update type
     */
    private void fill(double [][] ret,int n,char t){
	double w=0.0;
	double [] m=null;
	int k=0;
	for (int i=1;i<ontology.size();i++){
	    if (t=='u'){
		if (lUse[i][n]>0)
		    w=(double)lUse[i][n]/(double)lUse[0][n];
	    }
	    else w=lScore[i][n];
	    if (w>0.0){
		m=((Meaning)ontology.get(i)).getPrototype();
		for (int j=0;j<m.length;j++){
		    k=Utils.index(m[j]);
		    if (w>ret[k][j]) ret[k][j]=w;
		}
	    }
	}
    }


    /**
     * Function used to construct a matrix representation of meanings of word w.
     * This function is used for the lexicon display in the UI.
     *
     * @param w the word under consideration
     * @param t score-update type
     *
     * @return matrix
     */
     public double [][] getMeanings(final String w,char t){
	double [][] retval = new double [10][dimension];
	for (int i=1;i<lexicon.size();i++){
	    if (w.equals(((Symbols)lexicon.get(i)).getForm())){
		fill(retval,i,t);
		return retval;
	    }
	}
	return retval;
    }



     /**
     * Function for an artificially created lexicon.
     *
     * The lexicon created is a one-to-one lexicon with no synonymy nor homonymy.
     */

    public void createArtificialLexicon(final int nMeanings){
	Symbols S;
	lUse=new int[nMeanings+1][nMeanings+1];
	for (int i=1;i<=nMeanings;i++){
	    boolean added=false;
	    while (!added){
		S=new Symbols(i);
		boolean exists=false;
		for (int j=1;j<i && !exists;j++){
		    if ((S.getForm()).equals(((Symbols)lexicon.get(j)).getForm()))
			exists=true;
		}
		if (!exists){
		    lexicon.add(S);
		    added=true;
		}
	    }
	    lUse[i][i]=1;
	    lUse[i][0]=1;
	    lUse[0][i]=1;
	}
    }

    /**
    * Function for an artificially created lexicon.
      * The ontology is just an integer. Meanings are created in order not to conflict other procedures.
     */

    public void createArtificialOntology(final int nMeanings){
	if (lexicon.size()<=1) lUse=new int[nMeanings+1][nMeanings+1];
	for (int i=1;i<=nMeanings;i++)
	    ontology.add(new Meaning(i));
    }

    /**
     * Function for an artificially created lexicon.
     * This function calculates the production coherence for two agents.
     */

    public double calcProductionCoherence(HolisticAgent hearer,int nMeanings){
	Symbols sp,he;
	int sim=0;
	for (topicID=1;topicID<ontology.size();topicID++){
	    utteranceID=0;
	    sp=produce_utterance(0.0,'u');
	    hearer.setTOPICID(topicID);
	    he=hearer.produce_utterance(0.0,'u');
	    if (he!=null && (sp.getForm()).equals(he.getForm()))
		sim++;
	}
	return (double)sim / (double)nMeanings;
    }
    /**
    * Function for an artificially created lexicon.
    */
    private String bestMatch(int n){
	int maxUse=0;
	boolean singleBest=false;
	int best=-1;
	for (int i=1;i<lexicon.size();i++){
	    if (lUse[n][i]>maxUse){
		maxUse=lUse[n][i];
		singleBest=true;
		best=i;
	    }
	    else if (lUse[n][i]==maxUse){
		singleBest=false;
	    }
	}
	if (singleBest) return ((Symbols)lexicon.get(best)).getForm();
	return null;
    }

    /**
    * Function for an artificially created lexicon.
      * This function calculates the interpretation coherence.
     */

    public double calcInterpretationCoherence(final HolisticAgent hearer,int nMeanings){
	String sp,he;
	int sim=0;
	for (int i=1;i<ontology.size();i++){
	    sp=bestMatch(i);
	    he=hearer.bestMatch(i);
	    if (sp!=null && he!=null && sp.equals(he)) sim++;
	}
	return (double)sim / (double)nMeanings;
    }
}
