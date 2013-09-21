public class TestParameter{

    public static void main(String[] args) throws Exception {

        int i=0;
        while(i<10000){

           test(i);
           test2(i);
        }
    }

   public static void test(int i) throws Exception{

       int j = i + 10;
       Thread.sleep(300);
       System.out.println("i:" + i + " j:" + j);
   }
   
   public static void test1(int j) {

       System.out.println("j:" + j);
   }


   public static void test2(int i) throws Exception{
       i = 0;
       while( i<5) {
           Thread.sleep(200);
           i++;
       }

   }

}
