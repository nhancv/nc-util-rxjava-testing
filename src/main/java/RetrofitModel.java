import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.*;
import okio.Buffer;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

/**
 * Created by NhanCao on 28-Sep-15.
 */
public class RetrofitModel {
    String BASE_URL = "http://test3.sunnypoint.jp";

    private static final String TRUSTED_HOST_TEST3 = "test3.sunnypoint.jp";

    private static RetrofitModel instance = new RetrofitModel();

    public static RetrofitModel getInstance() {
        return instance;
    }

    interface ApiService {
        @GET("/appointment/api/v1.1/booking/getShopScheduleDetails?auth=abc&queryDate=2015-06-16&shopId=4")
        Call<ResponseBase> getShopScheduleDetails();
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
        System.out.println(msg);
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
        try {
            getRestAdapter(BASE_URL, TRUSTED_HOST_TEST3).create(ApiService.class).getShopScheduleDetails().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
