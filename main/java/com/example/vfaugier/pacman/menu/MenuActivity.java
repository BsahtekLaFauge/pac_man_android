package com.example.vfaugier.pacman.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.vfaugier.pacman.MainActivity;
import com.example.vfaugier.pacman.R;

public class MenuActivity extends AppCompatActivity {

    private final static String FILE_ID_KEY = "fileId";

    private GridView menuGridView;
    private int[] txtFilesIds;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menuGridView = (GridView) findViewById(R.id.menu_grid_view);
        context = this;

        int[] imageFilesIds = new int[] {
                R.drawable.map0,
                R.drawable.map1
        };
        txtFilesIds = new int[] {
                R.raw.map0,
                R.raw.map1
        };
        menuGridView.setAdapter(new ImageAdapter(this, imageFilesIds));

        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gameIntent = new Intent(context, MainActivity.class);
                gameIntent.putExtra(FILE_ID_KEY, txtFilesIds[position]);
                startActivity(gameIntent);
                finish();
            }
        });
    }

}
