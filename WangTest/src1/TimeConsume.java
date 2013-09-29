public class TimeConsume{
    public static void main(String[] args) throws Exception {
        for(int i=0; i<100; i++ ){
           test();
           test2();
        }
    }

   public static void test() throws Exception{
       Thread.sleep(300);
       System.out.println("test function");
   }
   

   public static void test2() throws Exception{
       for(int i=0; i<5; i++){
           Thread.sleep(200);
       }
   }

}
