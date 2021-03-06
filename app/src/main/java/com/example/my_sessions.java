package com.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.ReservedSessionAdapter;
import com.example.adapter.eventAdapter;
import com.example.entity.Event;
import com.example.entity.Session;
import com.example.myapplication.R;
import com.example.webService.EventAPI;
import com.example.webService.TaskAPI;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class my_sessions extends AppCompatActivity {

    EventAPI eventAPI;
    EditText search;
    String userToken;
    ReservedSessionAdapter reservedSessionAdapter;
    ListView sessionsListView;
    private ShimmerFrameLayout mFrameLayout;
    NetworkInfo mWifi;
    ConstraintLayout noItemFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_sessions);
        init();
        mFrameLayout = findViewById(R.id.shimmerLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFrameLayout.startShimmer();
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            fillList();
        }
        else{
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void init()
    {
        noItemFound = findViewById(R.id.notFoundContainer);
        LinearLayout.LayoutParams noItemParams = (LinearLayout.LayoutParams) noItemFound.getLayoutParams();
        noItemParams.height = 1;
        noItemFound.setVisibility(View.INVISIBLE);

        search = findViewById(R.id.searchEventEditText);
        SharedPreferences sharedPreferences = getSharedPreferences("authentication", MODE_PRIVATE);
        userToken = sharedPreferences.getString("token", "");
        sessionsListView = findViewById(R.id.sessionsList);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reservedSessionAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void fillList()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit getSessions = new Retrofit.Builder()
                .baseUrl(EventAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        eventAPI = getSessions.create(EventAPI.class);



        Call<List<Session>> request = eventAPI.session_get("token "+userToken);

        request.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if(response.isSuccessful()==false)
                {
                    CustomErrorAlertDialog getTasksDayError = new CustomErrorAlertDialog(my_sessions.this,"Error","there is a problem with your internet connection");
                }
                else{
                    int responseCode = response.code();
                    //Toast.makeText(day_task_activity.this, Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
                    List<Session> listOfSessions = response.body();
                    reservedSessionAdapter = new ReservedSessionAdapter(my_sessions.this,listOfSessions);

                    sessionsListView.setAdapter(reservedSessionAdapter);

                    if(reservedSessionAdapter.getCount() == 0)
                    {
                        LinearLayout.LayoutParams noItemParams = (LinearLayout.LayoutParams) noItemFound.getLayoutParams();
                        noItemParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                        noItemFound.setVisibility(View.VISIBLE);
                    }

                    mFrameLayout.startShimmer();
                    mFrameLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                CustomErrorAlertDialog getTasksDayError = new CustomErrorAlertDialog(my_sessions.this,"Error","there is a problem with your internet connection");
            }
        });

    }
    @Override
    protected void onPause() {
        mFrameLayout.stopShimmer();
        super.onPause();
    }
}