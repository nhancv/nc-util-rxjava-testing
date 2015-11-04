import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.*;
import okio.Buffer;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

/**
 * Created by NhanCao on 28-Sep-15.
 */
public class RetrofitModel {
//    String BASE_URL = "http://test3.sunnypoint.jp";
    String BASE_URL = "http://192.168.1.122";
    String BASE_URLs = "https://192.168.1.122:8183";

//    private static final String TRUSTED_HOST_TEST3 = "test3.sunnypoint.jp";
    private static final String TRUSTED_HOST_TEST3 = "192.168.1.122";

    private static RetrofitModel instance = new RetrofitModel();

    public static RetrofitModel getInstance() {
        return instance;
    }

//    192.168.1.122
//            8183
//    http://192.168.1.122/appointment/api/v1.1/booking/getShopScheduleDetails?auth=abc&queryDate=2015/09/25&shopId=2173
//    https://192.168.1.122:8183/api/v1.3/shop/getShopInfo?shopId=2010&auth=abc

    interface ApiService {
        @GET("/appointment/api/v1.1/booking/getShopScheduleDetails?auth=abc&queryDate=2015-06-16&shopId=4")
        Call<ResponseBase> getShopScheduleDetails();

        @GET("/appointment/api/v1.1/shop/findShopInfo?shopId=4")
        Call<ResponseBase> findShopInfo();

        @GET("/appointment/api/v1.1/booking/getAllAppointments?customerId=438415")
        Call<ResponseBase> getAllAppointments();

//        https://192.168.1.122:8183/api/v1.3/shop/getShopInfo?shopId=2010&auth=abc
        @GET("/api/v1.3/shop/getShopInfo?shopId=2010&auth=abc")
        Call<ResponseBase> getShopInfo();
    }

    class ResponseBase {
        @SerializedName("status")
        public int status;//: 0,
    }


    private static OkHttpClient getUnsafeOkHttpClient(String TRUSTED_HOST) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Buffer buffer = new Buffer();
                    if (request.body() != null)
                        request.body().writeTo(buffer);
                    if (buffer.size() == 0) buffer.writeUtf8("nothing in body");
                    log(String.format("Method: %s - Request to %s with->\nBODY: %s", request.method(), request.urlString(), buffer.readUtf8()));
                    long t1 = System.nanoTime();
                    Response response = chain.proceed(request);
                    long t2 = System.nanoTime();
                    String msg = response.body().string();
                    log(String.format("Response from %s in %.1fms%n\n%s",
                            response.request().urlString(), (t2 - t1) / 1e6d, msg));
                    return response
                            .newBuilder()
                            .body(ResponseBody.create(response.body().contentType(), msg))
                            .build();
                }
            });
            if (TRUSTED_HOST != null) {
                okHttpClient.setHostnameVerifier((hostname, session) -> hostname.contains(TRUSTED_HOST));
            }
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void log(String msg) {
//        System.out.println(msg);
    }

    private Retrofit getRestAdapter(String BASE_URL, String TRUSTED_HOST) {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getUnsafeOkHttpClient(TRUSTED_HOST))
                .build();
        return restAdapter;
    }

    public void Run() {
        int count=0;
        while (count++<1000) {
            try {
//
//                Observable.just(getRestAdapter(BASE_URL, TRUSTED_HOST_TEST3).create(ApiService.class).getShopScheduleDetails().execute())
//                        .subscribeOn(Schedulers.newThread());
//                Observable.just(getRestAdapter(BASE_URL, TRUSTED_HOST_TEST3).create(ApiService.class).getAllAppointments().execute())
////                        .subscribeOn(Schedulers.computation());
//                        .subscribeOn(Schedulers.newThread());
//                Observable.just(getRestAdapter(BASE_URL, TRUSTED_HOST_TEST3).create(ApiService.class).findShopInfo().execute())
//                        .subscribeOn(Schedulers.newThread());
                Observable.just(getRestAdapter(BASE_URLs, TRUSTED_HOST_TEST3).create(ApiService.class).getShopInfo().execute())
                        .subscribeOn(Schedulers.newThread());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
