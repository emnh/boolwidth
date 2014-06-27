package sadiasrc.heuristic;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.ArrayList;
import java.util.Collection;

import sadiasrc.util.IndexedSet;

public class ChooseFromSmallestComp implements GreedyChooser {

	IndexGraph G;
	public ChooseFromSmallestComp(IndexGraph G) {
		this.G = G;
	}
	@Override
	public IndexVertex next(Collection<IndexVertex> left) {
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet right = new VSubSet(groundSet);
		
		IndexVertex choosen_vertex = null;
		
		for(IndexVertex v: G.vertices())
		{
			right.add(v);
		}
		right.removeAll(left);
		
		boolean connected = BasicGraphAlgorithms.isConnected(G,right);
		
	
		if(!connected)
		{
			System.out.println("Chosing from smallest component");
			ArrayList<IndexVertex> smallestcomponent=null;
			int smallestComp_size=right.size();
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,right))
			{
				if(vs.size()<smallestComp_size)
				{
					smallestcomponent=vs;
					smallestComp_size=vs.size();
				}
				
			}
			System.out.println(smallestcomponent);
			choosen_vertex=ChooseVertexFrom(smallestcomponent,right, left,G);
		}
		
		else
		{
			choosen_vertex= ChooseVertexFrom(right, right, left,G);
		}
		
		return choosen_vertex;
	}
	
	public IndexVertex ChooseVertexFrom(Collection<IndexVertex> SubsetToChooseFrom,Collection<IndexVertex> right, Collection<IndexVertex> left, IndexGraph G)
	{
//		System.out.println("Left "+left);
//		System.out.println("Right"+right);
		IndexVertex choosen=null;
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet N_v_right = new VSubSet(groundSet);
		
		//If a vertex has no neighbour in the subset to choose from then move it to left
		for(IndexVertex v :  SubsetToChooseFrom)
		{
			if(G.degree(v,SubsetToChooseFrom)==0)
			{
				//System.out.println("Chosing from case 1");
				return v;
			}
		}
		//If a vertex of left has only one neighbour in the subset to choose then move it to left
		for(IndexVertex v :  left)
		{
			N_v_right = new VSubSet(groundSet);
			N_v_right.addAll(G.neighbours(v));
			N_v_right.retainAll(SubsetToChooseFrom);
			
			if(N_v_right.size()==1)
			{
				//System.out.println("Chosing from case 2");
				return v;		
			}
		}
		
		// if moving (u \in right) from right to left makes u and (v \in left) twin with respect to right then move u from right to left
		//v E Left N(v) in right
		ArrayList<VSubSet> N_Left_right = new ArrayList<VSubSet>(G.numVertices());
		for(int i=0; i<G.numVertices();i++)
			N_Left_right.add(new VSubSet(groundSet));
	
		//N(v) for all v in left
		for(IndexVertex x : left)
		{
			VSubSet temp = new VSubSet(groundSet);
			for(IndexVertex y : G.neighbours(x)){
				if(SubsetToChooseFrom.contains(y))
					temp.add(y);}
			//System.out.println("x"+x+"temp"+temp);
			N_Left_right.set(x.id(), temp);
		}
		
		for(IndexVertex v :  SubsetToChooseFrom)
		{
			N_v_right = new VSubSet(groundSet);
			N_v_right.addAll(G.neighbours(v));
			N_v_right.retainAll(SubsetToChooseFrom);
			for(IndexVertex x : left)
			{
				VSubSet temp1 = new VSubSet(N_Left_right.get(x.id()));
				temp1.remove(v);
				if(N_v_right.equals(temp1))
				{
					//System.out.println("Chosing from case 3");
					return v;
				}
			}
			
		}
		
		//if no case satisfied then grow neighborhood
		int min=SubsetToChooseFrom.size();
		VSubSet min_of_N_left_right=new VSubSet(groundSet);//find what is minimum neighbourhood of vertex from left in right
		for(IndexVertex l:left)
		{
			VSubSet temp=new VSubSet(N_Left_right.get(l.id()));
			//System.out.println("min"+min);
			int Size_N_v = temp.size();
			if((Size_N_v>0)&&(Size_N_v<=min))
			{
				min = Size_N_v;
				min_of_N_left_right=temp;
			}
		}
		
		if(min==0||min_of_N_left_right.isEmpty())
		{
			choosen=G.MinDegreeVertex(SubsetToChooseFrom);
			//System.out.println("Chosing minimum right");
		}
		else
		{
			choosen= min_of_N_left_right.first();	
			//System.out.println("Chosing from grow neighborhood");
		}
		
		return choosen;
	}
	@Override
	public IndexVertex next(Collection<IndexVertex> A,
			Collection<IndexVertex> B, Collection<IndexVertex> C) {
		// TODO Auto-generated method stub
		return null;
	}

}

