package sadiasrc.decomposition;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import sadiasrc.util.IndexedSet;

public class CountMIS {
	private static ArrayList<VSubSet> neighbourhoods;
	private static IndexedSet<IndexVertex> groundSet;
	public static long count=0;
	public static long trymod=0;
	static Boolean Ginf=true;

	public static long countMIS(IndexGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}
//		for(int i=0; i<G.numVertices(); i++)
//		{
//			System.out.println(neighbourhoods.get(i));
//		}
		
		
		VSubSet F = new VSubSet(groundSet);
		VSubSet M = new VSubSet(groundSet);
		F.addAll((Collection<? extends IndexVertex>) G.vertices());
		return countMIS(G,F,M);
	}


	/**
	 * @param G The graph of which we want to compute the MIS
	 * @param F free vertices
	 * @param M marked vertices
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long countMIS(IndexGraph G,VSubSet F,  VSubSet M)
	{
		trymod++;
		//System.out.println(G);
//		System.out.println("F"+F);
//		System.out.println("M"+M);
//		
		//checking termination conditions
		
		
		//H1
		if(F.isEmpty()&&M.isEmpty())
		{
//			System.out.println("Returning 1");
			return 1;
		}
		
		
		//H2
		for(IndexVertex u:M)
		{
			VSubSet N_Fu= new VSubSet(groundSet);
			N_Fu = new VSubSet(neighbourhoods.get(u.id()));
			N_Fu.retainAll(F);
			if(N_Fu.size()==0)
				return 0;				
		}
		
		//checking connected components
		Boolean con;
		VSubSet all = new VSubSet(groundSet);
		all.addAll(F);
		all.addAll(M);
		con = BasicGraphAlgorithms.isConnected(G,all);
		
		if(!con)
		{
			
			long total=1;
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(M);
				nrest.retainAll(F);

				total *= countMIS(G, nrest,nout);
//				System.out.println("total = "+total);
			}

			return total;
		}

		if(Ginf)
		{//start checkin if mod happens
			//R1
			//System.out.println("Checking red rules");
		for(IndexVertex u:M)
		{
			VSubSet N_Fu;
			VSubSet N_v=null;
			N_Fu = new VSubSet(neighbourhoods.get(u.id()));
			N_Fu.retainAll(F);
			if(N_Fu.size()==1)
			{
				IndexVertex v = null;
				v= N_Fu.first();
				N_v = new VSubSet(neighbourhoods.get(v.id()));
				VSubSet Mn=new VSubSet(M);
				VSubSet Fn=new VSubSet(F);
				Mn.removeAll(N_v);//M\N(v)
				
				Fn.removeAll(N_v);//F\N(v)
				Fn.remove(v);//F\N[v]
//				System.out.println("In rule 1");
				return countMIS(G,Fn,Mn);	
			}
		}
		
		//R2
		
		for(IndexVertex u:F)
		{
			VSubSet N_Fu= new VSubSet(groundSet);
			VSubSet N_u= new VSubSet(groundSet);
			
			N_Fu = new VSubSet(neighbourhoods.get(u.id()));
			N_Fu.retainAll(F);
//			System.out.println("N"+u+"="+N_Fu);
			N_u = neighbourhoods.get(u.id());
			if(N_Fu.size()==0)
			{
				VSubSet Mn=new VSubSet(M);
				VSubSet Fn=new VSubSet(F);
				Mn.removeAll(N_u);//M\N(u)
				Fn.removeAll(N_u);//F\N(u)
				Fn.remove(u);//F\N[u]
//				System.out.println("In rule 2");
				return countMIS(G,Fn,Mn);	
			}
		}
		
//		//R3
//		IndexEdge<IndexVertex> ep=null;
		Stack<IndexEdge<IndexVertex>> removedEdges = new Stack<IndexEdge<IndexVertex>>();
		for(IndexVertex u:M)
		{
			for(IndexVertex v : M)
			{
				if(neighbourhoods.get(u.id()).contains(v))
				{
					//remove the edge u,v
					removedEdges.push(new IndexEdge<IndexVertex>(G, -1, u, v));
					//Update neighborhoods
				}
			}
		}
		if(!removedEdges.isEmpty())
		{
//			System.out.println("In rule 3");
			for(IndexEdge<IndexVertex> e : removedEdges)
			{
				for(IndexVertex ev : e.endVertices())
				{
					neighbourhoods.get(ev.id()).remove(e.opposite(ev));
				}
			}
			long temp = countMIS(G,F,M);
			for(IndexEdge<IndexVertex> e : removedEdges)
			{
				//Update neighborhoods
				for(IndexVertex ev : e.endVertices())
				{
					neighbourhoods.get(ev.id()).add(e.opposite(ev));
				}
			}
			return temp;			
		}
//		
		//R6
		VSubSet N_u= new VSubSet(groundSet);
		VSubSet N_v= new VSubSet(groundSet);
		for(IndexVertex u:M)
		{
			for(IndexVertex v:M)
			{
				if(!u.equals(v))
				{
					N_u = neighbourhoods.get(u.id());
					N_v = neighbourhoods.get(v.id());
					
					if(N_u.equals(N_v))//N(u)=N(v)
					{
						M.remove(v);
//						System.out.println("In rule 6");
						long temp= countMIS(G,F,M);
						M.add(v);
						return temp;
					}
				}
			}
		}
		
//R5
		
		for(IndexVertex u:M)
		{
			for(IndexVertex v:G.neighbours(u))
			{
				N_u = neighbourhoods.get(u.id());
				N_v = neighbourhoods.get(v.id());
				if(N_u.containsAll(N_v))
				{
					M.remove(u);
//					System.out.println("In rule 5");
					long temp=countMIS(G,F,M);
					M.add(u);
					return temp;
				}
			}
		}
		
		
		
//		//R4

/*		long count=0,mis=0;
		for(IndexVertex v:F)
		{
			for(IndexVertex u:F)
			{
				if(!u.equals(v))
				{
					N_u = new VSubSet(neighbourhoods.get(u.id()));
					N_v = new VSubSet(neighbourhoods.get(v.id()));
					N_u.add(u);
					N_v.add(v);
					if(N_u.equals(N_v))//N[u]=N[v]
					{
						VSubSet Fn= new VSubSet(F);
						Fn.remove(v);
//						System.out.println("In rule 4");
						count = countMIS(G,Fn,M);
						Fn.remove(u);
						mis = countMIS(G,Fn,M);	
//						System.out.println("In rule 4");
						return 2*count-mis;
					}
							
					
				}
			}
		}
		
		*/
		
		
		//R7
		VSubSet FUM = new VSubSet(groundSet);
//		VSubSet N_u= new VSubSet(groundSet);
//    	VSubSet N_v= new VSubSet(groundSet);
		FUM=VSubSet.union(F,M);
		for(IndexVertex u:FUM)
		{
			for(IndexVertex v:F)
			{
				if(!u.equals(v))
				{
					N_u = neighbourhoods.get(u.id());
					N_v = neighbourhoods.get(v.id());
					
					if(G.neighbours(u).equals(G.neighbours(v)))//N(u)=N(v)
					{
//						System.out.println("N_u"+N_u+u);
//						System.out.println("N_v"+N_v+v);
						F.remove(v);
//						System.out.println("In rule 7");
						long temp = countMIS(G,F,M);
						F.add(v);
						return temp;
					}
				}
			}
		}
//		
		
		}
		//end checking where to check mod
//		
//		
		//Branching Rule
		IndexVertex u = null ;
		boolean found=false;
		for(IndexVertex v:M)
		{
			if(G.degree(v)==2)
			{
				found=true;
				u=v;
				break;
			}
				
		}
		if(!found)
		{
			VSubSet FuM = new VSubSet(groundSet);
			FuM=FuM.union(F,M);
		//	System.out.println("FuM"+FuM);
			u=G.MinDegreeVertex(FuM);
			
		}
//		System.out.println("Choosen u"+u);
		
		
		//BL(u)	
		VSubSet BL_u = new VSubSet(groundSet);
		BL_u = new VSubSet(neighbourhoods.get(u.id()));
		BL_u.retainAll(F);
//		System.out.println("BL"+BL_u);
		ArrayList<IndexVertex> OrderedBL_u = new ArrayList<IndexVertex>(BL_u.size());
		//Ordered BL_u
		if(!BL_u.isEmpty())
		{
		
		VSubSet NF_u = new VSubSet(groundSet);
		NF_u=BL_u;
		
		
		VSubSet nfu=new VSubSet(groundSet);
		nfu=new VSubSet(neighbourhoods.get(u.id()));
		nfu.retainAll(F);
		int min_neighbor=Integer.MAX_VALUE;
		IndexVertex v1=null;
		for(IndexVertex m1:BL_u)
		{
			VSubSet V_N_u= new VSubSet(groundSet);
			V_N_u.addAll((Collection<? extends IndexVertex>) G.vertices());
			V_N_u.removeAll(neighbourhoods.get(u.id()));
			VSubSet N_m1 = new VSubSet(neighbourhoods.get(m1.id()));
			
			V_N_u.retainAll(N_m1);
			if(V_N_u.size()<min_neighbor)
				v1=m1;
			
		}
		//System.out.println("v1 found"+v1);
		OrderedBL_u.add(v1);
		//System.out.println("BL"+BL_u);
		for(IndexVertex p:BL_u)
			nfu.add(p);
//		System.out.println("nfu"+nfu);
//		System.out.println("NF_u"+NF_u);
		VSubSet nv1=new VSubSet(neighbourhoods.get(v1.id()));
		
//		System.out.println("nv1"+nv1);
		
		for(IndexVertex e:nv1)
		{
			if(NF_u.contains(e))
				OrderedBL_u.add(e);
		}
		for(IndexVertex e:nv1)
		{
			if(NF_u.contains(e))
				NF_u.remove(e);
		}
//		System.out.println("NF_u"+NF_u);
//		System.out.println("BL"+BL_u);
		nfu.removeAll(neighbourhoods.get(v1.id()));
		nfu.remove(v1);
//		System.out.println("nfu"+nfu);
		while(!nfu.isEmpty())
		{
			int min_N=Integer.MAX_VALUE;
			for(IndexVertex m1:nfu)
			{
				VSubSet V_N_u= new VSubSet(groundSet);
				V_N_u.addAll((Collection<? extends IndexVertex>) G.vertices());
				V_N_u.removeAll(neighbourhoods.get(u.id()));
				VSubSet N_m1 = new VSubSet(groundSet);
				N_m1= neighbourhoods.get(m1.id());
				V_N_u.retainAll(N_m1);
				if(V_N_u.size()<min_N)
					v1=m1;				
			}
			OrderedBL_u.add(v1);
			nfu.remove(v1);
			
		}
		}
		
//		System.out.println("OrdererBL"+OrderedBL_u);
//		System.out.println("F before branch"+F);
//		System.out.println("M before branch"+M);
		
		count=0;
		VSubSet Ne_u = new VSubSet(groundSet);
//		System.out.println("u"+u);
		if(F.contains(u))
		{
			for(IndexVertex c:G.neighbours(u))
				Ne_u.add(c);
			
//			System.out.println("Ne_u"+Ne_u);
			Stack<IndexVertex> removedM = new Stack<IndexVertex>();
			Stack<IndexVertex> removedF = new Stack<IndexVertex>();
			for(IndexVertex x:Ne_u)
			{
				if(M.contains(x))
				{
					M.remove(x);//M\N(u)
					removedM.push(x);
				}
				if(F.contains(x))
				{
					F.remove(x);//F\N[u]
					removedF.push(x);
				}
			}
			F.remove(u);
			removedF.push(u);
			
			//
//			System.out.println("Calling from branch with for "+u+" with ("+F+","+M+")");
			count = countMIS(G,F,M);	
//			System.out.println("F after branch"+F);
//			System.out.println("M after branch"+M);
//			System.out.println("Returned value "+count);
//			
			F.addAll(removedF);
			M.addAll(removedM);
			
			
		}
//		System.out.println("Ordered BL"+OrderedBL_u);
//		System.out.println("F before for loop"+F);
//		System.out.println("M before for loop"+M);
		
		for(int i=0;i<OrderedBL_u.size();i++)
		{
			IndexVertex V_i=OrderedBL_u.get(i);
			VSubSet Mprime= new VSubSet(groundSet);
			for(int j=0;j<i;j++)
			{	
				IndexVertex V_j=OrderedBL_u.get(j);
//				System.out.println("V_i"+V_i+"V_j"+V_j);
				if(!G.areAdjacent(V_i, V_j))
				{
//					System.out.println("Adding "+V_j+"to MP");
					Mprime.add(V_j);
					
				}
			}
			VSubSet N_Vi= new VSubSet(groundSet);
			Stack<IndexVertex> removedM = new Stack<IndexVertex>();
			Stack<IndexVertex> removedF = new Stack<IndexVertex>();
			for(IndexVertex c:G.neighbours(V_i))
				N_Vi.add(c);
			//N_Vi=neighbourhoods.get(V_i.id());//N(Vi)
//			System.out.println("MPrime"+Mprime);
			VSubSet Mn=VSubSet.union(Mprime, N_Vi);
			
			for(IndexVertex b:Mn)
			{
				if(F.contains(b))
				{
					F.remove(b);
					removedF.push(b);
				}
			}
			F.remove(V_i);
			removedF.push(V_i);
			
			
			Mn=VSubSet.union(M,Mprime);
			Mn.removeAll(N_Vi);
//			for(IndexVertex b:N_Vi)
//			{
//				if(M.contains(b))
//				{
//					M.remove(b);
//					removedM.push(b);
//				}
//				
//			}
//			System.out.println("Calling from for loop for  including vi"+V_i);
//			System.out.println("not including v1-v_(i-1)");
//			for(int j=0;j<i;j++)
//				System.out.print(OrderedBL_u.get(j)+",");
			count+= countMIS(G,F,Mn);
			
//			M.addAll(removedM);
			F.addAll(removedF);

		}
		
		return count;

	}

}
