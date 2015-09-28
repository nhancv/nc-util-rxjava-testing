import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by NhanCao on 14-Sep-15.
 */
public class Main {
    interface ICallback{
        void success();
        void error();
    }
    class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            log(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            log(String.format("Received response for %s in %.1fms%n%s %s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers(), response.body()));

            return response;
        }
    }
    public ArrayList<String> urlList;
    public void Solve(ICallback callback){

        try {
            OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new LoggingInterceptor());
            Request request = new Request.Builder()
                    .url(urlList.get(0))
                    .build();
            Response response = client.newCall(request).execute();
            response.body().close();
            callback.success();

        } catch (IOException e) {
            e.printStackTrace();
            callback.error();
        }
    }

    public void createUrlList(){
        urlList=new ArrayList<>();
        urlList.add("http://test3.sunnypoint.jp/appointment/api/v1.1/booking/getShopScheduleDetails?auth=abc&queryDate=2015-06-16&shopId=4");
    }
    public static void log(Object msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {
        int numThread=1000;
        for (int i = 0; i < numThread; i++) {
            new Thread(() -> {
                RetrofitModel.getInstance().Run();
            }).start();
        }
//        Main test= new Main();
//        test.createUrlList();
//        test.Solve(new ICallback() {
//            @Override
//            public void success() {
//                log("sucess");
//            }
//
//            @Override
//            public void error() {
//                log("error");
//            }
//        });

//        Timer timer = new Timer();
//
//        TimerTask delayedThreadStartTask = new TimerTask() {
//            @Override
//            public void run() {
//
//                //captureCDRProcess();
//                //moved to TimerTask
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                    }
//                }).start();
//            }
//        };
//
//        timer.schedule(delayedThreadStartTask, 60 * 1000); //1 minute
    }
}
