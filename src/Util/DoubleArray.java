package Util;


/**
 * This class implements an array of doubles in a similar way ArrayLists are implemented.
 * They serve to decrease memory space (as opposed to ArrayLists) and allow functions directly
 * operate on doubles.<br>
 * The working will further not be discussed here.
 *
 * <p> Copyright (c) 2004 Paul Vogt
 *
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */


public class DoubleArray{

    double [] array = new double [0];
    private boolean changed=true;
    private double maxValue=0.0;

    public DoubleArray(){
    }

    public DoubleArray(final DoubleArray a){
	array=new double[a.array.length];
	for (int i=0;i<array.length;i++)
	    array[i]=a.array[i];
    }

    public DoubleArray(final double [] a){
	array=new double[a.length];
	for (int i=0;i<array.length;i++)
	    array[i]=a[i];
    }

    public DoubleArray(final int l){
	array=new double[l];
    }

    public DoubleArray(final int l,final double x){
	array=new double [l];
	for (int i=0;i<l;i++) array[i]=x;
    }

    public double get(final int i){
	if (i<array.length)
	    return array[i];
	else{
	    System.out.println("Index "+i+" is too large for DoubleArray, length="+array.length);
	    System.exit(0);
	}
	return .0;
    }

    public double [] get(){
	return array;
    }

    public void add(final double el){
	double [] a=new double[array.length+1];
	System.arraycopy(array,0,a,0,array.length);
	a[array.length]=el;
	array=a;
	changed=true;
    }

    public void add(final int i,final double el){
	double [] a=new double[array.length+1];
	if (i>0)
	    System.arraycopy(array,0,a,0,i);
	System.arraycopy(array,i,a,i+1,array.length-i);
	a[i]=el;
	array=a;
	changed=true;
    }

    public void addAll(final double [] el){
	double [] a=new double[array.length+el.length];
	System.arraycopy(array,0,a,0,array.length);
	System.arraycopy(el,0,a,array.length,el.length);
	array=a;
	changed=true;
    }

    public void addAll(final DoubleArray e){
	double [] el = e.get();
	addAll(el);
	changed=true;
    }

    public void addAll(final int i,final double [] el){
	double [] a=new double[array.length+el.length];
	if (i>0)
	    System.arraycopy(array,0,a,0,i);
	System.arraycopy(el,0,a,i,el.length);
	System.arraycopy(array,i,a,i+el.length,array.length-i);
	array=a;
	changed=true;
    }

    public void addAll(final int i,final DoubleArray e){
	double [] el = e.get();
	addAll(i,el);
	changed=true;
    }

    public void set(final int i,final double el){
	if (i<array.length)
	    array[i]=el;
	else{
	    System.out.println("Index out of bounds DoubleArray, Index="+i+", length="+array.length);
	    System.exit(1);
	}
	changed=true;
    }

    public int size(){
	return array.length;
    }

    public void clear(){
	array = new double[0];
	changed=true;
    }

    public double [] remove(final int i){
	double [] a=new double[array.length-1];
	if (i>0)
	    System.arraycopy(array,0,a,0,i);
	System.arraycopy(array,i+1,a,i,array.length-i-1);
	array=a;
	changed=true;
	return array;
    }

    public double [] remove(final int from,final int to){
	double [] a=new double[array.length-to+from-1];
	if (from>0)
	    System.arraycopy(array,0,a,0,from);
	System.arraycopy(array,to+1,a,from,array.length-to-1);
	array=a;
	changed=true;
	return array;
    }

    public void delete(final double el){
	int i=0;
	while (i<array.length)
	    if (array[i]==el) array=remove(i);
	    else i++;
    }

    public double [] copy(){
	double [] a=new double[array.length];
	for (int i=0;i<array.length;i++)
	    a[i]=array[i];
	return a;
    }

    public double [] copy(final int from,final int to){
	double [] a = new double [to-from+1];
	System.arraycopy(array,from,a,0,a.length);
	return a;
    }

    public int indexOf(final double el){
	for (int i=0;i<array.length;i++)
	    if (array[i]==el) return i;
	return -1;
    }

    public boolean contains(final double el){
	for (int i=0;i<array.length;i++)
	    if (array[i]==el) return true;
	return false;
    }

    public boolean containsElementOf(final double [] a){
	for (int i=0;i<array.length;i++)
	    for (int j=0;j<a.length;j++)
		if (array[i]==a[j]) return true;
	return false;
    }

    public boolean containsElementOf(final DoubleArray a){
	return containsElementOf(a.array);
    }


    public boolean isEmpty(){
	if (array.length==0) return true;
	return false;
    }

    public boolean equals(double [] a){
	if (array.length!=a.length) return false;
	for (int i=0;i<a.length;i++)
	    if (array[i]!=a[i]) return false;
	return true;
    }

    public String toString(){
	String ret=new String("[");
	for (int i=0;i<array.length;i++)
	    if (i<array.length-1)
		ret=ret.concat(Utils.doubleString(array[i],5)+",");
	    else ret=ret.concat(Utils.doubleString(array[i],5)+"]");
	return ret;
    }

    public String stringValue(double [] x){
	array=x;
	return toString();
    }

    public String stringValue(double [] x,final int l){
	array=x;
	return toString(l);
    }

    public String toString(final int l){
	String ret=new String("[");
	for (int i=0;i<array.length;i++)
	    if (i<array.length-1)
		ret=ret.concat(Utils.doubleString(array[i],l)+",");
	    else ret=ret.concat(Utils.doubleString(array[i],l)+"]");
	return ret;
    }

    public double maxValue(){
	if (changed){
	    maxValue=0;
	    for (int i=0;i<array.length;i++)
		if (array[i]>maxValue) maxValue=array[i];
	}
	return maxValue;
    }

    public void addUp(final DoubleArray x){
	if (array.length!=x.array.length) Utils.error("DoubleArray addUp length="+array.length+" x.length"+x.array.length);
	for (int i=0;i<array.length;i++) array[i]+=x.array[i];
    }

    public double average(){
	double sum=0;
	for (int i=0;i<array.length;i++)
	    sum+=array[i];
	if (sum>0.0) return sum/(double)array.length;
	return 0.0;
    }

    public double variancePop(final double avg){
	double var=0.0;
	for (int i=0;i<array.length;i++)
	    var+=(array[i]-avg)*(array[i]-avg);
	if (var>0.0) return var/(double)array.length;
	return 0.0;
    }
}
