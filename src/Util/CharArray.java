package Util;

/**
 * This class implements an array of character in a similar way ArrayLists or Strings are implemented.
 * They serve to decrease memory space (as opposed to ArrayLists) and allow functions directly
 * relate to the array of characters.<br>
 * The working will further not be discussed here.
 *
 * <p> Copyright (c) Paul Vogt
 *
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */


public class CharArray{

    char [] array = new char [0];

    public CharArray(){
    }

    public CharArray(final CharArray a){
	array=a.get();
    }

    public CharArray(final char [] a){
	array=a;
    }

    public CharArray(final int l){
	array=new char[l];
    }

    public char get(final int i){
	//if (i<array.length)
	    return array[i];
	    /*
	else{
	    System.out.println("Index "+i+" is too large for CharArray, length="+array.length);
	    System.exit(0);
	}
	return 'a';*/
    }

    public char [] get(){
	return array;
    }

    public void add(final char el){
	char [] a=new char[array.length+1];
	System.arraycopy(array,0,a,0,array.length);
	a[array.length]=el;
	array=a;
    }

    public void add(final int i,final char el){
	char [] a=new char[array.length+1];
	if (i>0)
	    System.arraycopy(array,0,a,0,i);
	System.arraycopy(array,i,a,i+1,array.length-i);
	a[i]=el;
	array=a;
    }

    public void addAll(final char [] el){
	char [] a=new char[array.length+el.length];
	System.arraycopy(array,0,a,0,array.length);
	System.arraycopy(el,0,a,array.length,el.length);
	array=a;
    }

    public void addAll(final CharArray e){
	char [] el = e.get();
	addAll(el);
    }

    public void addAll(final int i,final char [] el){
	char [] a=new char[array.length+el.length];
	if (i>0)
	    System.arraycopy(array,0,a,0,i);
	System.arraycopy(el,0,a,i,el.length);
	System.arraycopy(array,i,a,i+el.length,array.length-i);
	array=a;
    }

    public void addAll(final int i,final CharArray e){
	char [] el = e.get();
	addAll(i,el);
    }

    public void set(final int i,final char el){
	//if (i<array.length)
	    array[i]=el;
	    /*
	else{
	    System.out.println("Index out of bounds CharArray, Index="+i+", length="+array.length);
	    System.exit(1);
	    }*/
    }

    public int size(){
	return array.length;
    }

    public void clear(){
	array = new char[0];
    }

    public char [] remove(final int i){
	char [] a=new char[array.length-1];
	if (i>0)
	    System.arraycopy(array,0,a,0,i);
	System.arraycopy(array,i+1,a,i,array.length-i-1);
	array=a;
	return array;
    }

    public char [] remove(final int from,final int to){
	char [] a=new char[array.length-to+from-1];
	if (from>0)
	    System.arraycopy(array,0,a,0,from);
	System.arraycopy(array,to+1,a,from,array.length-to-1);
	array=a;
	return array;
    }

    public void delete(final char el){
	int i=0;
	while (i<array.length)
	    if (array[i]==el) array=remove(i);
	    else i++;
    }

    public char [] copy(){
	char [] a=new char[array.length];
	for (int i=0;i<array.length;i++)
	    a[i]=array[i];
	return a;
    }

    public char [] copy(final int from,final int to){
	char [] a = new char [to-from+1];
	System.arraycopy(array,from,a,0,a.length);
	return a;
    }

    public int indexOf(final char el){
	for (int i=0;i<array.length;i++)
	    if (array[i]==el) return i;
	return -1;
    }

    public boolean contains(final char el){
	for (int i=0;i<array.length;i++)
	    if (array[i]==el) return true;
	return false;
    }

    public boolean isEmpty(){
	if (array.length==0) return true;
	return false;
    }

    public boolean equals(char [] a){
	if (array.length!=a.length) return false;
	for (int i=0;i<a.length;i++)
	    if (array[i]!=a[i]) return false;
	return true;
    }

    public String toString(){
	String ret=new String("[");
	for (int i=0;i<array.length;i++)
	    if (i<array.length-1)
		ret=ret.concat(array[i]+",");
	    else ret=ret.concat(array[i]+"]");
	return ret;
    }
}
