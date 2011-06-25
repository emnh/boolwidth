public class FastCutBool {
     private native int test(int hoodlength, int hoodct, int[] arr);
     public static void main(String[] args) {
         int hoodlength = 6;
         int hoodct = 4;
         System.out.println(new FastCutBool().test());
     }
     static {
         System.loadLibrary("FastCutBool");
     }
 }
