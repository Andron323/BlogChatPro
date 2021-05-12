package betblogchat.pro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.onesignal.OneSignal;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import tech.gusavila92.websocketclient.WebSocketClient;


public class MainActivity extends AppCompatActivity
{
    private static Intent intent_def;
    private WebView webView2;
    private SwipeRefreshLayout swipeRefreshLayout_main;
    private static ChipNavigationBar bootomNav;
    private Fragment selectedFragment = null;
    private FloatingActionButton btn_info, btn_close;
    private static TextView name_of_activity;
    private static CircleImageView indikator;
    private CircleImageView avatar_image_chat;
    private static final List<News_data> list_of_News_data = new ArrayList<News_data>();
    private static ArrayList<String> list_of_viev_id = new ArrayList<String>();
    private static ArrayList<String> list_of_like_id = new ArrayList<String>();
    private Object savedInstanceState;
    private static WebSocketClient webSocketClient;
    private SharedPreferences sharedPreferences;
    private String UNIKID = "UNIKID";
    private String IFFIRST = "IFFIRST";
    private String massages;


    public static Intent openUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
        intent_def = intent;
        return intent_def;
    }

    public static String doPostOkHttp(String ID, String likes_1, String views_1)
    {

        String locale = Locale.getDefault().getLanguage();

        RequestQueue requestQueue = Volley.newRequestQueue(MyApplication.getContext());


        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://213.183.51.209:80/news/" + ID, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("likes", likes_1);
                params.put("views", views_1);
                params.put("language", locale);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");

                return params;
            }
        };

        requestQueue.add(stringRequest);

        return "Server response Error";
    }

    public void Vibration()
    {

        long[] pattern = {100, 300, 400, 300, 200, 200};
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
        {
            vibrator.vibrate(pattern, -1);
        }
    }

    public void wotIsPage( String masTo)
    {
        AlphaAnimation blinkanimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(500); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(5); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        if (name_of_activity.getText().toString().equals(getBaseContext().getString(R.string.chat)))
        {
            indikator.setVisibility(View.GONE);
        }
        else
        {
            indikator.setVisibility(View.VISIBLE);
            Vibration();
            setPush(masTo);
            indikator.startAnimation(blinkanimation);
        }

    }

    private void setPush(String toMas)
    {
        RelativeLayout push = findViewById(R.id.push_layout);
        TextView push_text = findViewById(R.id.massage_push);
        push_text.setText(toMas);
        AlphaAnimation blinkanimation = new AlphaAnimation((float) 0.9, 1); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(1000); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(4); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);

        push.setVisibility(View.VISIBLE);
        push.startAnimation(blinkanimation);

        delPush();
    }

    private void delPush()
    {
        RelativeLayout push = findViewById(R.id.push_layout);
        push.setVisibility(View.INVISIBLE);
    }


    static class News_dataAdapter extends ArrayAdapter<News_data>
    {

        public News_dataAdapter(Context context)
        {
            super(context, R.layout.item_list_news, list_of_News_data);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint({"WrongViewCast", "ResourceType"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            News_data News_data = getItem(getCount() - position - 1);

            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_news, null);
            }

            if (!list_of_viev_id.contains(News_data.id))
            {
                ImageView ikon_view_news = convertView.findViewById(R.id.ikon_view_news);
                ikon_view_news.isFocusable();
                News_data.setViews(String.valueOf(Integer.parseInt(News_data.views) + 1));
                list_of_viev_id.add(News_data.id);

                doPostOkHttp(News_data.id, "0", "1");
            }

            ((TextView) convertView.findViewById(R.id.data_news)).setText(News_data.date_time);
            ((TextView) convertView.findViewById(R.id.view_news)).setText(News_data.views);
            ((TextView) convertView.findViewById(R.id.content_news)).setText(News_data.content);

            if (!(News_data.image.equals("null")))
            {
                ((ImageView) convertView.findViewById(R.id.img_news)).setVisibility(View.VISIBLE);
                ImageView img = (ImageView) convertView.findViewById(R.id.img_news);
                Picasso.get().load(News_data.image)//достаем ссылку на фото
                        .fit().centerCrop()//полное заполнение и выравнивание из центра
                        .into(img);//куда вставить фото
            }
            else
            {
                ((ImageView) convertView.findViewById(R.id.img_news)).setVisibility(View.GONE);
            }

            String a = News_data.url_for_but;
            if (News_data.url == null || News_data.url.equals(""))
            {
                ((RelativeLayout) convertView.findViewById(R.id.batn_like)).setVisibility(View.VISIBLE);
                TextView like_news = ((TextView) convertView.findViewById(R.id.like_news));
                like_news.setText(News_data.likes);
                ImageView ikon_like_news = convertView.findViewById(R.id.ikon_like_news);
                ((TextView) convertView.findViewById(R.id.url_news)).setVisibility(View.GONE);
                RelativeLayout batn_like = ((RelativeLayout) convertView.findViewById(R.id.batn_like));
                RelativeLayout batn_like2 = ((RelativeLayout) convertView.findViewById(R.id.batn_like2));
                TextView like_news2 = ((TextView) convertView.findViewById(R.id.like_news2));
                if (!list_of_like_id.contains(News_data.id))
                {
                    batn_like.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            News_data.setLikes(String.valueOf(Integer.parseInt(News_data.likes) + 1));

                            doPostOkHttp(News_data.id, "1", "0");

                            batn_like.setVisibility(View.GONE);
                            batn_like2.setVisibility(View.VISIBLE);
                            like_news2.setText(News_data.likes);
                            list_of_like_id.add(News_data.id);
                        }
                    });
                }
                else
                {
                    batn_like.setVisibility(View.GONE);
                    batn_like2.setVisibility(View.VISIBLE);
                    like_news2.setText(News_data.likes);
                }

            }
            else
            {
                ((TextView) convertView.findViewById(R.id.url_news)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.url_news)).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        openUrl(News_data.url);
                    }
                });
                ((RelativeLayout) convertView.findViewById(R.id.batn_like)).setVisibility(View.GONE);
            }
            WebView video = ((WebView) convertView.findViewById(R.id.video_news));
            if (News_data.video.equals("") || News_data.video.equals("null"))
            {
                video.setVisibility(View.GONE);
            }
            else
            {
                video.setVisibility(View.VISIBLE);
                video.loadUrl(News_data.video);
                WebSettings videoSettings = video.getSettings();
            }
            return convertView;
        }
    }

    public static void RefreshList()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String locale = Locale.getDefault().getLanguage();

                if (!locale.equals("ru") && !locale.equals("en") && !locale.equals("tr"))
                {
                    locale = "en";
                }

                OkHttpClient client = new OkHttpClient();
                String url = "http://213.183.51.209:80/news/?format=json&language=" + locale;
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
                            myRecponse = "{\"news\": " + myRecponse + "}";
                            list_of_News_data.clear();
                            try
                            {
                                JSONObject object = new JSONObject(myRecponse);
                                JSONArray array = object.getJSONArray("news");
                                for (int i = 0; i < array.length(); i++)
                                {
                                    JSONObject jsonObject1 = array.getJSONObject(i);
                                    String id = jsonObject1.getString("id");
                                    String url = jsonObject1.getString("url");
                                    String url_for_but = jsonObject1.getString("url_for_but");
                                    String content = jsonObject1.getString("content");
                                    String image = jsonObject1.getString("image");
                                    String video = jsonObject1.getString("video");
                                    String likes = jsonObject1.getString("likes");
                                    String views = jsonObject1.getString("views");
                                    String date_time = jsonObject1.getString("date_time");
                                    String language = jsonObject1.getString("date_time");
                                    System.out.println(video);
                                    if (content.equals("null"))
                                    {
                                        content = "";
                                    }

                                    list_of_News_data.add(new News_data(id, url, url_for_but, content, image, video, likes, views, date_time, language));

                                }
                                System.out.println(list_of_News_data);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
    }


    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        RefreshList();
        createWebSocketClient();

        indikator = findViewById(R.id.indikator);

        bootomNav = findViewById(R.id.bottom_nav_bar);
        bootomNav.setOnItemSelectedListener(navListener);
        bootomNav.setItemSelected(R.id.tab_news, true);
        swipeRefreshLayout_main = findViewById(R.id.swiperefresh_main);

        btn_info = findViewById(R.id.btn_info);
        btn_close = findViewById(R.id.btn_close);
        btn_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Info_Fragment()).commit();
                btn_info.setVisibility(View.INVISIBLE);
                btn_close.setVisibility(View.VISIBLE);
                bootomNav.setVisibility(View.INVISIBLE);
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String now_txt = name_of_activity.getText().toString();
                if (now_txt.equals(getBaseContext().getString(R.string.info)))
                {
                    selectedFragment = new Info_Fragment();
                }
                if (now_txt.equals(getBaseContext().getString(R.string.browser)))
                {
                    selectedFragment = new Brouser_Fragment();
                }
                if (now_txt.equals(getBaseContext().getString(R.string.news)))
                {
                    selectedFragment = new News_Fragment();
                }
                if (now_txt.equals(getBaseContext().getString(R.string.chat)))
                {
                    selectedFragment = new Chat_Fragment();
                }
                btn_info.setVisibility(View.VISIBLE);
                btn_close.setVisibility(View.INVISIBLE);
                bootomNav.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
        });

        webView2 = findViewById(R.id.webView2);
        lodWW();

        swipeRefreshLayout_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                Toast.makeText(MainActivity.this, R.string.upload, Toast.LENGTH_SHORT).show();
                lodWW();
                swipeRefreshLayout_main.setRefreshing(false);
            }
        });

        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);


        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(20000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                sharedPreferences = MyApplication.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                String IFnew = sharedPreferences.getString(IFFIRST, "null");
                if (IFnew.equals("null"))
                {
                    // в случае если в хранилище нет click_id, генерируем новый
                    IFnew = "1";
                    sharedPreferences = MyApplication.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putString(IFFIRST, IFnew);
                    ed.commit();
                    webSocketClient.send("{\"room\":\"" + Cl_ID() + "\",\"timestamp\":\"" + timeText + "\",\"name_from\":\"admin\",\"message\":\"" + Launch.StaTes()[4] + "\",\"command\":\"new_message\"}");
                }
            }
        });
        thread.start();

    }


    private void createWebSocketClient()
    {
        URI uri;
        try
        {
            uri = new URI("ws://213.183.51.209:8001/ws/chat/" + Cl_ID() + "/");
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
            return;
        }
        webSocketClient = new WebSocketClient(uri)
        {
            @Override
            public void onOpen()
            {
            }

            @Override
            public void onTextReceived(String s)
            {
                final String message = s;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            JSONObject object = new JSONObject("{\"sms\": [" + message + "]}");
                            JSONArray array = object.getJSONArray("sms");
                            for (int i = 0; i < array.length(); i++)
                            {
                                JSONObject jsonObject1 = array.getJSONObject(i);
                                String message = jsonObject1.getString("message");
                                String name_from = jsonObject1.getString("name_from");
                                String timestamp = jsonObject1.getString("timestamp");

                                wotIsPage(message);

                                Chat_Fragment fragment = (Chat_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                fragment.retMassage(message, name_from, timestamp);
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data)
            {
            }

            @Override
            public void onPingReceived(byte[] data)
            {
            }

            @Override
            public void onPongReceived(byte[] data)
            {
            }

            @Override
            public void onException(Exception e)
            {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived()
            {
                System.out.println("onCloseReceived");
            }
        };
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    public static void sendMessage(View view, String getMass, String getId, String dataTime)
    {
        webSocketClient.send("{\"room\":\"" + getId + "\",\"timestamp\":\"" + dataTime + "\",\"name_from\":\"\",\"message\":\"" + getMass + "\",\"command\":\"new_message\"}");
    }


    private String Cl_ID()
    {
        sharedPreferences = MyApplication.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String unikId = sharedPreferences.getString(UNIKID, "null");
        if (unikId.equals("null"))
        {
            // в случае если в хранилище нет click_id, генерируем новый
            unikId = UUID.randomUUID().toString().replace("-", "");
            sharedPreferences = MyApplication.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(UNIKID, unikId);
            ed.commit();
        }
        return unikId;
    }


    private void lodWW()
    {
        if (savedInstanceState == null)
        {
            webView2.setWebViewClient(new WebViewClient()
                                      {
                                          @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                          @Override
                                          public boolean shouldOverrideUrlLoading(WebView view, String url)
                                          {
                                              CookieManager.getInstance().setAcceptThirdPartyCookies(view, true);
                                              view.loadUrl(url);
                                              return true;
                                          }

                                      }
            );

            if (Launch.StaTes()[3] != null)
            {
                webView2.loadUrl(Launch.StaTes()[3]);
            }
            else
            {
                webView2.loadUrl("");
            }
            WebSettings webSettings = webView2.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webView2.setWebChromeClient(new WebChromeClient()
            {
            });
        }
    }


    @Override
    public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        webView2.saveState(outState);
    }

    private ChipNavigationBar.OnItemSelectedListener navListener = new ChipNavigationBar.OnItemSelectedListener()
    {


        @SuppressLint({"NonConstantResourceId", "ResourceAsColor"})
        @Override
        public void onItemSelected(int i)
        {
            name_of_activity = findViewById(R.id.name_of_activity);

            switch (i)
            {
                case R.id.tab_browser:

                    if (Launch.StaTes()[2] != null)
                    {
                        if (Launch.StaTes()[2].equals("false"))
                        {
                            name_of_activity.setText(R.string.browser);
                            addAvatar();
                            selectedFragment = new Brouser_Fragment();
                        }
                        else
                        {
                            name_of_activity.setText(R.string.info);
                            addAvatar();
                            selectedFragment = new Info_Fragment();
                        }
                    }
                    else
                    {
                        name_of_activity.setText(R.string.info);
                        addAvatar();
                        selectedFragment = new Info_Fragment();
                    }
                    break;
                case R.id.tab_news:

                    name_of_activity.setText(R.string.news);
                    addAvatar();
                    selectedFragment = new News_Fragment();
                    break;
                case R.id.tab_chat:
                    name_of_activity.setText(R.string.chat);
                    addAvatar();
                    wotIsPage("n");
                    selectedFragment = new Chat_Fragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    };

    private void addAvatar()
    {
        try
        {
            avatar_image_chat = findViewById(R.id.avatar_image_chat);
            if (name_of_activity.getText().toString().equals(getBaseContext().getString(R.string.chat)))
            {
                avatar_image_chat.setVisibility(View.VISIBLE);
            }
            else
            {
                avatar_image_chat.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if (webView2.canGoBack())
                    {
                        webView2.goBack();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}