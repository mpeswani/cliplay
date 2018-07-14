package com.hoanganhtuan95ptit.editphoto.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;

import com.hoanganhtuan95ptit.editphoto.R;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    String path = getPathFromURI(selectedImageUri);
                    EditPhotoActivity.start(this, path);
                }
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
