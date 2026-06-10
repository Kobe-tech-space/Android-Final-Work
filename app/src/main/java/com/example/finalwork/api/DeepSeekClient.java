package com.example.finalwork.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeepSeekClient {
    // 默认 Key（用户未配置时使用）
    private static String apiKey = "YOUR_DEEPSEEK_API_KEY";
    private static final String BASE_URL = "https://api.deepseek.com/";
    private static DeepSeekApi api;

    public static void setApiKey(String key) {
        if (key != null && !key.trim().isEmpty()) {
            apiKey = key.trim();
        }
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static boolean isKeyConfigured() {
        return apiKey != null && !apiKey.isEmpty()
                && !apiKey.equals("YOUR_DEEPSEEK_API_KEY");
    }

    public static DeepSeekApi getApi() {
        if (api == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(120, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(DeepSeekApi.class);
        }
        return api;
    }

    /** 强制重建 Retrofit 实例（API Key 刷新后调用） */
    public static void reset() {
        api = null;
    }
}
