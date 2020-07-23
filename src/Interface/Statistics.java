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

public abstract class Statistics extends JComponent{

    /**
     * Constructs the frame with a pointer to the class where the 
     * actual statistics are maintained. It appeared to be more
     * easy to keep these two apart.
     *
     * @param pStats the actual statistics
     */
    public Statistics(){}

    public Statistics(Stats pStats){}

    /** After the statistics have been calculated, these have to be updated
     * for the window frame.
     *
     * @param pStats the actual statistics
     */
    public abstract void update(final Stats pStats);

    public abstract void  paintComponent(final Graphics tGraphics);

}





