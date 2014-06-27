package unfinished.ng.lib;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by emh on 4/26/2014.
 */
public class Util {
    public static String readFile(String fileName) {
        String s = null;
        try {
            File file = new File(fileName);
            Scanner sc = new Scanner(file);
            s = sc.useDelimiter("\\Z").next();
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
        return s;
    }
}
