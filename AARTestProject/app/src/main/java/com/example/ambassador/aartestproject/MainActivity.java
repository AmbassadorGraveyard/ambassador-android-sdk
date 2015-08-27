package com.example.ambassador.aartestproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ambassador.ambassadorsdk.Ambassador;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ambassador.runWithKey("UniversalToken bdb49d2b9ae24b7b6bc5da122370f3517f98336f");
        Ambassador.identify("jake@getambassador.com");

        Button btnRaf = (Button) findViewById(R.id.btnRAF2);
        final Context context = this;

        btnRaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ambassador.presentRAF(context, null, "305");
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
