package com.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.webService.TaskAPI;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditTask extends AppCompatActivity {

    EditText editTitle;
    EditText editdesc;
    DatePicker editDatePicker;
    TimePicker editTimePicker;
    Spinner editCategory;
    String userToken;
    String username;
    ConstraintLayout editTitleCons;
    ConstraintLayout editDescCons;
    TaskAPI taskAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        getSupportActionBar().hide();

        init();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit editTask = new Retrofit.Builder()
                .baseUrl(TaskAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        taskAPI = editTask.create(TaskAPI.class);


        editDescCons.setBackgroundResource(R.drawable.border_red_task_error);
        editTitleCons.setBackgroundResource(R.drawable.border_red_task_error);

        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTitle.getText().toString()!="")
                {
                    editTitleCons.setBackgroundResource(R.drawable.border_task);
                }
                else
                {
                    editTitleCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTitle.getText().toString()=="")
                {
                    editTitleCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }
        });

        editdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editdesc.getText().toString()!="")
                {
                    editDescCons.setBackgroundResource(R.drawable.border_task);
                }
                else{
                    editDescCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editdesc.getText().toString()=="")
                {
                    editDescCons.setBackgroundResource(R.drawable.border_red_task_error);
                }
            }
        });
    }


    public void init()
    {
        editTitle = findViewById(R.id.editTitleEt);
        editdesc = findViewById(R.id.editDescriptionEt);
        editDatePicker = findViewById(R.id.editDateTimeDatePicker);
        editTimePicker = findViewById(R.id.edit_time_picker);
        editCategory = findViewById(R.id.edit_Spinner_category);
        SharedPreferences sharedPreferences = getSharedPreferences("authentication", MODE_PRIVATE);
        userToken = sharedPreferences.getString("token", "");
        username = sharedPreferences.getString("username","");
        editTitleCons = findViewById(R.id.editTitleCons);
        editDescCons = findViewById(R.id.editDescCons);
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

    public void EditTask(View view) {
        int year = editDatePicker.getYear();
        int month = editDatePicker.getMonth();
        int day = editDatePicker.getDayOfMonth();
        int hour = editTimePicker.getCurrentHour();
        int min = editTimePicker.getCurrentMinute();

        String categoryStr =  editCategory.getSelectedItem().toString();

        if(categoryStr.equals(""))
        {
            CustomeAlertDialog categortyEmpyt = new CustomeAlertDialog(this,"error","please fill the category field");
        }

        String titleStr = editTitle.getText().toString();
        String descStr = editdesc.getText().toString();
        if(titleStr.equals(""))
        {
            CustomeAlertDialog titleError = new CustomeAlertDialog(this,"Error","fill the title field");
            //Toast.makeText(this, "fill the title field", Toast.LENGTH_SHORT).show();
            editTitleCons.setBackgroundResource(R.drawable.border_red_task_error);
        }
        if(descStr.equals(""))
        {
            editDescCons.setBackgroundResource(R.drawable.border_red_task_error);
            //Toast.makeText(this, "fill the description field", Toast.LENGTH_SHORT).show();
            CustomeAlertDialog messageError = new CustomeAlertDialog(this,"Error","fill the description field");
        }

        if(checkDate(year,month,day,hour,min) && !titleStr.equals("") && !descStr.equals("")&& !categoryStr.equals(""))
        {
            Toast.makeText(this,"task saved",Toast.LENGTH_SHORT).show();
        }
    }
}