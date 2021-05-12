package betblogchat.pro;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link News_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class News_Fragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SwipeRefreshLayout swipeRefreshLayout;
    private String id232;
    private  ArrayAdapter<News_data> adapter;
    private ImageView deff_img_news;
    private TextView txt_no_coonection;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public News_Fragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment News.
     */
    // TODO: Rename and change types and number of parameters
    public static News_Fragment newInstance(String param1, String param2)
    {
        News_Fragment fragment = new News_Fragment();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        deff_img_news = v.findViewById(R.id.deff_img_news);
        txt_no_coonection = v.findViewById(R.id.txt_no_coonection);
        ListView list_view_of_ = v.findViewById(R.id.list_view_of_news);
        adapter = new MainActivity.News_dataAdapter(getActivity());
        adapter.notifyDataSetChanged();
        list_view_of_.setStackFromBottom(false);
        list_view_of_.setAdapter(adapter);

        if (!adapter.isEmpty())
        {
            deff_img_news.setVisibility(View.GONE);
            txt_no_coonection.setVisibility(View.GONE);
        }else {
            deff_img_news.setVisibility(View.VISIBLE);
            txt_no_coonection.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.blue_deff);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                Toast.makeText(getActivity(), R.string.upload, Toast.LENGTH_SHORT).show();
                MainActivity.RefreshList();
                adapter.notifyDataSetChanged();
                if (!adapter.isEmpty())
                {
                    deff_img_news.setVisibility(View.GONE);
                    txt_no_coonection.setVisibility(View.GONE);
                }else {
                    deff_img_news.setVisibility(View.VISIBLE);
                    txt_no_coonection.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

}