package betblogchat.pro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import tech.gusavila92.websocketclient.WebSocketClient;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chat_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat_Fragment extends Fragment
{
    private ArrayAdapter<Massage> adapter;
    private ImageView img_update;
    private TextView txt_for_update;
    private String dateText;
    private String timeText;
    private static final List<Massage> list_of_massage_cerver = new ArrayList<Massage>();
    private SwipeRefreshLayout swipeRefreshLayout_chat;

    private String TAG = "CoinWeb";
    private SharedPreferences sharedPreferences;
    private String UNIKID = "UNIKID";
    private EditText writ_mass;
    private MediaPlayer mediaPlayer;
    private WebSocketClient webSocketClient;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Chat_Fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Support.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat_Fragment newInstance(String param1, String param2)
    {
        Chat_Fragment fragment = new Chat_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void soundOfMassage()
    {
        mediaPlayer = MediaPlayer.create(getContext(),R.raw.sound);
        mediaPlayer.start();
    }

    private String Cl_ID()
    {
        sharedPreferences = MyApplication.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String unikId = sharedPreferences.getString(UNIKID, "null");
        if (unikId.equals("null"))
        {
            unikId = UUID.randomUUID().toString().replace("-", "");
            sharedPreferences = MyApplication.getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(UNIKID, unikId);
            ed.commit();
        }
        return unikId;
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        img_update = v.findViewById(R.id.img_update);
        txt_for_update = v.findViewById(R.id.txt_for_update);

        ListView list_of_massag = v.findViewById(R.id.list_of_massag);
        adapter = new News_dataAdapter2(getActivity());
        adapter.notifyDataSetChanged();
        list_of_massag.setAdapter(adapter);
        oldMassage();


        if (list_of_massage_cerver.isEmpty())
        {
        }else {
            img_update.setVisibility(View.GONE);
            txt_for_update.setVisibility(View.GONE);
        }

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateText = dateFormat.format(currentDate);
        timeText = timeFormat.format(currentDate);

        writ_mass = v.findViewById(R.id.massegFilt);
        FloatingActionButton btn_send = v.findViewById(R.id.btn_send);
        writ_mass.setHint(R.string.massage___);

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String mass = writ_mass.getText().toString();
                String dataTime = dateText + "/" + timeText;
                MainActivity.sendMessage(getView(), mass, Cl_ID(), dataTime);
                writ_mass.setText("");
                if (!list_of_massage_cerver.isEmpty())
                {
                    img_update.setVisibility(View.GONE);
                    txt_for_update.setVisibility(View.GONE);

                }
                adapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout_chat = v.findViewById(R.id.swiperefresh_chat);
        swipeRefreshLayout_chat.setColorSchemeColors(R.color.blue_deff);
        swipeRefreshLayout_chat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                oldMassage();
                adapter.notifyDataSetChanged();
                if (!list_of_massage_cerver.isEmpty())
                {
                }else {
                }
                img_update.setVisibility(View.GONE);
                txt_for_update.setVisibility(View.GONE);
                swipeRefreshLayout_chat.setRefreshing(false);
            }
        });

        return v;
    }

    public void oldMassage()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String locale = Locale.getDefault().getLanguage();
                com.squareup.okhttp.OkHttpClient client = new OkHttpClient();
                String url = "http://213.183.51.209:8001/chat/" + Cl_ID() + "/?format=json";
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
                            myRecponse = "{\"oldmas\": " + myRecponse + "}";
                            list_of_massage_cerver.clear();
                            try
                            {
                                JSONObject object = new JSONObject(myRecponse);
                                JSONArray array = object.getJSONArray("oldmas");
                                for (int i = 0; i < array.length(); i++)
                                {
                                    JSONObject jsonObject1 = array.getJSONObject(i);
                                    String message = jsonObject1.getString("message");
                                    String timestamp = jsonObject1.getString("timestamp");
                                    String name_from = jsonObject1.getString("name_from");
                                    if (name_from.equals(""))
                                    {
                                        list_of_massage_cerver.add(new Massage("", "", "", timestamp, message));
                                    }
                                    else
                                    {
                                        list_of_massage_cerver.add(new Massage(name_from, message, timestamp, "", ""));
                                    }

                                }
                                System.out.println(list_of_massage_cerver);
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
        if (list_of_massage_cerver.isEmpty())
        {

        }else {
            img_update.setVisibility(View.GONE);
            txt_for_update.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    public void retMassage(String sms, String name_from, String timestamp)
    {
        if (name_from.equals(""))
        {
            list_of_massage_cerver.add(new Massage("", "", "", timestamp, sms));
            adapter.notifyDataSetChanged();
            soundOfMassage();
        }
        else
        {
            list_of_massage_cerver.add(new Massage(name_from, sms, timestamp, "", ""));
            adapter.notifyDataSetChanged();
            soundOfMassage();
        }

    }

    static class News_dataAdapter2 extends ArrayAdapter<Massage>
    {

        public News_dataAdapter2(Context context)
        {
            super(context, R.layout.list_item_chat, list_of_massage_cerver);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint({"WrongViewCast", "ResourceType"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            try
            {
                Massage Massage = getItem(position);

                if (convertView == null)
                {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_chat, null);
                }
                ((TextView) convertView.findViewById(R.id.name_server)).setText(Massage.name);
                ((TextView) convertView.findViewById(R.id.massage_server)).setText(Massage.masage_server);
                ((TextView) convertView.findViewById(R.id.data_server)).setText(Massage.data_time_server);
                ((TextView) convertView.findViewById(R.id.massage_client)).setText(Massage.massage_client);
                ((TextView) convertView.findViewById(R.id.data_client)).setText(Massage.data_time_client);

                if (Massage.massage_client.equals(""))
                {
                    ((TextView) convertView.findViewById(R.id.massage_client)).setVisibility(View.INVISIBLE);
                    ((TextView) convertView.findViewById(R.id.massage_server)).setVisibility(View.VISIBLE);
                }
                if (Massage.masage_server.equals(""))
                {
                    ((TextView) convertView.findViewById(R.id.massage_client)).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.massage_server)).setVisibility(View.INVISIBLE);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return convertView;

        }
    }

}