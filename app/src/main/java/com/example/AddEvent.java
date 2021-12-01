package com.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.adapter.SessionAdapter;
import com.example.adapter.taskAdapter;
import com.example.entity.Event;
import com.example.entity.Session;
import com.example.myapplication.R;
import com.example.webService.EventAPI;
import com.example.webService.TaskAPI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEvent  extends AppCompatActivity {

    Spinner category;
    EditText title;
    Spinner privacy;
    Spinner location;
    EditText description;
    Button addEvent;

    EventAPI eventAPI;

    String titleStr;
    String categoryStr;
    String locationStr;
    String privacyStr;
    String descriptionStr;
    String userToken;

    ConstraintLayout titleSpace;
    ConstraintLayout categorySpace;
    ConstraintLayout privacySpace;
    ConstraintLayout locationSpace;
    ConstraintLayout descriptionSpace;

    ListView sessionsList;

    List<Session> sessions;
    SessionAdapter sessionAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        getSupportActionBar().hide();
        init();
    }

    public void init()
    {
        title = findViewById(R.id.titleevent);
        category = findViewById(R.id.categoryevent);
        location = findViewById(R.id.locationevent);
        privacy = findViewById(R.id.privacyevent);
        description = findViewById(R.id.descript);
        addEvent = findViewById(R.id.addEventButton);
        sessionsList = findViewById(R.id.sessionsListView);

        titleSpace = findViewById(R.id.titleSpace);
        categorySpace = findViewById(R.id.categorySpace);
        privacySpace = findViewById(R.id.privacylayout);
        locationSpace = findViewById(R.id.locationSpace);
        descriptionSpace = findViewById(R.id.descriptionSpace);

        sessions = new ArrayList<Session>();


        SharedPreferences sharedPreferences = getSharedPreferences("authentication", MODE_PRIVATE);
        userToken = sharedPreferences.getString("token", "");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit createTask = new Retrofit.Builder()
                .baseUrl(TaskAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        eventAPI = createTask.create(EventAPI.class);

    }

    public void AddEvent(View view) {
        titleStr = title.getText().toString();
        categoryStr = category.getSelectedItem().toString();
        locationStr = location.getSelectedItem().toString();
        privacyStr = privacy.getSelectedItem().toString();
        descriptionStr = description.getText().toString();

        boolean valid = true;
        if(titleStr.equals(""))
        {
            CustomeAlertDialog titleEmpyt = new CustomeAlertDialog(this,"Error","please fill the title field");
            titleSpace.setBackgroundResource(R.drawable.border_event_error);
            valid = false;
        }
        if(categoryStr.equals(""))
        {
            CustomeAlertDialog categortyEmpyt = new CustomeAlertDialog(this,"Error","please fill the category field");
            categorySpace.setBackgroundResource(R.drawable.border_event_error);
            valid = false;
        }
        if(locationStr.equals(""))
        {
            CustomeAlertDialog locationEmpyt = new CustomeAlertDialog(this,"Error","please fill the location field");
            locationSpace.setBackgroundResource(R.drawable.border_event_error);
            valid = false;
        }
        if(privacyStr.equals(""))
        {
            CustomeAlertDialog privacyEmpyt = new CustomeAlertDialog(this,"Error","please fill the privacy field");
            privacySpace.setBackgroundResource(R.drawable.border_event_error);
            valid = false;
        }
        if(descriptionStr.equals(""))
        {
            CustomeAlertDialog privacyEmpyt = new CustomeAlertDialog(this,"Error","please fill the descrption field");
            descriptionSpace.setBackgroundResource(R.drawable.border_event_error);
            valid = false;
        }

        if(valid)
        {
            //String session = "20_2021-12-14_18:30";
            ArrayList<String> sessionsStr = new ArrayList<>();
            for(int i=0;i<sessions.size();i++)
            {
                String session = "";
                Session currentSession = sessions.get(i);
                String limit = currentSession.getLimit();
                String year = currentSession.getYear();
                String month = currentSession.getMonth();
                String day = currentSession.getDay();
                String hour = currentSession.getHour();
                String min = currentSession.getMin();

                String thisSession = limit+"_"+year+"-"+month+"-"+day+"_"+hour+":"+min;
                sessionsStr.add(thisSession);
            }
            boolean pv = false;
            if(privacyStr.equals("Public"))
            {
                pv = false;
            }
            else if(privacyStr.equals("Private"))
            {
                pv = true;
            }
            Event newEvent = new Event(userToken, titleStr,pv, categoryStr, descriptionStr, false, locationStr, sessionsStr);
            Call<Event> callBack = eventAPI.event_create("token "+userToken,newEvent);
            callBack.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    if(!response.isSuccessful())
                    {
                        CustomeAlertDialog errorConnecting = new CustomeAlertDialog(AddEvent.this,"error","there is a problem connecting to server");
                    }
                    else{
                        String code = Integer.toString(response.code());
                        Event addedEvent = response.body();
                        Toast.makeText(AddEvent.this, code, Toast.LENGTH_SHORT).show();
                        CustomeAlertDialog saved = new CustomeAlertDialog(AddEvent.this,"Successful","event saved");
                        saved.btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent event = new Intent(AddEvent.this, event_activity.class);
                                startActivity(event);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
                    CustomeAlertDialog errorConnecting = new CustomeAlertDialog(AddEvent.this,"Error","there is a problem connecting to server");
                }
            });
        }
    }

    public void addSessionBtn(View view) {

        EditText limit = findViewById(R.id.added_session_limit);
        Spinner year = findViewById(R.id.added_date_year);
        Spinner month = findViewById(R.id.added_date_month);
        Spinner day = findViewById(R.id.added_date_year);
        Spinner hour = findViewById(R.id.added_hour);
        Spinner min = findViewById(R.id.added_min);

        String limitStr = limit.getText().toString();
        String yearStr = year.getSelectedItem().toString();
        String monthStr = month.getSelectedItem().toString();
        String dayStr = day.getSelectedItem().toString();
        String hourStr = hour.getSelectedItem().toString();
        String minStr = min.getSelectedItem().toString();

        int yearNum = Integer.parseInt(yearStr);
        int monthNum = Integer.parseInt(monthStr);
        int dayNum = Integer.parseInt(dayStr);
        int hourNum = Integer.parseInt(hourStr);
        int minNum = Integer.parseInt(minStr);

        if(checkDate(yearNum,monthNum,dayNum,hourNum,minNum))
        {
            Session newSession = new Session(yearStr,monthStr,dayStr,hourStr,minStr,limitStr);
            sessions.add(newSession);

            sessionAdapter = new SessionAdapter(AddEvent.this,sessions);
            sessionsList.setAdapter(sessionAdapter);
        }
        else{
            CustomeAlertDialog errorDate = new CustomeAlertDialog(AddEvent.this,"Error","you can not select a date in past");
        }
    }

    public boolean checkDate(int year, int month , int day,int hour,int min)
    {

        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH);
        int currentYear = c.get(Calendar.YEAR);


        Time now = new Time();
        now.setToNow();

        int currentHour = now.hour;
        int currentMinuate = now.minute;

        String currentDateTime = Integer.toString(currentYear)+Integer.toString(currentMonth)+Integer.toString(currentDay)+"_"+Integer.toString(currentHour)+":"+Integer.toString(currentMinuate);
        Toast.makeText(this, currentDateTime, Toast.LENGTH_SHORT).show();
        if(currentYear<year)
        {
            return true;
        }
        else if(currentYear==year && currentMonth<month)
        {
            return true;
        }
        else if(currentYear == year && currentMonth == month && currentDay < day)
        {
            return true;
        }
        else if (currentYear == year && currentMonth == month && currentDay==day && currentHour<hour)
        {
            return true;
        }
        else if(currentYear == year && currentMonth == month && currentDay==day && currentHour==hour && currentMinuate<min)
        {
            return true;
        }
        CustomeAlertDialog dateAlert = new CustomeAlertDialog(this,"error","you can set a task for past");
        //Toast.makeText(this, "you can set a task for past", Toast.LENGTH_SHORT).show();
        return false;

    }
}