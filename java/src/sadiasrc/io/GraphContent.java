package sadiasrc.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GraphContent {
	//variables for storing graph content

	protected Scanner sc;
	protected int nodesNum;
	protected int edgeNum;
	protected ArrayList<int[]> edgeList = new ArrayList<int[]>();
	private HashMap<String, Integer> nodeLabelMap = null;
	private HashMap<String, Double> nodeweightMap = null;
	protected int minNodeNum;
	protected int maxNodeNum;
	public boolean emptyGraph;

	public void getGraphContent(String fileName)
	throws FileNotFoundException {
		sc = setFile(fileName);
		nodesNum = 0;
		edgeNum = 0;
		maxNodeNum = 0;
		minNodeNum = Integer.MAX_VALUE;
		emptyGraph = false;
		char label='0';
		int nodectr = 0;
		System.out.println("Reading the graph :"+fileName);

		// read header containing node and edge count
		while (sc.hasNext()|| sc.hasNextLine()) {

			if(sc.hasNext("c")) {
				label='c';
			} else if (sc.hasNext("p")) {
				label='p';
			} else if(sc.hasNext("n")) {
				label='n';
			} else if (sc.hasNext("e")) {
				label='e';
			} else if (sc.hasNext("d")) {
				label='d';
			} else if(sc.hasNext("v")) {
				label='v';
			} else if (sc.hasNext("x")) {
				label='x';
			} else {
				label='0';
			}

			//System.out.print(label);

			switch(label){
			case 'c'://this is just a comment
			{
			//	System.out.println(sc.nextLine());
				sc.nextLine();
				break;
			}
			case 'p'://problem description
			{
				sc.next();
				String  chk= sc.next();
				if (chk.startsWith("edge")||chk.startsWith("col")) {
					nodesNum = sc.nextInt();
					edgeNum = sc.nextInt();
					System.out.println("p edge "+nodesNum+" "+edgeNum);
					edgeList = new ArrayList<int[]>(edgeNum);
				}
				break;
			}
			case 'n'://node weight
			{
				sc.next();
				if (nodeLabelMap == null) {
					nodeLabelMap = new HashMap<String, Integer>();
				}
				if (nodeweightMap == null) {
					nodeweightMap = new HashMap<String, Double>();
				}

				String nodelabel = sc.next();
				// read weight. not used
				double weight=1.0;
				sc.next();
				
				// map from node label to weight
				nodeweightMap.put(nodelabel, weight);

				// map from node label to index
				nodeLabelMap.put(nodelabel, nodectr);
				nodectr++;
				break;
			}
			case 'e'://edge description
			{
				sc.next();
				int[] tab;
				if (nodeLabelMap == null) {
					tab = new int[] { sc.nextInt(), sc.nextInt() };
				} else {
					// assumes that all nodes have labels if at least one does
					tab = new int[] { nodeLabelMap.get(sc.next()),
							nodeLabelMap.get(sc.next()) };
				}
				minNodeNum = Math.min(minNodeNum, tab[0]);
				minNodeNum = Math.min(minNodeNum, tab[1]);
				maxNodeNum = Math.max(maxNodeNum, tab[0]);
				maxNodeNum = Math.max(maxNodeNum, tab[1]);
				edgeList.add(tab);
				break;
			}
			case 'd'://Geometric descriptor dimension metric
			{
				sc.nextLine();
				break;
			}
			case 'v'://vertex coordinate
			{
				sc.nextLine();
				break;
			}
			case 'x'://parameter and its value
			{
				sc.nextLine();
				break;
			}
			default://unknown character
			{
//				System.out.println("Unknown line identifier");
				sc.nextLine();
				break;
			}

			}//end switch

		}//end while

		sc.close();

		

		if (nodesNum == 0) {
			emptyGraph = true;
		}

	}
	@SuppressWarnings("unused")
	public static Scanner setFile(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if (file == null) {
			throw new FileNotFoundException("File not chosen");
		} else {
			return new Scanner(file);
		}
	}


}
