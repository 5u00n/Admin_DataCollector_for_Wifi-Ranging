package com.projectopel.admindatacollector.permissions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public abstract class PermittedTask {
    private ActivityResultLauncher<String> launcher;
    private String permission;
    private AppCompatActivity activity;

    public PermittedTask(AppCompatActivity activity, String permission) {
        this.activity = activity;
        this.permission = permission;
        this.launcher =  activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            granted();
                        } else {
                            denied();
                        }
                    }
                }
        );
    }

    protected abstract void granted();

    protected void denied() {}

    private void showRequestPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permissions needed")
                .setMessage("App needs permissions to do that. You can allow or deny in next screen. Proceed?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launcher.launch(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        denied();
                    }
                })
                .show();
    }

    public void run() {
        if(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            granted();
        } else if(activity.shouldShowRequestPermissionRationale(permission)) {
            showRequestPermissionRationale();
        } else {
            launcher.launch(permission);
        }
    }
}