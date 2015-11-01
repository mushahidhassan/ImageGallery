package com.declarevariable.imagegallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private String TAG ="MainActivity";
    private int itemPosition = -1;//for storing item relative to gridView's position
    //ArrayList<String> galleryList;
    GridAdapter gridAdapter;//for invalidating data set
    private Boolean check, replace = false;
    private View _view;//refrence to a view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

            //ifelse
            /*ImageView iv_test = (ImageView) findViewById(R.id.iv_image_test);
            int Test = galleryList.size();
            String item  = galleryList.get(1);
            Bitmap bitmap = Common.BitmapConversion.stringToImage(item);
            iv_test.setImageBitmap(bitmap);*/
        try {
            setUpGridView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUpGridView(){
        //setting up gridView and items

        GridView mGridView = (GridView) findViewById(R.id.galleryGrid);
        gridAdapter = new GridAdapter(Common.Arr.imagesArr, this/*, galleryList*/);
        try {
            mGridView.setAdapter(gridAdapter);
        } catch (Exception e) {
            Log.e(TAG + " setUpGridView", e.getLocalizedMessage());
        }
        registerForContextMenu(mGridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _view = view;
                itemPosition = position;

                ArrayList<String> galleryList = new ArrayList<String>();
                galleryList = Common.SharedPref.get(getApplicationContext());

                if (position < galleryList.size()) {
                    check = true;
                    view.showContextMenu();
                }else if (position == galleryList.size()) {
                    check = false;
                    view.showContextMenu();
                }
                else if (position > galleryList.size()){
                    Toast.makeText(getApplicationContext(), "You need to select the item which has Add symbol on it", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //TextView text = (TextView) v.findViewById(R.id.btitle);
        //CharSequence itemTitle = text.getText();
        //menu.setHeaderTitle(itemTitle);
        MenuInflater inflater = getMenuInflater();
        if (check == true) {
            inflater.inflate(R.menu.context_menu, menu);
            check = false;
        }else {
            inflater.inflate(R.menu.context_menu_first, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.replace:
                //check = true;
                //_view.showContextMenu();
                addItemOptoins();
                return true;
            case R.id.camera:
                captureImage();
                return true;
            case R.id.gallery:
                openGallery();
                return true;
            case R.id.delete:
                deleteImageFromGalleryList();
            case R.id.make_main_image:
                makeItemMainInGalleryList();
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onContextItemSelected(item);
    }

    private void captureImage(){
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Common.Keys.CAMERA_REQUEST);
        }catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void openGallery(){
        try {
            Intent intent = new Intent();
            intent.setType("image/*");// images only
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.Keys.PICK_IMAGE_REQUEST);
        }catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Common.Keys.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Log.d(TAG, String.valueOf(bitmap));
                    String galleryItemStr = Common.BitmapConversion.imageToString(bitmap);
                    if (replace==false) {
                        addImageToGalleryList(galleryItemStr);
                    }else{
                        replaceImageInGalleryList(galleryItemStr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == Common.Keys.CAMERA_REQUEST && resultCode == RESULT_OK) {
                try{
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    String galleryItemStr = Common.BitmapConversion.imageToString(bmp);
                    if (replace==false) {
                        addImageToGalleryList(galleryItemStr);
                    }else{
                        replaceImageInGalleryList(galleryItemStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error While getting back to the MainActivity", Toast.LENGTH_LONG).show();
        }
    }

    private void addImageToGalleryList(String item){
        //1. get shared pref and check for total tiems
        //2. then add the item at proper location
        //3. edit shared_pref
        ArrayList<String> gallery = Common.SharedPref.get(this);
        if(!gallery.isEmpty()){
            gallery.add(itemPosition, item);
        }else {
            gallery.add(item);
        }
        if(gallery.size()>0){
            Common.SharedPref.update(this, gallery.toString());
        }
        gridAdapter.setGalleryList(gallery);
    }

    private void deleteImageFromGalleryList(){
        //1. get shared pref and check for total tiems
        //2. then add the item at proper location
        //3. edit shared_pref
        ArrayList<String> gallery = Common.SharedPref.get(this);
        //ArrayList<String> newGallery = new ArrayList<String>();
        if(!gallery.isEmpty()){
            //String temp_main = gallery.get(0);//backup
            int test = gallery.size();
            gallery.remove(itemPosition);
            //gallery.set(0, temp_main);//because arrayList was losing its places

            /*for(String temp: gallery){
                if(temp == gallery.get(itemPosition)){

                }
                else{
                    newGallery.add(temp);
                }
            }*/
            test = gallery.size();
            Common.SharedPref.update(this, gallery.toString());
        }
        gridAdapter.setGalleryList(gallery);
    }

    private void makeItemMainInGalleryList() {
        //1. get shared pref and check for total tiems
        //2. then add the item at proper location
        //3. edit shared_pref
        if (itemPosition > 0) {
            ArrayList<String> gallery = Common.SharedPref.get(this);
            if (!gallery.isEmpty()) {
                String temp = gallery.get(itemPosition);
                gallery.set(itemPosition, gallery.get(0));
                gallery.set(0, temp);
                Common.SharedPref.update(this, gallery.toString());
                gridAdapter.setGalleryList(gallery);
            }else {
                Toast.makeText(this, "Already Main Item",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void replaceImageInGalleryList(String item){
        //1. get shared pref and check for total tiems
        //2. then add the item at proper location
        //3. edit shared_pref
        ArrayList<String> gallery = Common.SharedPref.get(this);
        if(!gallery.isEmpty()) {
            gallery.set(itemPosition, item);
            Common.SharedPref.update(this, gallery.toString());
        }
        gridAdapter.setGalleryList(gallery);
        replace = false;
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



    private void addItemOptoins() {
        DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog.cancel();
                if (which == 0) {
                    replace = true;
                     captureImage();

                } else if (which == 1) {
                    replace = true;
                     openGallery();
                }
            }
        };
        String[] items = { "Camera", "Gallery" };
        Common.CustomDialog.listDialog(MainActivity.this, items, posListener);
    }

}
