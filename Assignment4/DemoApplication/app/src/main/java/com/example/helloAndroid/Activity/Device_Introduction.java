package com.example.helloAndroid.Activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.helloAndroid.R;

public class Device_Introduction extends AppCompatActivity {

    private Button readyBtn;
    private EditText deviceId;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_introduction);
        readyBtn = findViewById(R.id.btn_ready);
        deviceId = findViewById(R.id.device_name_edit);
        imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.images);
        readyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Device_Introduction.this, MainActivity.class);
            intent.putExtra("deviceId", deviceId.getText().toString());
            this.startActivity(intent);
        });
    }
}