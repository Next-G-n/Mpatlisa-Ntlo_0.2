package com.example.mpatlisantlo.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorizatipon:key=AAAAIzX7v1k:APA91bFHqgJ8HD-1v5ltMu9hFgfNII8qpg5TEpZqUGeUdYldxFEF22FX-6wvBDUDNNJJuBBgSdTSL4Ogh5wzjOoSNgAvEoZ4muIdaCkd4L1qSwRc6X3H-HS4bs1YzhvRPIbIHB0oSb2N"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
