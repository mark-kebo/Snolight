package com.vorozhbicky.dmitry.snolight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_graph_ch:
                break;
            case R.id.action_settings:
                break;
            case R.id.action_about_us:
                Intent intent = new Intent(getBaseContext(), DataReader.class);// запуск потока приёма и отправки данных
                startActivity(intent);
        }
        return true;
    }
}
