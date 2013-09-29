public class PrintVariable{

   public static void main(String[] args) {

       for(int i=0; i<2; i++){
           test(i);
       }
   }

   public static void test(int i) {

       int j = i*10;
       int m = j-34;
       int n = m/5;

       System.out.println("n:" + n);
   }

}
