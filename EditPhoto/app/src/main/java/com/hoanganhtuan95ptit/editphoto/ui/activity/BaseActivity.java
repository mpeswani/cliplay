package com.hoanganhtuan95ptit.editphoto.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Hoang Anh Tuan on 3/10/2017.
 */
abstract class BaseActivity extends AppCompatActivity {
    public boolean runnable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runnable = true;
    }

    @Override
    protected void onDestroy() {
        runnable = false;
        super.onDestroy();
    }

    /**
     * show notification
     *
     * @param message infor show
     */
    protected void showMessage(int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_LONG).show();
    }

    /**
     * ask permission android
     *
     * @param requestCode code
     * @param permissions list permission
     */
    protected void askPermissions(int requestCode, String... permissions) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
