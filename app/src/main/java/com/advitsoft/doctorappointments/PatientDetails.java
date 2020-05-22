package com.advitsoft.doctorappointments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PatientDetails extends AppCompatActivity {

    Button submit;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        submit=findViewById(R.id.submit);
        back=findViewById(R.id.back);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent (PatientDetails.this,MainActivity.class);
                startActivity(i);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent (PatientDetails.this,AppointmentActivity.class);
                startActivity(i);

            }
        });
    }
}
