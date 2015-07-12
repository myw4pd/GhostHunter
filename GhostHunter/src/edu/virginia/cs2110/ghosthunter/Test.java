package edu.virginia.cs2110.ghosthunter;

import java.util.ArrayList;

public class Test {


	public static void main(String[] args) {

		
		UnionIntersection("1 2 3 4 5", "5 4 7 2");
		System.out.println("test");
	}
	


public static void UnionIntersection (String x, String y) {
String[] line1 = x.split(" ");
String[] line2 = y.split(" ");

System.out.println(line1);
System.out.println(line2);

ArrayList<Integer> line1num = new ArrayList<Integer>();
ArrayList<Integer> line2num = new ArrayList<Integer>();
ArrayList<Integer> union = new ArrayList<Integer>();
for(int i = 0; i< line1num.size(); i++) {
line1num.add(Integer.parseInt(line1[i]));
union.add(line1num.get(i));
}
for(int j = 0; j< line2num.size(); j++) {
line2num.add(Integer.parseInt(line2[j]));
union.add(line2num.get(j));
}
ArrayList<Integer> intersection = new ArrayList<Integer>();
for(int k: line1num){
if(line2num.contains(k)){
intersection.add(k);
line1num.remove(k);
line2num.remove(k);
}
}
System.out.println("Union set: " + union);
System.out.println("Intersection set:" + intersection);
}

}