package com.dpcat237.nps.behavior.factory.retrofit;


import com.dpcat237.nps.behavior.factory.retrofit.Responces.ArticleSyncResponce;
import com.dpcat237.nps.behavior.factory.retrofit.Responces.Dictation;
import com.dpcat237.nps.behavior.factory.retrofit.Responces.NewpselFeed;
import com.dpcat237.nps.behavior.factory.retrofit.Responces.SavedArticle;
import com.dpcat237.nps.behavior.factory.retrofit.Responces.ServerLaterItem;
import com.dpcat237.nps.behavior.factory.retrofit.Responces.ServerSharedItem;
import com.dpcat237.nps.behavior.factory.retrofit.Responces.SimpleResponce;
import com.dpcat237.nps.constant.ApiConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Alexey on 19.06.2016.
 */
public interface ApiInterface {

    @POST(ApiConstants.URL_SYNC_ITEMS_UNREAD)
    @FormUrlEncoded
    Call<ArticleSyncResponce[]> syncArticle(@Field("appKey") String appKey, @Field("limit") String limit, @Field("items") String items);

    @POST(ApiConstants.URL_ADD_FEED)
    @FormUrlEncoded
    Call<String[]> addFeed(@Field("appKey") String appKey, @Field("feed_url") String feed_url);

    @POST(ApiConstants.URL_GET_FEEDS)
    @FormUrlEncoded
    Call<NewpselFeed> getFeeds(@Field("appKey") String appKey, @Field("items") NewpselFeed[] items);

    @POST(ApiConstants.URL_ADD_GCM_ID)
    @FormUrlEncoded
    Call<SimpleResponce> addGcmId(@Field("appKey") String appKey, @Field("gcm_id") String gcm_id);

    @POST(ApiConstants.URL_SYNC_LABEL_ITEMS)
    @FormUrlEncoded
    Call<String[]> addSaved(@Field("appKey") String appKey, @Field("laterItems") ServerLaterItem[] items);

    @POST(ApiConstants.URL_SYNC_SHARED_ITEMS)
    @FormUrlEncoded
    Call<Integer> syncShared(@Field("appKey") String appKey, @Field("laterItems") ServerSharedItem[] items);

    @POST(ApiConstants.URL_SYNC_LATER_ITEMS)
    @FormUrlEncoded
    Call<SavedArticle[]> syncSavedArticle(@Field("appKey") String appKey, @Field("limit") int limit, @Field("later_items") String items, @Field("labels") String labels);

    @POST(ApiConstants.URL_SYNC_DICTATE_ITEMS)
    @FormUrlEncoded
    Call<Dictation[]> syncDictate(@Field("appKey") String appKey, @Field("limit") int limit, @Field("items") String items);

    @POST(ApiConstants.URL_SYNC_LABELS)
    @FormUrlEncoded
    Call<com.dpcat237.nps.behavior.factory.retrofit.Responces.Tag[]> syncLabel(@Field("appKey") String appKey, @Field("labels") String labels);

    @POST(ApiConstants.URL_SIGN_IN)
    @FormUrlEncoded
    Call<Integer> login(@Field("appKey") String appKey, @Field("email") String email, @Field("password") String password);

    @POST(ApiConstants.URL_SIGN_UP)
    @FormUrlEncoded
    Call<Integer> register(@Field("appKey") String appKey, @Field("email") String email, @Field("password") String password);

    @POST(ApiConstants.URL_RECOVERY_PASSWORD)
    @FormUrlEncoded
    Call<Integer> forgetPass(@Field("email") String email, @Field("password") String password);

}
