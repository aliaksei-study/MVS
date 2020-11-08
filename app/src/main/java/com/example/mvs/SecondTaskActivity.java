package com.example.mvs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class SecondTaskActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private Button loadImage;
    private Button calculate;
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_task_activity);
        loadImage = findViewById(R.id.load_image);
        calculate = findViewById(R.id.calculate);
        imageView = findViewById(R.id.portrait);
        setOnClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.second_task:
                Intent firstIntent = new Intent(this, SecondTaskActivity.class);
                startActivity(firstIntent);
                return true;
            case R.id.first_task:
                Intent secondIntent = new Intent(this, MainActivity.class);
                startActivity(secondIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageURI = data.getData();

                Picasso.get().load(selectedImageURI).resize(256, 300).into(imageView);
            }

        }
    }

    private void loadInternalImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private void setOnClickListeners() {
        loadImage.setOnClickListener((view) -> {
            loadInternalImage();
        });
        calculate.setOnClickListener((view) -> {
            extractRedFromEachImagePixel();
        });
    }

    private void extractRedFromEachImagePixel() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);
        bitmap = imageView.getDrawingCache();
        int pixel = bitmap.getPixel(1,1);
        System.out.println(Color.red(pixel));
    }
} 
