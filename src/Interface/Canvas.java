package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import Util.CompositionalStats;
import Util.HolisticStats;
import Util.Parameters;
import Util.Parameters.AgentType;
import LGames.Agent;
import LGames.Context;
import Util.Stats;

/**
 * This class makes sure that the canvas goes up and controls the top level
 * of the user interface.
 * At the moment I will not go into the details of this class.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class Canvas{

    boolean [] views = new boolean [3];    

    final JFileChooser fc = new JFileChooser();
    final JFileChooser fc0 = new JFileChooser();
    
    //    final Stats pStats = new Stats();
    final JFrame tJFrame = new JFrame("Talking Heads simulation v4.0.3");
    final JDesktopPane tJDesktopPane = new JDesktopPane();
    final Container tContentPane = tJFrame.getContentPane();
    final JInternalFrame tFirstJInternalFrame =
	new JInternalFrame("GEOM World",true,false,false);
    final Container tFirstContentPane = 
	tFirstJInternalFrame.getContentPane();
   
    final JInternalFrame tThirdJInternalFrame = 
	new JInternalFrame("Language games",true,false,false);
    

    
    final Container tThirdContentPane =
	tThirdJInternalFrame.getContentPane();
    JInternalFrame tFourthJInternalFrame =
	    new JInternalFrame("Statistics",true,false,false);
    Container tFourthContentPane =
	tFourthJInternalFrame.getContentPane();
    final Toolkit tToolkit = tJFrame.getToolkit();
    final Dimension tDimension = tToolkit.getScreenSize();
    final GeomJComponent tGeomJComponent = new GeomJComponent();
    final LanguageJComponent tLGJComponent = 
	new LanguageJComponent();
    Statistics tStatistics = null;
    
    JInternalFrame tSecondJInternalFrame =
	new JInternalFrame("Control box",true,false,true);
    ControlDialog tDialog = null;
    final PopMenu tPopMenu = new PopMenu();
    final JMenuBar tJMenuBar = tPopMenu.getJMenuBar();
    //   Stats xStats = null;
    private AgentType oldAgentType = AgentType.COMPOSITIONAL2;
    
    public Canvas(){
    }

    public Canvas(final Stats pStats, final Parameters parameters){
	
	//xStats = pStats;
	if (parameters.getAgentType()==AgentType.COMPOSITIONAL2 || parameters.getAgentType() == AgentType.STRATEGIC)
	    tStatistics = new CompositionalStatistics((CompositionalStats)pStats);
	else
	    tStatistics = new HolisticStatistics((HolisticStats)pStats);
	oldAgentType=parameters.getAgentType();

	tPopMenu.setParameters(parameters);
	tDialog = new ControlDialog(parameters);
	tContentPane.setBackground(Color.gray);
	tContentPane.add(tJDesktopPane, BorderLayout.CENTER);

	tFirstContentPane.setBackground(Color.white);
	tFirstContentPane.add(tGeomJComponent,BorderLayout.CENTER);
	tFirstJInternalFrame.setLocation(0,0);
	tFirstJInternalFrame.setSize(400,300);
	tFirstJInternalFrame.setVisible(true);
	
	
	tSecondJInternalFrame= tDialog.
	    createInternalFrame(tFirstContentPane,"Control");
	tSecondJInternalFrame.setDefaultCloseOperation(
			       JInternalFrame.DO_NOTHING_ON_CLOSE);
	tSecondJInternalFrame.setLocation(400,0);
	tSecondJInternalFrame.setSize(400,300);
	tSecondJInternalFrame.setVisible(true);
	tSecondJInternalFrame.setResizable(true);
	
	tThirdContentPane.setBackground(Color.lightGray);
	tThirdContentPane.add(tLGJComponent,BorderLayout.CENTER);
	tThirdJInternalFrame.setLocation(0,300);
	tThirdJInternalFrame.setSize(400,270);
	tThirdJInternalFrame.setVisible(true);

	tFourthContentPane.setBackground(Color.white);
	tFourthContentPane.add(tStatistics,BorderLayout.CENTER);
	tFourthJInternalFrame.setLocation(400,300);
	tFourthJInternalFrame.setSize(400,270);
	tFourthJInternalFrame.setVisible(true);
	
	tJDesktopPane.add(tFirstJInternalFrame);
	tJDesktopPane.add(tSecondJInternalFrame);
	tJDesktopPane.add(tThirdJInternalFrame);
	tJDesktopPane.add(tFourthJInternalFrame);
	
	tJFrame.setJMenuBar(tJMenuBar);
	tJFrame.setLocation(100,100);
	tJFrame.setSize(800,590);
	tJFrame.setVisible(true);
	tJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	for (int i=0;i<views.length;i++)
	    views[i]=true;
    }

    public void updateNewGame(final Stats pStats,int aType){
	if (aType!=oldAgentType.getValue()){
	    tFourthContentPane.remove(tStatistics);
	    if (aType==2)
		tStatistics=new CompositionalStatistics((CompositionalStats)pStats);
	    else tStatistics=new HolisticStatistics((HolisticStats)pStats);
	    tFourthContentPane.add(tStatistics,BorderLayout.CENTER);
	    tFourthContentPane.setBackground(Color.white);
	    tFourthJInternalFrame.setContentPane(tStatistics);
	    tFourthJInternalFrame.setLocation(400,300);
	    tFourthJInternalFrame.setSize(400,270);
	    oldAgentType=AgentType.valueOf(aType);
	    // tStatistics.paintComponent();
	}
	if (views[0]){
	    tFirstJInternalFrame.setVisible(true);
	    tGeomJComponent.updateCxt(null);
	    tGeomJComponent.repaint();
	}
	else tFirstJInternalFrame.setVisible(false);
	
	if (views[1]){
	    tThirdJInternalFrame.setVisible(true);
	    tLGJComponent.updateNewGame();
	    tLGJComponent.repaint();
	}
	else tThirdJInternalFrame.setVisible(false);
	
	if (views[2]){
	    
	    tFourthJInternalFrame.setVisible(true);
	    tStatistics.update(pStats);
	    tStatistics.repaint();
	}
	else tFourthJInternalFrame.setVisible(false);

    }

    public File openLogFile(){
	File logFile = null;
	int retval=fc0.showOpenDialog(tDialog);
	if (retval==JFileChooser.APPROVE_OPTION){
	    logFile = fc0.getSelectedFile();
	}
	return logFile;
    }

    public void updateGame(final Agent speaker,final Context cxt,final int n,
			   final Agent hearer,final int iter,final char type,final int aType,
			   final Stats pStats){
	if (aType!=oldAgentType.getValue()){
	    tFourthContentPane.remove(tStatistics);
	    if (aType==2)
		tStatistics=new CompositionalStatistics((CompositionalStats)pStats);
	    else tStatistics=new HolisticStatistics((HolisticStats)pStats);
	    tFourthContentPane.add(tStatistics,BorderLayout.CENTER);
	    tFourthContentPane.setBackground(Color.white);
	    tFourthJInternalFrame.setContentPane(tStatistics);
	    tFourthJInternalFrame.setLocation(400,300);
	    tFourthJInternalFrame.setSize(400,270);
	    oldAgentType=AgentType.valueOf(aType);	    
	}
	if (views[0]){
	    tFirstJInternalFrame.setVisible(true);
	    tGeomJComponent.
		updateTopic(speaker.getTopic());
	    tGeomJComponent.updateCxt(cxt);
	    tGeomJComponent.repaint();
	}
	else tFirstJInternalFrame.setVisible(false);
	
	if (views[1]){
	    tThirdJInternalFrame.setVisible(true);
	    tLGJComponent.updateGame(n+1,speaker,
				     hearer,iter,type,aType);
	    tLGJComponent.repaint();
	}
	else tThirdJInternalFrame.setVisible(false);
	
	if (views[2]){
	    tFourthJInternalFrame.setVisible(true);
	    //  pStats.print();
	    tStatistics.update(pStats);
	    tStatistics.repaint();
	}
    }

    public File openLexFile(){
	File file = null;
	int retval = fc.showOpenDialog(tDialog);
	
	if (retval == JFileChooser.APPROVE_OPTION) 
	    file = fc.getSelectedFile();
	return file;
    }

    public void savedLexFile(){
	tDialog.showMessageDialog(tDialog,
				  "Saved the lexicon",
				  "OK",
				  JOptionPane.ERROR_MESSAGE);
    }

    public void showLexicon(final List lAgents,AgentType aType,char uType){
	JFrame lexFrame = new JFrame("Lexicon");//,true,true,true);

	Lexicon lexicon = new Lexicon(lAgents,aType,uType);
	lexicon.setBackground(Color.lightGray);
	lexicon.setOpaque(false);
	//lexicon.setOpaque(false);
	//lexicon.setForeground(Color.white);
	Container tLexiconPane = lexFrame.getContentPane();
	//tLexiconPane.setBackground(Color.lightGray);
	//tLexiconPane.setOpaque(false);
	tLexiconPane.add(lexicon);
	lexicon.setSize(410,500);
	tLexiconPane.setSize(410,500);
	lexFrame.setLocation(10,10);
	lexFrame.setSize(410,500);
	lexFrame.setVisible(true);
	lexFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	lexicon.repaint();
	while (!lexicon.isDone()) ;//lexicon.repaint();
	lexFrame.dispose();
    }


    public void errorMessage(final int e){

	switch (e){
	case 0:
	    tDialog.showMessageDialog(tDialog,
				      "You are not allowed to change uScore without starting a new simulation",
				      "OK",
				      JOptionPane.ERROR_MESSAGE);
	    break;
	case 1:
	    tDialog.showMessageDialog(tDialog,
				      "nAgents+growth should be <= maxAgents",
				      "OK",
				      JOptionPane.ERROR_MESSAGE);
	    break;
	case 2:
	    tDialog.showMessageDialog(tDialog,
				      "foa should be <= cxtSize",
				      "OK",
				      JOptionPane.ERROR_MESSAGE);
	    break;
	}
    }
}
