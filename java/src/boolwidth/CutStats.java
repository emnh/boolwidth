package boolwidth;

import graph.BiGraph;
import graph.Vertex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CutStats {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int[][][][] num = new int[20][20][19 * 19 + 1][20];
		double[][][][] avg = new double[20][20][19 * 19 + 1][20];

		String fileName = "cutStats.stat";
		// starting timer
		// long start = System.currentTimeMillis();
		File file = new File(fileName);
		file.createNewFile();
		Scanner sc = new Scanner(file);
		while (sc.hasNextInt()) {
			int i1 = sc.nextInt();
			int i2 = sc.nextInt();
			int i3 = sc.nextInt();
			int i4 = sc.nextInt();
			int n = sc.nextInt();
			double a = sc.nextDouble();
			num[i1][i2][i3][i4] = n;
			avg[i1][i2][i3][i4] = a;
		}
		int iter = 100000;
		for (int a = 0; a < iter; a++) {
			if (a % (iter / 100) == 0) {
				System.out.println(100 * a / iter + "%");
			}
			int left = 19;
			int right = 10;
			int m = (int) (Math.random() * left * right);
			BiGraph<Integer, String> h = BiGraph.random(left, right, m);
			// System.out.println(h);
			int before = CutBool.countNeighborhoods(h);
			// [left][right][m][degree]

			Vertex<Integer> v = h.leftVertices().iterator().next();
			int degree = h.degree(v);
			h.removeVertex(v);
			// System.out.println(h);
			int after = CutBool.countNeighborhoods(h);
			// System.out.println("Removed degree "+degree+" vertex.\nBefore: "+before+" After: "+after);
			int tn = num[left][right][m][degree];
			double ta = avg[left][right][m][degree];
			avg[left][right][m][degree] = (ta * tn + (double) after
					/ (double) before)
					/ (tn + 1);
			num[left][right][m][degree]++;
			// System.out.println(avg[left][right][m][degree]);
		}
		try {
			FileWriter fw = new FileWriter(file);
			for (int i1 = 0; i1 < num.length; i1++) {
				for (int i2 = 0; i2 < num[i1].length; i2++) {
					for (int i3 = 0; i3 < num[i1][i2].length; i3++) {
						for (int i4 = 0; i4 < num[i1][i2][i3].length; i4++) {
							if (num[i1][i2][i3][i4] != 0) {
								fw.append(i1 + " " + i2 + " " + i3 + " " + i4
										+ " " + num[i1][i2][i3][i4] + " "
										+ avg[i1][i2][i3][i4] + "\n");
							}
						}
					}
				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.err.println("Output file not found");
			e.printStackTrace();
		}

		double[] degavg = new double[20];
		int[] degnum = new int[20];
		for (int i3 = 0; i3 < num[19][10].length; i3++) {
			for (int i4 = 0; i4 < num[19][10][i3].length; i4++) {
				degavg[i4] += avg[19][10][i3][i4] * num[19][10][i3][i4];
				degnum[i4] += num[19][10][i3][i4];
			}
		}
		for (int i4 = 0; i4 < degavg.length; i4++) {
			System.out.println(degavg[i4] / degnum[i4]);
		}
	}
}
