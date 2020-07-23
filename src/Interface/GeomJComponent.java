/* Implementation of the Geom world window.

   (c) Paul Vogt, 2001
*/
package Interface;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JComponent;

import LGames.Context;

/** GeomJComponent implements the window frame for the Geom world.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class GeomJComponent extends JComponent{

    private Context tCxt = null;
    private int topic = 0;

    public GeomJComponent(){

    }

    public void updateCxt(Context cxt){

	tCxt = cxt;

    }

    public void updateTopic(int t){
	topic=t;
    }

    private int [] getXs(int x0,int x1,int type){
	int [] result = new int [1];
	switch (type){
	case 0://triangle
	    result = new int [3];
	    result[0] = x0;
	    result[1] = x0+(int)(0.5*x1);
	    result[2] = x0+x1;
	    break;
	case 1://5 sided polygon
	    result = new int [5];
	    result[0] = x0;
	    result[1] = x0;
	    result[2] = x0+(int)(0.5*x1);
	    result[3] = x0+x1;
	    result[4] = x0+x1;
	    break;
	case 2://6 sided polygon
	    result = new int[6];
	    result[0]=x0+(int)((double)x1/(double)3);
	    result[1]=x0;
	    result[2]=x0+(int)((double)x1/(double)3);
	    result[3]=x0+2*(int)((double)x1/(double)3);
	    result[4]=x0+x1;
	    result[5]=x0+2*(int)((double)x1/(double)3);
	    break;
	case 3://6 sided irregular polygon 1
	    result = new int[6];
	    result[0]=x0;
	    result[1]=x0;
	    result[2]=x0+(int)((double)x1/(double)3);
	    result[3]=x0+2*(int)((double)x1/(double)3);
	    result[4]=x0+x1;
	    result[5]=x0+x1;
	    break;
	case 4://6 sided irregular polygon 2
	    result = new int[6];
	    result[0]=x0;
	    result[1]=x0;
	    result[2]=x0+(int)((double)x1/(double)3);
	    result[3]=x0+2*(int)((double)x1/(double)3);
	    result[4]=x0+x1;
	    result[5]=x0+x1;
	    break;
	case 5://5 sided irregular polygon
	    result = new int[5];
	    result[0]=x0;
	    result[1]=x0;
	    result[2]=x0+2*(int)((double)x1/(double)3);
	    result[3]=x0+x1;
	    result[4]=x0+x1;
	    break;
	case 6://cross
	    result=new int[12];
	    result[0]=x0+(int)((double)x1/(double)3);
	    result[1]=x0+(int)((double)x1/(double)3);
	    result[2]=x0;
	    result[3]=x0;
	    result[4]=x0+(int)((double)x1/(double)3);
	    result[5]=x0+(int)((double)x1/(double)3);
	    result[6]=x0+2*(int)((double)x1/(double)3);
	    result[7]=x0+2*(int)((double)x1/(double)3);
	    result[8]=x0+x1;
	    result[9]=x0+x1;
	    result[10]=x0+2*(int)((double)x1/(double)3);
	    result[11]=x0+2*(int)((double)x1/(double)3);
	    break;
	}
	return result;
    }

    private int [] getYs(int y0,int y1,int type){

	int [] result = new int [3];

	switch (type){
	case 0://triangle
	    result[0] = y0+y1;
	    result[1] = y0;
	    result[2] = y0+y1;
	    break;
	case 1://5 sided polygon
	    result = new int [5];
	    result[0]=y0+y1;
	    result[1]=y0+(int)(0.5*y1);
	    result[2]=y0;
	    result[3]=y0+(int)(0.5*y1);
	    result[4]=y0+y1;
	    break;
	case 2://6 sided polygon
	    result = new int [6];
	    result[0] = y0+y1;
	    result[1] = y0+(int)(0.5*y1);
	    result[2]=y0;
	    result[3]=y0;
	    result[4]=y0+(int)(0.5*y1);
	    result[5]=y0+y1;
	    break;
	case 3://6 sided irregular polygon 1 
	    result = new int [6];
	    result[0]=y0+y1;
	    result[1]=y0+(int)(0.5*y1);
	    result[2]=y0;
	    result[3]=y0;
	    result[4]=y0+(int)(0.5*y1);
	    result[5]=y0+y1;
	    break;
	case 4://6 sided irregular polygon 2
	    result = new int [6];
	    result[0]=y0+y1;
	    result[1]=y0+(int)((double)y1/4.0);
	    result[2]=y0;
	    result[3]=y0;
	    result[4]=y0+(int)((double)y1/4.0);
	    result[5]=y0+y1;
	    break;
	case 5://5 sided irregular polygon
	    result = new int [5];
	    result[0]=y0+y1;
	    result[1]=y0;
	    result[2]=y0;
	    result[3]=y0+(int)((double)y1/4.0);
	    result[4]=y0+y1;
	    break;
	case 6://cross
	    result=new int[12];
	    result[0]=y0+y1;
	    result[1]=y0+2*(int)((double)y1/4.0);
	    result[2]=y0+2*(int)((double)y1/4.0);
	    result[3]=y0+(int)((double)y1/4.0);
	    result[4]=y0+(int)((double)y1/4.0);
	    result[5]=y0;
	    result[6]=y0;
	    result[7]=y0+(int)((double)y1/4.0);
	    result[8]=y0+(int)((double)y1/4.0);
	    result[9]=y0+2*(int)((double)y1/4.0);
	    result[10]=y0+2*(int)((double)y1/4.0);
	    result[11]=y0+y1;
	    break;
	}
	return result;
    }

    public void paintComponent(final Graphics tGraphics){

	double [] fv;
	int [] pX;
	int [] pY;
	if (tCxt!=null)
	for (int i=0;i<tCxt.distinctive.length;i++){

	    fv=tCxt.featureVector[i];
	    Color c = new Color(tCxt.red[i],tCxt.green[i],
				tCxt.blue[i]);
	    tGraphics.setColor(c);
	    //System.out.println(i+" "+tCxt.types[i]);
	    switch (tCxt.types[i]){
	    case 0://circle
		tGraphics.fillOval(tCxt.pointX[i],tCxt.pointY[i],
				   tCxt.width[i],
				   tCxt.height[i]);
		break;
	    case 1://triangle
		pX=getXs(tCxt.pointX[i],tCxt.width[i],0);
		pY=getYs(tCxt.pointY[i],tCxt.height[i],0);
		//for (int j=0;j<pX.length;j++)
		//  System.out.print("("+pX[j]+","+pY[j]+") ");
		tGraphics.fillPolygon(pX,pY,3);
		break;
	    case 2://rectangle 1
		tGraphics.fillRect(tCxt.pointX[i],tCxt.pointY[i],
				   tCxt.width[i],
				   tCxt.height[i]);
		break;
	    case 3://rectangle 2
		tGraphics.fillRect(tCxt.pointX[i],tCxt.pointY[i],
				   tCxt.width[i],
				   tCxt.height[i]);
		break;
	    default://all other figures
		pX=getXs(tCxt.pointX[i],tCxt.width[i],tCxt.types[i]-3);
		pY=getYs(tCxt.pointY[i],tCxt.height[i],tCxt.types[i]-3);
		tGraphics.fillPolygon(pX,pY,pX.length);
		break;
		//default:
		//tGraphics.drawString("other",tCxt.pointX[i],tCxt.pointY[i]);
		//break;
	    }

	    if (i==topic){
		tGraphics.setColor(Color.black);
		tGraphics.drawRect(tCxt.pointX[i],tCxt.pointY[i],
				   tCxt.width[i],
				   tCxt.height[i]);
	    }

	}
    }

    public Dimension getPreferredSize(){

	return new Dimension(100,100);
    }
}
