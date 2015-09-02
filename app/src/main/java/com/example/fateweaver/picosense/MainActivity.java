package com.example.fateweaver.picosense;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
 //   Intent serviceIntent = new Intent(SenseService.class.getName());
    boolean pressure, temperature, humidity, light, accelerometer, magnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button startBtn = (Button) findViewById(R.id.start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Intent test = new Intent(MainActivity.this, SenseService.class);
            test.putExtra("pressure", pressure);
            test.putExtra("temperature", temperature);
            test.putExtra("humidity", humidity);
            test.putExtra("light", light);
            test.putExtra("accelerometer", accelerometer);
            startService(test);

            }
        });
        final Button stopBtn = (Button) findViewById(R.id.stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopService(new Intent(MainActivity.this, SenseService.class));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_pressure:
                if (checked) pressure = true;
                else pressure = false;
                break;
            case R.id.checkbox_temperature:
                if (checked) temperature = true;
                else temperature = false;
                break;
            case R.id.checkbox_accelerometer:
                if (checked) accelerometer = true;
                else accelerometer = false;
                break;
            case R.id.checkbox_humidity:
                if(checked) humidity = true;
                else humidity = false;
                break;
            case R.id.checkbox_light:
                if(checked) light = true;
                else light = false;
                break;
            case R.id.checkbox_magnetic:
                if(checked) magnetic = true;
                else magnetic = false;
                break;
        }
    }


}
