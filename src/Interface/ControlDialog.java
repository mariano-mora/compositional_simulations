package Interface;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import Util.Parameters;
import Util.Parameters.AgentType;

/**
 * ControlDialog implements the frame for controlling the simulations.
 *
 * Copyright (c) 2004, Paul Vogt
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class ControlDialog extends JOptionPane{

    private JOptionPane optionPane;
    private int nrRuns = 100;
    private int nrAgents = 2;
    private int stepSize=1;
    private int agentType=2;
    private boolean stop = false;
    private boolean run = false;
    private boolean quit = false;
    private boolean newGame = false;
    private boolean step=false;
    private boolean printGame=false;
    private String typedText1 = null;
    private String typedTextStSize=null;
    private String typedText2 = null;
    private String typedText3 = null;
    private String typedText4 = null;
    private String typedTextFOA = null;
    private String typedText5 = null;
    private String typedText5a = null;
    private String typedText5b = null;
    private String typedText5c = null;
    private String typedText5d = null;
    private String typedText6b = null;
    private String typedText6c = null;
    private String typedText8 = null;
    private String typedText9 = null;
    private String typedText10 = null;
    private String typedText10a = null;
    private String typedTextBottleneck = null;
    private String typedTextWorkDir = null;
    private String typedTextAlphabetSize = null;
    private String typedTextPreLing = null;
    private String typedTextMemMeanings = null;
    private String typedTextMemWords = null;
    private double noiseCxt=0.0;
    private double probability=1.0;
    private Parameters parameters = null;
    private int maxCxtSize=4;
    private String fileName=new String("test");
    private boolean logFile=false;
    private boolean [] features;
    private boolean ILM=false;
    private boolean saveLexicon=false;
    private String lexFile=new String("testLex");
    private int nIterations=1;
    private char gameType='o';
    private boolean forget=false;
    private int forgetRate=30;
    private boolean incrementalForgetting=true;
    private boolean ilmIncremental=false;
    private int popGrowth=2;
    private int maxAgents=20;
    private boolean variation=false;
    private double ogProbability=0.0;
    private double ggProbability=1.0;
    private boolean sToS=true;
    private double pAdultSpeaker=1.0;
    private double pAdultHearer=0.0;
    private double etaN=0.9;
    private double etaS=0.9;
    private char updatePType='a';
    private char updateScore='s';
    private boolean updateScoreBool=true;
    private boolean adaptation=true;
    private boolean fixedColours=true;
    private int foa=4;

    private int bottleneck=0;
    private String workDir=new String("null");
    private boolean testPop=true;
    private boolean printScores=false;
    private int alphabetSize=26;
    private int preLing=0;
    private int memMeanings=1000;
    private int memWords=1000;

   private final JTextField textField1 = new JTextField(Integer.
							 toString(nrRuns),3);
   private final JTextField textFieldStSize = new JTextField(Integer.
							 toString(stepSize),3);
    private final JTextField textField2 = new JTextField(Integer.
							 toString(nrAgents),3);
    private final JTextField textField4 =
	new JTextField(Integer.toString(maxCxtSize),3);
    private final JLabel msgAType = new JLabel("agentType",
					       SwingConstants.RIGHT);
    private final String [] aTypes = {"Holistic - flat", "Holistic - hierarchical", "Compositional"};
    private final JComboBox aTypeBox = new JComboBox(aTypes);
    private final Box agentTypeBox = new Box(BoxLayout.X_AXIS);
    private final JFileChooser fc = new JFileChooser();
    
    private final Dimension tDim = new Dimension(150,20);
    private final Dimension tDim1 = new Dimension(100,20);
    private final Dimension tDim2 = new Dimension(75,20);
    private final Dimension tDim3 = new Dimension(50,20);
    
    private final JLabel msg1 = new JLabel("nGames",
					   SwingConstants.RIGHT);
    private final Box gameBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel msgStSize = new JLabel("stepSize",
					   SwingConstants.RIGHT);
    private final Box stepSizeBox = new Box(BoxLayout.X_AXIS);

    private final JLabel msg2 = new JLabel("popSize",
					   SwingConstants.RIGHT);
    private final Box popSizeBox = new Box(BoxLayout.X_AXIS);

    
    private final JLabel msg4 = new JLabel("cxtSize",
					   SwingConstants.RIGHT);
    private final Box cxtBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel msg3 = new JLabel("pNoise",
					   SwingConstants.RIGHT);
    private final Box noiseBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel msg5 = new JLabel("pWC",
					   SwingConstants.RIGHT);
    private final Box probBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel msg5a = new JLabel("pAdultS",
					    SwingConstants.RIGHT);
    private final Box adultSBox = new Box(BoxLayout.X_AXIS);

    private final JLabel msg5b = new JLabel("pAdultH",
					    SwingConstants.RIGHT);
    private final Box adultHBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg5c = new JLabel("etaN",SwingConstants.RIGHT);
    private final Box etaNBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg5d = new JLabel("etaS",SwingConstants.RIGHT);
    private final Box etaSBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel msg8 = new JLabel("ILM",
					   SwingConstants.RIGHT);
    private final Box ilmBox = new Box(BoxLayout.X_AXIS);

    private final JTextField textField3 = 
	new JTextField(Double.toString(noiseCxt),8);
    private final JTextField textField5 =
	new JTextField(Double.toString(probability),8);
    private final JTextField textField5a = 
	new JTextField(Double.toString(pAdultSpeaker),8);
    private final JTextField textField5b = 
	new JTextField(Double.toString(pAdultHearer),8);
    private final JCheckBox checkBox2 = new JCheckBox("",ILM);
    private final JLabel msg8a = new JLabel("nIter");
    private final Box nIterBox = new Box(BoxLayout.X_AXIS);
    private final JTextField textField8 =
	new JTextField(Integer.toString(nIterations),4);
    
    
    private final JTextField textField5c =
	new JTextField(Double.toString(etaN),8);
    private final JTextField textField5d =
	new JTextField(Double.toString(etaS),8);
    
    private final JLabel msg7 = new JLabel("Select features: R");
    private final JLabel msg71 = new JLabel(" G");
    private final JLabel msg72 = new JLabel(" B");
    private final JLabel msg73 = new JLabel(" S");
    private final JLabel msg74 = new JLabel(" X");
    private final JLabel msg75 = new JLabel(" Y");
    
    private JCheckBox fv0 = new JCheckBox();
    private JCheckBox fv1 = new JCheckBox();
    private JCheckBox fv2 = new JCheckBox();
    private JCheckBox fv3 = new JCheckBox();
    private JCheckBox fv4 = new JCheckBox();
    private JCheckBox fv5 = new JCheckBox();
    
    
    private final Box featureBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg6a = new JLabel("varGames",SwingConstants.RIGHT);
    private final JCheckBox checkBox6a = new JCheckBox("",variation);
    private final Box varBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg6b = new JLabel("pOG",SwingConstants.RIGHT);
    private final JTextField textField6b = 
	new JTextField(Double.toString(ogProbability),8);
    private final Box pOGBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg6c = new JLabel("pGG",SwingConstants.RIGHT);
    private final JTextField textField6c = 
	new JTextField(Double.toString(ggProbability),8);
    private final Box pGGBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg6d = new JLabel("adaptSG",SwingConstants.RIGHT);
    private final JCheckBox checkBox6d = new JCheckBox("",adaptation);
    private final Box adaptSGBox = new Box(BoxLayout.X_AXIS);
    private final JLabel uJLType = new JLabel("uPType",SwingConstants.RIGHT);
    private final String[] uTypes = {"centre-of-mass",
				     "simulated annealing","walk","none"};
    private final JComboBox uTypeBox = new JComboBox(uTypes);
    
    private final Box dgBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel fColours = new JLabel("fCol",
					       SwingConstants.RIGHT);
    private final JCheckBox checkFCol = new JCheckBox("",fixedColours);
    private final Box colorBox = new Box(BoxLayout.X_AXIS);
    private final JLabel uScore = new JLabel("uScore",
					     SwingConstants.RIGHT);
    private final JCheckBox checkUScore = new JCheckBox("",updateScoreBool);
    private final Box scoreBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg6 = new JLabel("log",
					   SwingConstants.RIGHT);
    private final JCheckBox checkBox1 = new JCheckBox("",logFile);
    
    private final JLabel msg6f = new JLabel("lex",
					    SwingConstants.RIGHT);
    private final Box lexBox = new Box(BoxLayout.X_AXIS);
    private final JCheckBox checkBox1a = new JCheckBox("",saveLexicon);
    private final JLabel msgPrint = new JLabel("printGame",
					   SwingConstants.RIGHT);
    private final Box writeBox = new Box(BoxLayout.X_AXIS);
    private final JCheckBox checkBoxPrint = new JCheckBox("",printGame);

    
    private final Box logBox = new Box(BoxLayout.X_AXIS);
    

    private final JLabel msg9 = new JLabel("S2S",SwingConstants.RIGHT);
    private final JCheckBox checkbox3 = new JCheckBox("",sToS);
    private final Box s2sBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg10 = new JLabel("incrPop",
					    SwingConstants.RIGHT);
    private final JCheckBox checkbox4 = new JCheckBox("",ilmIncremental);
    private final Box incrBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg10a = new JLabel("growth",SwingConstants.RIGHT);
    private final JTextField textField10 =
	new JTextField(Integer.toString(popGrowth),5);
    private final Box growthBox = new Box(BoxLayout.X_AXIS);
    private final JLabel msg10b = new JLabel("maxAgents",SwingConstants.RIGHT);
    private final JTextField textField10a =
	new JTextField(Integer.toString(maxAgents),5);
    private final Box maxAgentsBox = new Box(BoxLayout.X_AXIS);
    
    private final JLabel gameTypeJL = new JLabel("gameType",SwingConstants.RIGHT);
    private final String[] games = {"observational game",
				    "guessing game","selfish game"};
    private final JComboBox comboBox = new JComboBox(games);

    private final JLabel msgBottleneck = new JLabel("trainingSetSize");
    private final JTextField textFieldBottleneck = new JTextField(Integer.toString(bottleneck),4);
    private final Box bottleneckBox= new Box(BoxLayout.X_AXIS);

    private final JLabel msgWorkDir = new JLabel("workDir");
    private final JTextField textFieldWorkDir = new JTextField(workDir,1);
    private final Box workDirBox = new Box(BoxLayout.X_AXIS);

    private final JLabel msgTestPop = new JLabel("testPop");
    private final JCheckBox checkTestPop = new JCheckBox("",testPop);
    private final Box testPopBox = new Box(BoxLayout.X_AXIS);

    private final JLabel msgPrintScores = new JLabel("printScores");
    private final JCheckBox checkPrintScores = new JCheckBox("",printScores);
    private final Box printScoresBox = new Box(BoxLayout.X_AXIS);

    private final JLabel msgAlphabetSize = new JLabel("alphabetSize");
    private final JTextField textFieldAlphabetSize = new JTextField(Integer.toString(alphabetSize),4);
    private final Box alphabetSizeBox= new Box(BoxLayout.X_AXIS);

    private final JLabel msgPreLing = new JLabel("preLing");
    private final JTextField textFieldPreLing = new JTextField(Integer.toString(preLing),4);
    private final Box preLingBox= new Box(BoxLayout.X_AXIS);

    private final JLabel msgMemMeanings = new JLabel("memMeanings");
    private final JTextField textFieldMemMeanings = new JTextField(Integer.toString(memMeanings),4);
    private final Box memMeaningsBox = new Box(BoxLayout.X_AXIS);

    private final JLabel msgMemWords = new JLabel("memWords");
    private final JTextField textFieldMemWords = new JTextField(Integer.toString(memWords),4);
    private final Box memWordsBox = new Box(BoxLayout.X_AXIS);

    private final Box gametypeBox = new Box(BoxLayout.X_AXIS);
    private final Box messageBox = new Box(BoxLayout.Y_AXIS);
    private final Box messageBox2 = new Box(BoxLayout.X_AXIS);
    private final Box msgBox = new Box(BoxLayout.Y_AXIS);
    private final Box editBox = new Box(BoxLayout.Y_AXIS);

    private JTable lexiconTable = new JTable();
    private JScrollPane scrollMessageBox = new JScrollPane();
    private final JButton btn0 = new JButton("New");
    private final JButton btn1 = new JButton("Run");
    private final JButton btn2 = new JButton("Stop");
    private final JButton btn3 = new JButton("Show");
    private final JButton btn5 = new JButton("Step");
    
    private Object[] options = {btn1,btn5,btn0,btn2,btn3};

    final JLabel foaLabel = new JLabel("foa",
				       SwingConstants.RIGHT);
    private final Box foaBox = new Box(BoxLayout.X_AXIS);
    final JTextField foaTextField = new JTextField(Integer.toString(foa),3);

    /**
     * This constructor constructs and handles the manipulation of the control
     * frame. The value of the fields are stored in the Parameters class.
     *
     * @param param The parameters of the control frame.
     */

    public ControlDialog(final Parameters param){
	//super(tFrame,true);
	//setTitle("Control dialog");

	parameters = param;
	nrRuns=parameters.getNInteractions();
	nrAgents=parameters.getNAgents();
	noiseCxt=parameters.getNoiseCxt();
	fileName=parameters.getLogFileName();
	agentType=parameters.getAgentType().getValue();
	logFile=parameters.getLogFile();
	probability=parameters.getCreationProb();
	features=parameters.getFeatures();
	ILM=parameters.getILM();
	printGame=parameters.getPrintGame();
	nIterations=parameters.getNIterations();
	gameType=parameters.getGameType();
	forget=parameters.getForget();
	forgetRate=parameters.getForgetRate();
	incrementalForgetting=parameters.getIncrementalForgetting();
	ilmIncremental=parameters.ilmIncremental();
	popGrowth=parameters.getPopGrowth();
	maxAgents=parameters.getMaxAgents();
	variation=parameters.getVariation();
	ogProbability=parameters.getOGProbability();
	ggProbability=parameters.getGGProbability();
	sToS=parameters.getSpeakerToSpeaker();
	pAdultSpeaker=parameters.getPAdultSpeaker();
	pAdultHearer=parameters.getPAdultHearer();
	etaN=parameters.getEtaN();
	etaS=parameters.getEtaS();
	saveLexicon=parameters.getSaveLexicon();
	adaptation=parameters.getAdaptation();
	fixedColours=parameters.getFixedColours();
	foa=parameters.getFOA();
	updatePType=parameters.getUpdatePType();
	updateScore=parameters.getUpdateScore();
	maxCxtSize=parameters.getMaxCxtSize();
	if (updateScore=='s') updateScoreBool=true;
	else updateScoreBool=false;
	bottleneck=parameters.trainingSet();
	workDir=parameters.getDir();
	testPop=parameters.getTestPopulation();
	printScores=parameters.getPrintScore();
	alphabetSize=parameters.getAlphabetSize();
	preLing=parameters.getNNoLearning();
	memMeanings=parameters.getNMeanings();
	memWords=parameters.getNSymbols();

	update();

	gameTypeJL.setMaximumSize(new Dimension(100,20));
	gameTypeJL.setToolTipText("Selects the type of language game that is played.");
	msg1.setMaximumSize(tDim2);
	msg1.setToolTipText("Sets the number of games per iteration.");
	textField1.setMaximumSize(tDim2);
	gameBox.add(msg1);
	gameBox.add(textField1);



	msgAType.setMaximumSize(new Dimension(100,20));
	msgAType.setToolTipText("Choose the type of agent.");

	aTypeBox.setSelectedIndex(agentType);
	aTypeBox.setMaximumSize(new Dimension(200,20));

	agentTypeBox.add(msgAType);
	agentTypeBox.add(aTypeBox);

	msgStSize.setMaximumSize(tDim2);
	textFieldStSize.setMaximumSize(tDim2);
	msgStSize.setToolTipText("Sets the step-size with which the simulation proceeds.");
	stepSizeBox.add(msgStSize);
	stepSizeBox.add(textFieldStSize);


	msg2.setMaximumSize(tDim2);
	msg2.setToolTipText("Sets the initial population size.");
	textField2.setMaximumSize(tDim2);
	popSizeBox.add(msg2);
	popSizeBox.add(textField2);

	msg4.setMaximumSize(tDim2);
	msg4.setToolTipText("Sets the context size.");
	textField4.setMaximumSize(tDim2);
	cxtBox.add(msg4);
	cxtBox.add(textField4);

	msg3.setMaximumSize(tDim2);
	msg3.setToolTipText("Sets the perceptual noise level.");
	textField3.setMaximumSize(tDim2);
	noiseBox.add(msg3);
	noiseBox.add(textField3);


	msg5.setMaximumSize(tDim2);
	msg5.setToolTipText("Sets the word-creation probability.");
	textField5.setMaximumSize(tDim2);
	probBox.add(msg5);
	probBox.add(textField5);

	msg8.setMaximumSize(tDim2);
	msg8.setToolTipText("Sets the iterated learning model on.");
	msg8a.setMaximumSize(tDim3);
	msg8a.setToolTipText("Specifies the number of iterations.");

	msg5a.setMaximumSize(tDim2);
	msg5b.setMaximumSize(tDim2);
	msg5a.setToolTipText("Sets the probability with which speakers are selected from the adult population.");
	msg5b.setToolTipText("Sets the probability with which hearers are selected from the adult population.");
	textField5a.setMaximumSize(tDim2);
	textField5b.setMaximumSize(tDim2);
	adultSBox.add(msg5a);
	adultSBox.add(textField5a);
	adultHBox.add(msg5b);
	adultHBox.add(textField5b);


	msg5c.setMaximumSize(tDim2);
	msg5c.setToolTipText("Sets the learning rate eta for the observational and guessing games.");
	msg5d.setMaximumSize(tDim2);
	msg5d.setToolTipText("Sets the learning rate eta for the selfish games.");

	textField5c.setMaximumSize(tDim2);
	textField5d.setMaximumSize(tDim2);
	etaNBox.add(msg5c);
	etaNBox.add(textField5c);
	etaSBox.add(msg5d);
	etaSBox.add(textField5d);

	fv0 = new JCheckBox("",features[0]);
	fv1 = new JCheckBox("",features[1]);
	fv2 = new JCheckBox("",features[2]);
	fv3 = new JCheckBox("",features[3]);
	fv4 = new JCheckBox("",features[4]);
	fv5 = new JCheckBox("",features[5]);

	msg7.setToolTipText("Sets which object features are perceived by the agents. R is Red");
	featureBox.add(msg7);
	featureBox.add(fv0);
	msg71.setToolTipText("Green");
	msg72.setToolTipText("Blue");
	msg73.setToolTipText("Shape");
	msg74.setToolTipText("X-coordinate");
	msg75.setToolTipText("Y-coordinate");
	featureBox.add(msg71);
	featureBox.add(fv1);
	featureBox.add(msg72);
	featureBox.add(fv2);
	featureBox.add(msg73);
	featureBox.add(fv3);
	featureBox.add(msg74);
	featureBox.add(fv4);
	featureBox.add(msg75);
	featureBox.add(fv5);

	msg6a.setMaximumSize(tDim2);
	msg6b.setMaximumSize(tDim2);
	msg6c.setMaximumSize(tDim2);
	msg6d.setMaximumSize(tDim2);
	msgPrint.setMaximumSize(tDim2);
	msg6a.setToolTipText("Makes that the agents vary between game types.");
	msg6b.setToolTipText("Specifies the chance the agents play an observational game.");
	msg6c.setToolTipText("Specifies the chance the agents play a guessing game.");
	msg6d.setToolTipText("Specifies whether or not score adaptation is switched on in the selfish game.");
	varBox.add(msg6a);
	varBox.add(checkBox6a);

	textField6b.setMaximumSize(tDim2);
	pOGBox.add(msg6b);
	pOGBox.add(textField6b);
	pGGBox.add(msg6c);
	textField6c.setMaximumSize(tDim2);
	pGGBox.add(textField6c);
	adaptSGBox.add(msg6d);
	adaptSGBox.add(checkBox6d);

	uJLType.setMaximumSize(tDim2);

	int select1=0;
	switch (updatePType){
	case 'a':
	    select1=0;
	    break;
	case 's':
	    select1=1;
	    break;
	case 'w':
	    select1=2;
	    break;
	}

	uTypeBox.setSelectedIndex(select1);
	uTypeBox.setMaximumSize(tDim);

	uJLType.setToolTipText("Specifies which mechanism is used to move prototypes.");
	dgBox.add(uJLType);
	dgBox.add(uTypeBox);

	foaLabel.setToolTipText("Sets the size of the focus of attention (should be smaller or equal to the context size.");
	foaLabel.setMaximumSize(tDim2);
	fColours.setMaximumSize(tDim2);
	fColours.setToolTipText("Specifies whether the colours are selected from a fixed set or are generated randomly.");
	uScore.setMaximumSize(tDim2);
	uScore.setToolTipText("Specifies whether scores are updated score-based or usage-based.");
	msg6.setMaximumSize(tDim2);
	msg6.setToolTipText("Indicates whether the statistics are logged");
	msg6f.setMaximumSize(tDim2);
	msg6f.setToolTipText("Indicates whether the lexicons are stored.");
	msgPrint.setToolTipText("Indicates whether extended game info is written to standard output.");
	foaBox.add(foaLabel);
	foaTextField.setMaximumSize(tDim2);
	foaBox.add(foaTextField);
	colorBox.add(fColours);
	colorBox.add(checkFCol);
	scoreBox.add(uScore);
	scoreBox.add(checkUScore);
	logBox.add(msg6);
	logBox.add(checkBox1);
	lexBox.add(msg6f);
	lexBox.add(checkBox1a);
	writeBox.add(msgPrint);
	writeBox.add(checkBoxPrint);

	ilmBox.add(msg8);
	ilmBox.add(checkBox2);
	nIterBox.add(msg8a);
	textField8.setMaximumSize(tDim2);
	nIterBox.add(textField8);

	msg9.setMaximumSize(tDim2);
	msg9.setToolTipText("If checked, no distinction between adults and learners is made during the first iteration when ILM is on.");
	msg10.setMaximumSize(tDim2);
	msg10.setToolTipText("Switches incremental population growth on/off.");
	msg10a.setMaximumSize(tDim2);
	msg10a.setToolTipText("Sets the population growth for incrPop.");
	msg10b.setMaximumSize(tDim1);
	msg10b.setToolTipText("Sets the maximum population size.");
	s2sBox.add(msg9);
	s2sBox.add(checkbox3);
	incrBox.add(msg10);
	incrBox.add(checkbox4);
	growthBox.add(msg10a);
	textField10.setMaximumSize(tDim2);
	growthBox.add(textField10);
	maxAgentsBox.add(msg10b);
	textField10a.setMaximumSize(tDim1);
	maxAgentsBox.add(textField10a);

	int select=0;
	switch (gameType){
	case 'o':
	    select=0;
	    break;
	case 'g':
	    select=1;
	    break;
	case 's':
	    select=2;
	    break;
	case 'S':
	    select=3;
	    break;
	}

	comboBox.setSelectedIndex(select);
	comboBox.setMaximumSize(tDim);

	gametypeBox.add(gameTypeJL);
	gametypeBox.add(comboBox);

	msgBottleneck.setMaximumSize(tDim1);
	msgBottleneck.setToolTipText("Sets the size of the subset of the world (or bottleck) during learning.");
	textFieldBottleneck.setMaximumSize(tDim2);
	bottleneckBox.add(msgBottleneck);
	bottleneckBox.add(textFieldBottleneck);

	msgWorkDir.setMaximumSize(tDim1);
	msgWorkDir.setToolTipText("Sets the working directory for printing the scores (if ticked).");
	textFieldWorkDir.setMaximumSize(tDim);
	workDirBox.add(msgWorkDir);
	workDirBox.add(textFieldWorkDir);

	msgTestPop.setMaximumSize(tDim2);
	msgTestPop.setToolTipText("When ticked, the population is tested after each iteration.");
	testPopBox.add(msgTestPop);
	testPopBox.add(checkTestPop);

	msgPrintScores.setMaximumSize(tDim2);
	msgPrintScores.setToolTipText("When ticked, the dynamics of some word-meaning scores are traced.");
	printScoresBox.add(msgPrintScores);
	printScoresBox.add(checkPrintScores);

	msgAlphabetSize.setMaximumSize(tDim1);
	msgAlphabetSize.setToolTipText("Sets the size of the alphabet used in compositional experiments.");
	textFieldAlphabetSize.setMaximumSize(tDim2);
	alphabetSizeBox.add(msgAlphabetSize);
	alphabetSizeBox.add(textFieldAlphabetSize);

	msgPreLing.setMaximumSize(tDim2);
	msgPreLing.setToolTipText("Sets the number of games played where learner only plays discrimination games.");
	textFieldPreLing.setMaximumSize(tDim2);
	preLingBox.add(msgPreLing);
	preLingBox.add(textFieldPreLing);

	msgMemMeanings.setMaximumSize(tDim1);
	msgMemMeanings.setToolTipText("Sets the size of the memory for meanings (holistic games only).");
	textFieldMemMeanings.setMaximumSize(tDim2);
	memMeaningsBox.add(msgMemMeanings);
	memMeaningsBox.add(textFieldMemMeanings);

	msgMemWords.setMaximumSize(tDim1);
	msgMemWords.setToolTipText("Sets the size of the  memory for words (holistic games only).");
	textFieldMemWords.setMaximumSize(tDim2);
	memWordsBox.add(msgMemWords);
	memWordsBox.add(textFieldMemWords);

	//	messageBox.createalStrut(50);
	messageBox.add(agentTypeBox);
	messageBox.add(gametypeBox);
	messageBox.add(dgBox);
	messageBox.add(featureBox);
	messageBox.add(popSizeBox);
	messageBox.add(gameBox);
	messageBox.add(stepSizeBox);
	//messageBox.add(ilmBox);
	messageBox.add(nIterBox);
	messageBox.add(adultSBox);
	messageBox.add(adultHBox);
	messageBox.add(bottleneckBox);
	messageBox.add(pOGBox);
	messageBox.add(pGGBox);
	messageBox.add(probBox);
	messageBox.add(alphabetSizeBox);
	messageBox.add(etaNBox);
	messageBox.add(etaSBox);
	messageBox.add(preLingBox);
	messageBox.add(memMeaningsBox);
	messageBox.add(memWordsBox);
	messageBox.add(cxtBox);
	messageBox.add(foaBox);
	messageBox.add(noiseBox);
	messageBox.add(growthBox);
	messageBox.add(maxAgentsBox);
	messageBox.add(workDirBox);
	messageBox.add(testPopBox);
	messageBox.add(printScoresBox);
	messageBox.add(s2sBox);
	messageBox.add(varBox);
	messageBox.add(scoreBox);
	messageBox.add(adaptSGBox);
	messageBox.add(colorBox);
	messageBox.add(incrBox);
	//messageBox.add(forgetBox);
	messageBox.add(logBox);
	messageBox.add(lexBox);
	messageBox.add(writeBox);

	//messageBox2.add(msgBox);
	//messageBox2.add(editBox);

	//	setMessage(messageBox);
	//setIcon(array2);

	scrollMessageBox = new JScrollPane(messageBox);
	scrollMessageBox.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMessageBox.setPreferredSize(new Dimension(250, 155));

	setMessage(scrollMessageBox);
	setMessageType(PLAIN_MESSAGE);
	setOptionType(YES_OPTION);
	setOptions(options);
	setInitialValue(btn1);

	btn0.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    actions(e);
		    parameters.setNewGame(true);
		}
	    });

	btn1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    actions(e);
		    parameters.setRun(true);
		}
	    });

	btn5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    actions(e);
		    parameters.setStep(true);
		}
	    });

	btn2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    parameters.setStop(true);
		}
	    });
	
	btn3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    parameters.setShowLexicon(true);
		}
	    });
	
    }

    private void update(){

	textField1.setText(String.valueOf(nrRuns));
	textFieldStSize.setText(String.valueOf(stepSize));
	textField2.setText(String.valueOf(nrAgents));
	textField4.setText(String.valueOf(maxCxtSize));
	textField3.setText(String.valueOf(noiseCxt));
	textField5.setText(String.valueOf(probability));
	textField5a.setText(String.valueOf(pAdultSpeaker));
	textField5b.setText(String.valueOf(pAdultHearer));
	textField5c.setText(String.valueOf(etaN));
	textField5d.setText(String.valueOf(etaS));
	textField6b.setText(String.valueOf(ogProbability));
	textField6c.setText(String.valueOf(ggProbability));
	textField8.setText(String.valueOf(nIterations));
	//aTypes.setText(String.valueOf(agentType));
	if (nIterations>1) ILM=true;
	else ILM=false;
	//	checkBox2.setSelected(ILM);
	checkBox6a.setSelected(variation);
	checkBox6d.setSelected(adaptation);
	checkFCol.setSelected(fixedColours);
	checkUScore.setSelected(updateScoreBool);
	checkBox1.setSelected(logFile);
        checkBox1a.setSelected(saveLexicon);
        checkbox3.setSelected(sToS);
	checkbox4.setSelected(ilmIncremental);
	checkBoxPrint.setSelected(printGame);
	textField10.setText(String.valueOf(popGrowth));
	textField10a.setText(String.valueOf(maxAgents));
	foaTextField.setText(String.valueOf(foa));

	textFieldBottleneck.setText(String.valueOf(bottleneck));
	textFieldWorkDir.setText(String.valueOf(workDir));
	checkTestPop.setSelected(testPop);
	checkPrintScores.setSelected(printScores);
	textFieldAlphabetSize.setText(String.valueOf(alphabetSize));
	textFieldPreLing.setText(String.valueOf(preLing));
	textFieldMemMeanings.setText(String.valueOf(memMeanings));
	textFieldMemWords.setText(String.valueOf(memWords));
    }


    public void actions(ActionEvent e){
		    typedText1=textField1.getText();
		    if (typedText1!=null){
			try{
			    nrRuns = Integer.parseInt(typedText1);
			}
			catch(final NumberFormatException pNumberFormatException){
			    showMessageDialog(ControlDialog.this,
				     "Nr. of games should be an integer",
				     "OK",
				     ERROR_MESSAGE);
			    return;
			}
			if (nrRuns<=0){
			    showMessageDialog(
				  ControlDialog.this,
				  "Sorry, Nr of runs not a valid number",
				  "try again",
				  ERROR_MESSAGE);
			    typedText1=null;
			    return;
			}
		    }
		    parameters.setNInteractions(nrRuns);
		    typedTextStSize=textFieldStSize.getText();
		    if (typedTextStSize!=null){
			try{
			    stepSize = Integer.parseInt(typedTextStSize);
			}
			catch(final NumberFormatException pNumberFormatException){
			    showMessageDialog(ControlDialog.this,
				     "stepSize should be an integer",
				     "OK",
				     ERROR_MESSAGE);
			    return;
			}
			if (stepSize<=0){
			    showMessageDialog(
				  ControlDialog.this,
				  "Sorry, stepSize not a valid number",
				  "try again",
				  ERROR_MESSAGE);
			    typedTextStSize=null;
			    return;
			}
		    }
		    parameters.setStepSize(stepSize);
		    typedText2=textField2.getText();
		    if (typedText2!=null){
			try{
			    nrAgents = Integer.parseInt(typedText2);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "Nr. of agents should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (nrAgents<=1){
			    showMessageDialog(ControlDialog.this,
			       "Nr. of agents should be larger than 1",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
		    }
		    parameters.setNAgents(nrAgents);

		    typedText3=textField3.getText();
		    if (typedText3!=null){
			try{
			    noiseCxt = Double.parseDouble(typedText3);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "Noise level should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (noiseCxt<0.0 || noiseCxt>1.0){
			    showMessageDialog(ControlDialog.this,
			       "Noise level should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setNoiseCxt(noiseCxt);
		    }

		    typedText4=textField4.getText();
		    if (typedText4!=null){
			try{
			    maxCxtSize = Integer.parseInt(typedText4);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "Max context size should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (maxCxtSize<1){
			    showMessageDialog(ControlDialog.this,
			       "Max context size should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setMaxCxtSize(maxCxtSize);
		    }

		    typedTextFOA=foaTextField.getText();
		    if (typedTextFOA!=null){
			try{
			    foa = Integer.parseInt(typedTextFOA);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "foa should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (foa<1){
			    showMessageDialog(ControlDialog.this,
			       "foa should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setFOA(foa);
		    }

		    typedTextBottleneck=textFieldBottleneck.getText();
		    if (typedTextBottleneck!=null){
			try{
			    bottleneck = Integer.parseInt(typedTextBottleneck);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "bottleneck should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (bottleneck>120){
			    showMessageDialog(ControlDialog.this,
			       "bottleneck should be less or equal to 120",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setTrainingSet(bottleneck);
		    }

		    typedTextWorkDir=textFieldWorkDir.getText();
		    if (!typedTextWorkDir.equals("null")){
			String wDir=typedTextWorkDir;
			File n = new File(wDir);
			if (!n.isDirectory() && !n.mkdirs()){
			    showMessageDialog(ControlDialog.this,
				       "could not create the workDir, system might crash",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			workDir=wDir;			

			parameters.setDir(workDir);
		    }

		    typedTextAlphabetSize=textFieldAlphabetSize.getText();
		    if (typedTextAlphabetSize!=null){
			try{
			    alphabetSize = Integer.parseInt(typedTextAlphabetSize);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "alphabetSize should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (alphabetSize<1){
			    showMessageDialog(ControlDialog.this,
			       "alphabetSize should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			if (alphabetSize>26){
			    showMessageDialog(ControlDialog.this,
			       "alphabetSize should be less or equal to 26",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setAlphabetSize(alphabetSize);
		    }

		    typedTextPreLing=textFieldPreLing.getText();
		    if (typedTextPreLing!=null){
			try{
			    preLing = Integer.parseInt(typedTextPreLing);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "preLing should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (preLing>nrRuns){
			    showMessageDialog(ControlDialog.this,
			       "preLing should be less or equal to nGames",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setNNoLearning(preLing);
		    }

		    typedTextMemMeanings=textFieldMemMeanings.getText();
		    if (typedTextMemMeanings!=null){
			try{
			    memMeanings = Integer.parseInt(typedTextMemMeanings);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "memMeanings should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (memMeanings<1){
			    showMessageDialog(ControlDialog.this,
			       "memMeanings should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setNMeanings(memMeanings);
		    }

		    typedTextMemWords=textFieldMemWords.getText();
		    if (typedTextMemWords!=null){
			try{
			    memWords = Integer.parseInt(typedTextMemWords);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "memWords should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (memWords<1){
			    showMessageDialog(ControlDialog.this,
			       "memWords should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setNSymbols(memWords);
		    }


		    typedText5=textField5.getText();
		    if (typedText5!=null){
			try{
			    probability = Double.parseDouble(typedText5);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "Creation probability should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (probability<0.0 || probability>1.0){
			    showMessageDialog(ControlDialog.this,
			       "Creation probability should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setCreationProb(probability);
		    }

		    typedText5a=textField5a.getText();
		    if (typedText5a!=null){
			try{
			    pAdultSpeaker = Double.parseDouble(typedText5a);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "pAdultSpeaker should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (pAdultSpeaker<0.0 || pAdultSpeaker>1.0){
			    showMessageDialog(ControlDialog.this,
			       "pAdultSpeaker should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setPAdultSpeaker(pAdultSpeaker);
		    }

		    typedText5b=textField5b.getText();
		    if (typedText5b!=null){
			try{
			    pAdultHearer = Double.parseDouble(typedText5b);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "pAdultHearer should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (pAdultHearer<0.0 || pAdultHearer>1.0){
			    showMessageDialog(ControlDialog.this,
			       "pAdultHearer should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setPAdultHearer(pAdultHearer);
		    }

		    typedText5c=textField5c.getText();
		    if (typedText5c!=null){
			try{
			    etaN = Double.parseDouble(typedText5c);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "etaN should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (etaN<0.0 || etaN>1.0){
			    showMessageDialog(ControlDialog.this,
			       "etaN should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setEtaN(etaN);
		    }

		    typedText5d=textField5d.getText();
		    if (typedText5d!=null){
			try{
			    etaS = Double.parseDouble(typedText5d);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "etaS should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (etaS<0.0 || etaS>1.0){
			    showMessageDialog(ControlDialog.this,
			       "etaS should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setEtaS(etaS);
		    }

		    sToS=checkbox3.isSelected();
		    parameters.setSpeakerToSpeaker(sToS);

		    adaptation=checkBox6d.isSelected();
		    parameters.setAdaptation(adaptation);

		    printGame=checkBoxPrint.isSelected();
		    parameters.setPrintGame(printGame);

		    variation=checkBox6a.isSelected();
		    parameters.setVariation(variation);

		    typedText6b=textField6b.getText();
		    if (typedText6b!=null){
			try{
			    ogProbability = Double.parseDouble(typedText6b);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "ogProbability should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (ogProbability<0.0 || ogProbability>1.0){
			    showMessageDialog(ControlDialog.this,
			       "ogProbability should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setOGProbability(ogProbability);
		    }

		    typedText6c=textField6c.getText();
		    if (typedText6c!=null){
			try{
			    ggProbability = Double.parseDouble(typedText6c);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "ggProbability should be a double",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (ggProbability<0.0 || ggProbability>1.0){
			    showMessageDialog(ControlDialog.this,
			       "ggProbability should be between 0.0 and 1.0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setGGProbability(ggProbability);
		    }


		    logFile=checkBox1.isSelected();
		    parameters.setLogFile(logFile);

		    saveLexicon=checkBox1a.isSelected();
		    parameters.setSaveLexicon(saveLexicon);

		    typedText8 = textField8.getText();
		    if (typedText8!=null){
			try{
			    nIterations = Integer.parseInt(typedText8);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "nIter should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (nIterations<1){
			    showMessageDialog(ControlDialog.this,
			       "nIter size should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setNIterations(nIterations);
			if (nIterations>1) ILM=true;
			else ILM=false;
			parameters.setILM(ILM);

		    }
		    
		    fixedColours=checkFCol.isSelected();
		    parameters.setFixedColours(fixedColours);
		    updateScoreBool=checkUScore.isSelected();
		    if (updateScoreBool) parameters.setUpdateScore('s');
		    else parameters.setUpdateScore('u');

		    testPop=checkTestPop.isSelected();
		    parameters.setTestPopulation(testPop);

		    printScores=checkPrintScores.isSelected();
		    parameters.setPrintScore(printScores);

		    ilmIncremental=checkbox4.isSelected();
		    parameters.setIlmIncremental(ilmIncremental);

		    typedText10 = textField10.getText();
		    if (typedText10!=null){
			try{
			    popGrowth = Integer.parseInt(typedText10);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "popGrowth should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			if (popGrowth<1){
			    showMessageDialog(ControlDialog.this,
			       "popGrowth size should be larger than 0",
					      "OK",
					      ERROR_MESSAGE);
			    return;
			}
			parameters.setPopGrowth(popGrowth);
		    }

		    typedText10a = textField10a.getText();
		    if (typedText10a!=null){
			try{
			    maxAgents = Integer.parseInt(typedText10a);
			}
			catch(final NumberFormatException pNrFormatExc){
			    showMessageDialog(ControlDialog.this,
				       "maxAgents should be an integer",
				       "OK",
				       ERROR_MESSAGE);
			    return;
			}
			
			parameters.setMaxAgents(maxAgents);
		    }

		    features[0]=fv0.isSelected();
		    features[1]=fv1.isSelected();
		    features[2]=fv2.isSelected();
		    features[3]=fv3.isSelected();
		    features[4]=fv4.isSelected();
		    features[5]=fv5.isSelected();

		    parameters.setFeatures(features);

		    agentType=aTypeBox.getSelectedIndex();
		    parameters.setAgentType(AgentType.valueOf(agentType));

		    String s=(String)comboBox.getSelectedItem();
		    gameType=s.charAt(0);
		    parameters.setGameType(gameType);
		  
		    String s1=(String)uTypeBox.getSelectedItem();
		    updatePType=s1.charAt(0);
		    parameters.setUpdatePType(updatePType);

    }

}
				     


