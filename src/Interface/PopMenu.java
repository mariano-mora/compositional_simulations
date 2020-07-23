package Interface;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import Util.Parameters;

/**
 * Implements the menu of the simulator.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */
public class PopMenu implements ActionListener{

    private JMenuBar iJMenuBar;
    private boolean GEOM=true;
    private boolean CONTR=true;
    private boolean LG=true;
    private boolean STATS=true;
    private JMenu tView=null;
    private boolean[] tViews = new boolean [3];
    private Parameters parameters = null;

    class CheckBoxListener implements ItemListener{

	public void itemStateChanged(final ItemEvent e){
	    
	    Object source = e.getItemSelectable();
	    
	    if (source == tView.getItem(0)){
		tViews[0]=((JCheckBoxMenuItem)tView.getItem(0)).getState();
	    }
	    else if (source==tView.getItem(1)){
		tViews[1]=((JCheckBoxMenuItem)tView.getItem(1)).getState();
	    }
	    else if (source==tView.getItem(2)){
		tViews[2]=((JCheckBoxMenuItem)tView.getItem(2)).getState();
	    }
	}
    }

    private CheckBoxListener listener = new CheckBoxListener();

    private void iAddJMenuItem(final JMenu pJMenu, final int pKeyEvent,
			       final String pString,final String toolTip){

	final JMenuItem tJMenuItem = new JMenuItem(pString);
	pJMenu.add(tJMenuItem);
	tJMenuItem.setMnemonic(pKeyEvent);
	tJMenuItem.addActionListener(this);
	tJMenuItem.setToolTipText(toolTip);
    }

    private void iAddJCheckBoxMenuItem(final JMenu pJMenu,final int pKeyEvent,
				   final String pString,boolean status){

	final JCheckBoxMenuItem tJCheckBoxItem =
	    new JCheckBoxMenuItem(pString,status);
	pJMenu.add(tJCheckBoxItem);
	tJCheckBoxItem.setMnemonic(pKeyEvent);
	tJCheckBoxItem.addItemListener(listener);
    }

 
    private void iCreateFileMenu(final JMenuBar pJMenuBar){

	final JMenu tFileJMenu = new JMenu("File");
	tFileJMenu.setMnemonic(KeyEvent.VK_F);
	pJMenuBar.add(tFileJMenu);
	iAddJMenuItem(tFileJMenu,KeyEvent.VK_S,"save","Save the lexicon");
	iAddJMenuItem(tFileJMenu,KeyEvent.VK_Q,"quit","Quit the program");
    }

    private JMenu iCreateView(final JMenuBar pJMenuBar){

	final JMenu tViewJMenu = new JMenu("View");
	pJMenuBar.add(tViewJMenu);
	tViewJMenu.setMnemonic(KeyEvent.VK_V);
	iAddJCheckBoxMenuItem(tViewJMenu,KeyEvent.VK_G,"Geom world",true);
	iAddJCheckBoxMenuItem(tViewJMenu,KeyEvent.VK_L,
			  "Language game dialog",true);
	iAddJCheckBoxMenuItem(tViewJMenu,KeyEvent.VK_S,"Statistics",true);
	return tViewJMenu;
    }

    public PopMenu(boolean [] views){
	iJMenuBar = new JMenuBar();
	iCreateFileMenu(iJMenuBar);
	tView=iCreateView(iJMenuBar);
	tViews=views;
    }

    public PopMenu(){
	iJMenuBar = new JMenuBar();
	iCreateFileMenu(iJMenuBar);
    }

    public void setParameters(final Parameters param){
	parameters=param;
    }


    public void actionPerformed(final ActionEvent e){

	switch (e.getActionCommand().charAt(0)){

	case 's':
	    parameters.setPrintLexicon(true);
	    break;

	case 'q':
	    System.exit(0);
	    break;

	}
    }

    public JMenuBar getJMenuBar(){
	return iJMenuBar;
    }
}

