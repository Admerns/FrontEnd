package com.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.BroadCast.NotificationServ;
import com.example.DataBase.tasksDB;
import com.example.entity.Task;
import com.example.myapplication.R;
import com.example.webService.TaskAPI;
import com.example.webService.TaskSession;


import java.io.IOException;
import java.util.Calendar;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTask extends AppCompatActivity {

    DatePicker datePicker;
    TimePicker timePicker;
    EditText title;
    Spinner category;
    EditText desc;
    String userToken;
    String username;
    TaskAPI taskAPI;
    ConstraintLayout titleCons;
    ConstraintLayout descCons;
    NetworkInfo mWifi;

    @Override
    protected void onStop() {
        super.onStop();
        startService( new Intent( this, NotificationServ. class )) ;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        startService( new Intent( this, NotificationServ.class )) ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService( new Intent( this, NotificationServ.class ));
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().hide();

        init();

        String date = getIntent().getStringExtra("date");
        String [] dateInfo = date.split("-");

        int year = Integer.parseInt(dateInfo[0]);
        int month = Integer.parseInt(dateInfo[1]);
        int day = Integer.parseInt(dateInfo[2]);

        datePicker.init(year,month,day,null);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Authorization", "token "+userToken)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });


        Retrofit createTask = new Retrofit.Builder()
                .baseUrl(TaskAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        taskAPI = createTask.create(TaskAPI.class);


        descCons.setBackgroundResource(R.drawable.border_red_task_error);
        titleCons.setBackgroundResource(R.drawable.border_red_task_error);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title.getText().toString()!="")
                {
                    titleCons.setBackgroundResource(R.drawable.border_shadow);
                }
                else
                {
                    titleCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(title.getText().toString()=="")
                {
                    titleCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }
        });

        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(desc.getText().toString()!="")
                {
                    descCons.setBackgroundResource(R.drawable.border_shadow);
                }
                else{
                    descCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(desc.getText().toString()=="")
                {
                    descCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }
        });
    }

    public void init()
    {

        datePicker = findViewById(R.id.DateTimeDatePicker);
        timePicker = findViewById(R.id.time_picker);
        title = findViewById(R.id.titleEt);
        category = findViewById(R.id.Spinner_category);
        desc = findViewById(R.id.descriptionEt);
        descCons = findViewById(R.id.descCons);
        titleCons = findViewById(R.id.titleCons);

        desc.setScroller(new Scroller(AddTask.this));
        desc.setMaxLines(1);
        desc.setVerticalScrollBarEnabled(true);
        desc.setMovementMethod(new ScrollingMovementMethod());


        SharedPreferences sharedPreferences = getSharedPreferences("authentication", MODE_PRIVATE);
        userToken = sharedPreferences.getString("token", "");
        username = sharedPreferences.getString("username","");
    }

    public void addTask(View view) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();

        String categoryStr =  category.getSelectedItem().toString();

        if(categoryStr.equals(""))
        {
            CustomErrorAlertDialog categortyEmpyt = new CustomErrorAlertDialog(this,"Error","please fill the category field");
        }

        String titleStr = title.getText().toString();
        String descStr = desc.getText().toString();
        if(titleStr.equals(""))
        {
            CustomErrorAlertDialog titleError = new CustomErrorAlertDialog(this,"Error","fill the title field");
            //Toast.makeText(this, "fill the title field", Toast.LENGTH_SHORT).show();
            titleCons.setBackgroundResource(R.drawable.border_red_task_error);
        }
        if(descStr.equals(""))
        {
            descCons.setBackgroundResource(R.drawable.border_red_task_error);
            //Toast.makeText(this, "fill the description field", Toast.LENGTH_SHORT).show();
            CustomErrorAlertDialog messageError = new CustomErrorAlertDialog(this,"Error","fill the description field");
        }

        if(checkDate(year,month,day,hour,min) && !titleStr.equals("") && !descStr.equals("")&& !categoryStr.equals(""))
        {
            Toast.makeText(this,"task saved",Toast.LENGTH_SHORT).show();
            String hours = Integer.toString(hour);
            if(hours.length()==1)
            {
                hours = "0"+hours;
            }
            String mins = Integer.toString(min);
            if(mins.length()==1)
            {
                mins = "0"+mins;
            }
            String datetime = Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day)+"_"+hours+":"+mins;
            String status = "pending";
            //Toast.makeText(this, datetime, Toast.LENGTH_SHORT).show();
            Task newTask = new Task(titleStr,descStr, datetime, categoryStr, "time for "+categoryStr,userToken,status);

            ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if(mWifi.isConnected())
            {
                Call<TaskSession> callBack =taskAPI.createTask("token "+userToken,newTask);
                callBack.enqueue(new Callback<TaskSession>() {
                    @Override
                    public void onResponse(Call<TaskSession> call, Response<TaskSession> response) {
                        if(!response.isSuccessful())
                        {
                            CustomeAlertDialog errorConnecting = new CustomeAlertDialog(AddTask.this,"error","there is a problem connecting to server");
                        }
                        else{
                            String code = Integer.toString(response.code());
                            TaskSession savedTaskSession = response.body();
                            Task savedTask = savedTaskSession.getTask();
                            String task_token = savedTask.getTaskToken();
                            String title = savedTask.getTitle();
                            String dateTime = savedTask.getDateTime();
                            String [] infos = dateTime.split("_");
                            String date = infos[0];
                            String time = infos[1];

//                            CustomeAlertDialog saved = new CustomeAlertDialog(AddTask.this,"Successful","task saved");
//                            saved.btnOk.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    finish();
//                                }
//                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<TaskSession> call, Throwable t) {
                        CustomeAlertDialog errorConnecting = new CustomeAlertDialog(AddTask.this,"error","there is a problem connecting to server");

                    }
                });
            }

            //for offline part....
            tasksDB tasksdb = new tasksDB(AddTask.this);
            String [] dateTimeInfo = datetime.split("_");
            String date = dateTimeInfo[0];
            String [] dateArray = date.split("-");
            if(dateArray[1].length() == 1){
                dateArray[1] = "0"+dateArray[1];
            }
            if(dateArray[2].length() == 1){
                dateArray[2] = "0"+dateArray[2];
            }
            date = dateArray[0]+"-"+dateArray[1]+"-"+dateArray[2];
            String time = dateTimeInfo[1];
            long res = tasksdb.insert(titleStr,date,time,descStr,status,categoryStr);
            Log.i("DATE",date);
            Log.i("Time",time);
            if(res >=0)
            {
                //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
                CustomSuccessAlertDialog saved = new CustomSuccessAlertDialog(AddTask.this,"Successful","task saved");
                saved.btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
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
        // Toast.makeText(this, currentDateTime, Toast.LENGTH_SHORT).show();
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
        CustomErrorAlertDialog dateAlert = new CustomErrorAlertDialog(this,"Error","you can't set a task for past");
        //Toast.makeText(this, "you can set a task for past", Toast.LENGTH_SHORT).show();
        return false;

    }
}