package sadiasrc.heuristic;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.naming.ldap.ExtendedRequest;

import sadiasrc.util.IndexedSet;

public class GrowNeighborhoodWithStart extends AbstractChooser implements IChooser{
	
		
		public GrowNeighborhoodWithStart(IndexGraph g) {
			
			super(g);
			//System.out.println("LEFT : "+LEFT);
			//System.out.println("RIGHT : "+RIGHT);
		
		}
		
		@Override
		public IndexVertex choose() 
		{
			
			IndexVertex choosen=null;
			IndexedSet<IndexVertex> groundSet;
			groundSet = new IndexedSet<IndexVertex>(G.vertices());
			
			VSubSet SubsetToChooseFrom = new  VSubSet(groundSet);
			
			if(N_LEFT.isEmpty())
			{
				//get the smallest component from RIGHT
			/*	ArrayList<IndexVertex> smallestComponent=null;
				int smallestComp_size=RIGHT.size();
				for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,RIGHT))
				{
					if(vs.size()<=smallestComp_size)
					{
						smallestComponent=vs;
						smallestComp_size=vs.size();
					}
					
				}
				//Find the extremal vertex in the smallest component in RIGHT
				 * */
				choosen = BasicGraphAlgorithms.BFS(G,RIGHT.iterator().next());
				choosen = BasicGraphAlgorithms.BFS(G,choosen);
				
				//choosen= G.MinDegreeVertex(RIGHT);
				//System.out.println("Choosen from right "+choosen);
			}
			else
			{
				
			
				//To store E/I ratios if needed
				HashMap<IndexVertex, Double> E_I_ratio = null;
				HashMap<IndexVertex, Double> External = null;
				//O(n)
				/*for(IndexVertex x : LEFT)
				{
					//If a vertex of left has only one neighbour in the subset to choose then move it to left
					if(N_v_RIGHT.get(x.id()).size()==1)
					{
						System.out.println("Choosing from case 2 : "+N_v_RIGHT.get(x.id()).first());
						return N_v_RIGHT.get(x.id()).first();
					}
				}*/
				
				//If a vertex in N_left has no neighbour in the right then move it to left
				//O(n)
				for(IndexVertex v :  N_LEFT)
				{
					if(N_v_RIGHT.get(v.id()).size()==0)
					{
						//System.out.println("Chosing from case 1: "+v);
						return v;
					}
				}
				
				
				// if moving u from right to left makes u and v( \in left) twin with respect to right then move u from right to left
				//O(n^2)			
				for(IndexVertex u :  RIGHT)
				{
					
					VSubSet N_u_right = N_v_RIGHT.get(u.id());
					
					for(IndexVertex v : LEFT)
					{
						VSubSet N_v_right = new VSubSet(N_v_RIGHT.get(v.id()));
						N_v_right.remove(u);
						if(N_u_right.equals(N_v_right))//|| N_v_right.contains(N_u_right))
						{
							//System.out.println("Choosing from case 3 : "+u);
							return u;
						}
					}
					
				}
				
				
				//if no case satisfied then grow neighbourhood based on E, I and A			
				
				int min=N_LEFT.size();
				VSubSet min_of_N_left_right=new VSubSet(groundSet);//find what is minimum neighbourhood of a vertex \in left in right
				//O(n)
				for(IndexVertex l:LEFT)
				{
					VSubSet temp=new VSubSet(N_v_RIGHT.get(l.id()));
					
					int Size_N_v = temp.size();
					if((Size_N_v>0)&&(Size_N_v<=min))
					{
						min = Size_N_v;
						min_of_N_left_right=temp;
					}
				}
				
				//System.out.println("MIN_of_N_left_right" +min_of_N_left_right);
				
				E_I_ratio = new HashMap<IndexVertex, Double>();
				double min_e_I=Double.MAX_VALUE;
				double min_E=RIGHT.size()-1;
				IndexVertex choosenwtMinE=null;
				External = new HashMap<IndexVertex, Double>();
				
				//O(n)
				for(IndexVertex x : min_of_N_left_right)
				{
					
					VSubSet temp=new VSubSet(groundSet);
					temp.addAll(G.neighbours(x));
					double A=temp.size();
					temp.removeAll(LEFT);
					A=A-temp.size();
					double I=0;
					for(IndexVertex y : SubsetToChooseFrom)
					{
						if(temp.contains(y)){
							temp.remove(y);
							I++;
						}
					}
								
					double E = temp.size();
					External.put(x, E)	;	
					double ratio=0;
					if(I==0)
						ratio=E/(I+0.1);
					else
						ratio=E/I;
					
					//System.out.println("vertex="+x+" I = "+ I+" ,e = "+e+"ratio=" +ratio);
				
					E_I_ratio.put(x, ratio);
					
					if(min_e_I>ratio)
					{
						min_e_I=ratio;
						choosen=x;
					}
					
					if(min_E>=E)
					{
						min_E=E;
						choosenwtMinE=x;
					}
						
					
					//if no one has internal and fail to give smaller E/I ratio then choose vertex with smallest E
					if(choosenwtMinE!=null)
					{
						//System.out.println("Choosing from grow neighborhood with min E: "+choosen);
						return choosenwtMinE;
						//return G.MinDegreeVertex(right);
					}
					else
					{
						//System.out.println("Choosing from grow neighborhood : "+choosen);
						return choosen;
					}
					
				}
			
			}
			//System.out.println("choosen "+choosen);
			return choosen;
		}

	}


