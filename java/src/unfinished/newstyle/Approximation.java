package unfinished.newstyle;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Approximation {

	class State {
		int[] sample;
	}

    public int[] concat(int[] a, int[] b) {
        int[] ab = Arrays.copyOf(a, a.length + b.length);
        for (int i = 0; i < b.length; i++) {
            ab[a.length + i] = b[i];
        }
        return ab;
    }



	public void iterate(int[][] mat) {
        final int[] solct = new int[1];
        ArrayList<ArrayList<Integer>> solutions = new ArrayList<>();
		Function<State,Integer> itersub = (State state) -> {
            if (state.sample.length >= mat[0].length) {
                solct[0]++;
            } else {
                int zeroct = isPartialHood(concat(state.sample, new int[]{0}));
                int onect = isPartialHood(concat(state.sample, new int[]{0}));
            }
            return 0;
        };
		//itersub(new State({.sample = []}));
	}

    private int isPartialHood(int[] sample) {
        return 0;
    }

    public static String readFile(String file) {
        try(Stream<String> lines = Files.lines(FileSystems.getDefault().getPath(file))) {
            StringBuilder sb = new StringBuilder();
            lines.forEach((line) -> sb.append((line + System.lineSeparator())));
            String everything = sb.toString();
            return everything;
        } catch (IOException e) {
            System.out.println("fail to read " + file);
            System.exit(-1);
        }
        return "";
    }

    public static void main(String[] args) {
		int[][] mat = {
				{1,0,0},
				{0,1,0},
				{0,0,1}
		};
		//iterate(mat);

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");

        String js = readFile("D:\\dev\\boolwidth\\java\\src\\unfinished.newstyle\\test.js");

        try {
            engine.eval(js);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }
}