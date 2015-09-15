package com.example.ambassador.aartestproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ambassador.ambassadorsdk.AmbassadorSDK;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AmbassadorSDK.runWithKeys("SDKToken 9de5757f801ca60916599fa3f3c92131b0e63c6a", "abfd1c89-4379-44e2-8361-ee7b87332e32");
        AmbassadorSDK.identify("jake@getambassador.com");

        Button btnRaf = (Button) findViewById(R.id.btnRAF2);
        final Context context = this;

        btnRaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, null, "305");
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
}
