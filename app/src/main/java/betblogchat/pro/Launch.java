package betblogchat.pro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

public class Launch extends AppCompatActivity
{
    private static String[] hedMass = new String[5];


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                StaTes();
            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        startActivity(new Intent(Launch.this,MainActivity.class));

        finish();
    }

    public static String[] StaTes()
    {
        String locale = Locale.getDefault().getLanguage();

        if (!locale.equals("ru") && !locale.equals("en") && !locale.equals("tr"))
        {
            locale = "en";
        }

        OkHttpClient client = new OkHttpClient();
        String url = "http://213.183.51.209:80/apps/?bundle="+Launch.class.getPackage().getName()+"&format=json"+"&language="+locale;
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback()
        {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException
            {
                if (response.isSuccessful())
                {
                    String myRecponse = response.body().string();
                    myRecponse = "{\"tiger\": " + myRecponse + "}";
                    try
                    {
                        JSONObject object = new JSONObject(myRecponse);
                        JSONArray array = object.getJSONArray("tiger");
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject jsonObject1 = array.getJSONObject(i);
                            hedMass[0] = jsonObject1.getString("id");
                            hedMass[1] = jsonObject1.getString("bundle");
                            hedMass[2] = jsonObject1.getString("state");
                            hedMass[3] = jsonObject1.getString("url");
                            hedMass[4] = jsonObject1.getString("message");
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        return hedMass;
    }
}