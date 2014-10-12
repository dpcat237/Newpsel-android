package com.dpcat237.nps.behavior.factory.apiManager;

import android.util.Log;

import com.dpcat237.nps.behavior.manager.EasySSLSocketManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiManager {
    private static final String TAG = "NPS:ApiManager";
    protected JSONObject jsonData;
    protected Map<String, Object> result;
    protected Boolean error;
    protected String errorMessage;
    protected HttpClient httpClient;
    protected StringEntity jsonEntity;
    protected String jsonString;
    protected HttpResponse response;
    protected String httpResponse;

    public void setup(JSONObject jsonData) {
        this.jsonData = jsonData;
        result = new HashMap<String, Object>();
        error = false;
        errorMessage = "";
        setupHttpClient();
        setupExtra();
    }

    private void setupHttpClient() {
        // prepare for the https connection call this in the constructor of the
        // class that does the connection if it's used multiple times
        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // https scheme
        schemeRegistry.register(new Scheme("https", new EasySSLSocketManager(), 443));

        HttpParams params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf8");

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope("yourServerHere.com", AuthScope.ANY_PORT), new UsernamePasswordCredentials("YourUserNameHere", "UserPasswordHere"));
        ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);

        HttpContext context = new BasicHttpContext();
        context.setAttribute("http.auth.credentials-provider", credentialsProvider);
        httpClient = new DefaultHttpClient(clientConnectionManager, params);
    }

    public void makeRequest() {
        prepareData();
        if (error) {
            return;
        }

        execute();
    }

    private void prepareData() {
        try {
            jsonString = jsonData.toString();
            //Log.d(TAG, "tut: request: "+jsonString);
            jsonEntity = new StringEntity(jsonString);
            setRequestData(jsonEntity);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "tut: UnsupportedEncodingException: "+e.getMessage());
            error = true;
        }
    }

    private void execute() {
        executeRequest();
        //Log.d(TAG, "tut: httpResponse: "+httpResponse);
        if (error) {
            return;
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            getRequestResult();
        } else {
            error = true;
            errorMessage = httpResponse;
        }
    }

    public Map<String, Object> getResult() {
        result.put("error", error);
        result.put("errorMessage", errorMessage);

        return result;
    }

    abstract void setupExtra();
    abstract void setRequestData(StringEntity jsonEntity);
    abstract void executeRequest();
    abstract void getRequestResult();
}