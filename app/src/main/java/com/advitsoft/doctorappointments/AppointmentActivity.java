package com.advitsoft.doctorappointments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AppointmentActivity extends AppCompatActivity {

    RelativeLayout availbledate;
    ImageView back;
    RelativeLayout dayslots,timings,evngslt,mngslot,txttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        availbledate=findViewById(R.id.availbledate);
        back=findViewById(R.id.back);
        dayslots=findViewById(R.id.dayslots);
        timings=findViewById(R.id.timings);
        evngslt=findViewById(R.id.evngslt);
        mngslot=findViewById(R.id.mngslot);
        txttime=findViewById(R.id.txttime);
        availbledate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evngslt.setVisibility(View.VISIBLE);
                mngslot.setVisibility(View.VISIBLE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent (AppointmentActivity.this,MainActivity.class);
                startActivity(i);

            }
        });
        timings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayslots.setVisibility(View.VISIBLE);

            }
        });
        txttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent (AppointmentActivity.this,PatientDetails.class);
                startActivity(i);


            }
        });
    }
}
