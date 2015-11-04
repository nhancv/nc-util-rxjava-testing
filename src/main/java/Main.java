import java.io.*;
import java.util.Scanner;

/**
 * Created by NhanCao on 14-Sep-15.
 */
public class Main {
    interface ICallback{
        void success();
        void error();
    }

    public void Solve(ICallback callback){

        try {
            defineWhatToDo();
            callback.success();
        } catch (Exception e) {
            e.printStackTrace();
            callback.error();
        }
    }
    private void defineWhatToDo() throws Exception{
        String find="SPMAAdapterMailDetails: ";

        PrintWriter writer = new PrintWriter(new File("test.html"), "UTF-8");
        Scanner scanner = new Scanner(new FileInputStream("input.txt"), "UTF-8");
        while(scanner.hasNext()){
            String s= scanner.nextLine();
            int last = s.lastIndexOf(find);
            if(last>-1){
                s=s.substring(last+find.length());
            }
            log(s);
            writer.println(s);
        }
        scanner.close();
        writer.close();
    }


    public static void log(Object msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {

        Main test= new Main();
        test.Solve(new ICallback() {
            @Override
            public void success() {
                log("sucess");
            }

            @Override
            public void error() {
                log("error");
            }
        });



//        testServer();



    }

    private static void testServer() {
        int numThread=10000;
        for (int i = 0; i < numThread; i++) {
            new Thread(() -> {
                RetrofitModel.getInstance().Run();
            }).start();
        }
    }
}
