import java.io.FileReader;
import java.util.Scanner;
import java.lang.Math;
/**
 * 
 * k mean clustering
 * 
 * @author Steve Jun
 *
 */
public class KmeansClustering {
	
	public static void main(String[] args){
		if (args.length==0) System.out.println("No file specified.");
		else{
			//step 0: Prepare everything for processing
			int count=0, k, p, x, y, closestCentroid;
			int[][] A, C;
			double dist, smallestDist;		
			Node newNode;
			Node[] L;
			Scanner scn;
			
			try{			
				scn = new Scanner(new FileReader(args[0]));
				k=Integer.valueOf(args[1]);
				System.out.println("Our k="+k);
				System.out.println("");
				
				A = new int[81][81];
				for (int a=0; a<81; ++a)
					for (int b=0; b<81; ++b)
						A[a][b]=0;//0 out array
				
				//step 1: Randomly partition the point set into k-groups, evenly
				L = new Node[k]; //array of Node heads for k partitions
				for (int i=0; i<k; ++i){
					L[i]=new Node();
				}
				
				C = new int[k][2];//array for k centroids
				p = 80/k;
				for (int i=0; i<k; ++i){
					C[i][0]=(i)*p;//x for centroid i
					C[i][1]=(i)*p;//y for centroid i
				}
				
				System.out.println("Given points: ");
				while(scn.hasNextInt()){
					++count;
					x=scn.nextInt();
					y=scn.nextInt();
					System.out.println(count+": x="+x+" y="+y); 
					
					smallestDist=80*Math.sqrt(2);
					closestCentroid=k-1;
					for (int i=0; i<k; ++i){
						dist= distanceOf(x,y,C[i][0],C[i][1]);
						if (dist<=smallestDist){
							smallestDist=dist;
							closestCentroid=i;
						}
					}
					newNode = new Node();
					newNode.x=x;
					newNode.y=y;
					newNode.label=closestCentroid;
					Node.append(L[closestCentroid],newNode);
					
				}
				
				/****FILLING, PRINTING, ZEROING graph****/
				//FILLING
				for (int i=0; i<k; ++i){
					Node ptr = L[i];
					while(ptr.next!=null){
						x=ptr.next.x;
						y=ptr.next.y;
						A[x][y]=ptr.next.label+1;
						ptr=ptr.next;
					}
					x=C[i][0];
					y=C[i][1];
					A[x][y]=90+i+1;//centroids will appear with a 9
				}
				//PRINTING
				for (int a=80; a>=0; --a){
					for (int b=0; b<81; ++b){
						if(A[a][b]>=90)	
							System.out.print(" "+A[a][b]);
						else if (A[a][b]==0)
							System.out.print("   ");
						else 
							System.out.print(" "+A[a][b]+" ");
					}
					System.out.println();
				}
				//ZEROING
				for (int a=0; a<81; ++a)
					for (int b=0; b<81; ++b)
						A[a][b]=0;//0 out array
				System.out.println();
				/****FILLING, PRINTING, ZEROING graph****/
				
				boolean changedLabels = true;
				int itrCount=0;
				
				while(changedLabels){
					changedLabels=false;
					System.out.println("***************");
					System.out.println("* Iteration "+(++itrCount)+" *");
					System.out.println("***************");
					System.out.println();
					//step 2: For every group, we compute the centroid (x,y)
					//x = the average number of x-coordinates of points in group
					//y = the average number of y-coordinates of points in group
					int sumx,	sumy,	counter;	double meanx,	meany;	Node ptr;
					for (int i=0; i<k; ++i){
						sumx=0;		sumy=0;		counter=0;	meanx=0;	meany=0;
						ptr = L[i];
						while(ptr.next!=null){
							sumx+=ptr.next.x;
							sumy+=ptr.next.y;
							++counter;
							ptr=ptr.next;
						}
						meanx=sumx/counter;
						meany=sumy/counter;
						C[i][0]= (int)meanx;
						C[i][1]= (int)meany;
						System.out.println("Centroid "+(i+1)+" now has coordinates ("+C[i][0]+","+C[i][1]+")");
					}
					
					//step 3: For every point in the entire set, we compute k distances 
					//from p to the centroid of k groups
					
					for (int i=0; i<k; ++i){
						ptr = L[i];//for centroids of k groups
						while(ptr.next!=null){
							//calc distance
							//find smallest
							smallestDist=distanceOf(ptr.next.x,ptr.next.y,C[i][0],C[i][1]);
							closestCentroid=i;
							
							for (int j=0; j<k; ++j){
								dist= distanceOf(ptr.next.x,ptr.next.y,C[j][0],C[j][1]);
								if (dist<smallestDist){
									smallestDist=dist;
									closestCentroid=j;
								}
							}
							
					//step 4: We re-label each point to the group that has the shortest of the centroid distance
							if(closestCentroid!=i){
								Node temp = ptr.next;
								ptr.next=ptr.next.next;
								temp.next=null;
								temp.label=closestCentroid;
								Node.append(L[closestCentroid], temp);
								changedLabels=true;
							}
							else 
								ptr=ptr.next;
						}
					}
					
					System.out.println();
					
					Node walker;
					for (int i=0; i<k; ++i){
						walker=L[i];
						while(walker.next!=null){
							System.out.print("x="+walker.next.x+"  y="+walker.next.y);
							System.out.println("   Closest centroid is "+(walker.next.label+1)+" with coordinates("+C[walker.next.label][0]+","+C[walker.next.label][1]+")");
							walker=walker.next;
						}
						System.out.println();
					}
					
					
					/****FILLING, PRINTING, ZEROING graph****/
					//FILLING
					for (int i=0; i<k; ++i){
						ptr = L[i];
						while(ptr.next!=null){
							x=ptr.next.x;
							y=ptr.next.y;
							A[x][y]=ptr.next.label+1;
							ptr=ptr.next;
						}
						x=C[i][0];
						y=C[i][1];
						A[x][y]=90+i+1;//centroids will appear with a 9
					}
					//PRINTING
					for (int a=80; a>=0; --a){
						for (int b=0; b<81; ++b){
							if(A[a][b]>=90)	
								System.out.print(" "+A[a][b]);
							else if (A[a][b]==0)
								System.out.print("   ");
							else 
								System.out.print(" "+A[a][b]+" ");
						}
						System.out.println();
					}
					//ZEROING
					for (int a=0; a<81; ++a)
						for (int b=0; b<81; ++b)
							A[a][b]=0;//0 out array
					System.out.println();
					/****FILLING, PRINTING, ZEROING graph****/
				}//step 5: Repeat step 2 to the step 4 until no point change in label. 
				
				
			}
			catch (Exception e){System.out.println("We have a problem.");}
		}
	}
	
	public static double distanceOf(int xi, int yi, int xj, int yj ){
		double dist;
		dist=Math.sqrt((xi-xj)*(xi-xj)+(yi-yj)*(yi-yj));
		return dist;
	}
	
	public static class Node {
		int x;
		int y;
		int label; //closest centroid
		Node next;
		
		Node(){
			x=0;	y=0;
			label=-1;
			next=null;
		}
		
		static void append(Node h,Node n){
			if (h.next==null) 	h.next=n;
			else append(h.next,n);
		}		
	}
	
}
