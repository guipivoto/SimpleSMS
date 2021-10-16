package com.pivoto.simplesms;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Telephony.Sms;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    public static final String TAG = "SimpleSMS";
    public static final String BLACKLIST_SHARED_PREFS_FILE = "blacklist_shared_pref_file";
    public static final String BLACKLIST_KEY = "blacklist";


    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Default APP: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.list_view);
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[]{Sms.ADDRESS, Sms.BODY},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            getSupportLoaderManager().initLoader(1, null, this);
        } else {
            String defaultSmsApp = Sms.getDefaultSmsPackage(this);
            Log.d(TAG, "Default APP: " + defaultSmsApp);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS},
                    0);
        }

        String defaultSmsApp = Sms.getDefaultSmsPackage(this);
        Log.d(TAG, "Default APP: " + defaultSmsApp);
        if (!"com.pivoto.simplesms".equals(defaultSmsApp)) {
            Intent intent = new Intent(Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }
    }


    private OnItemClickListener listViewItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                final long id) {

            Cursor cursorOnThatPosition = (Cursor) mAdapter.getItem(position);
            final int smsId = cursorOnThatPosition.getInt(
                    cursorOnThatPosition.getColumnIndex(BaseColumns._ID));

            Log.d(TAG, "Item Clicked: " + position + " smsId: " + smsId);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Delete SMS?")
                    .setMessage("Are you sure you want to delete this SMS?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            int result = getContentResolver().delete(
                                    Uri.parse("content://sms/" + smsId), null, null);

                            Log.d(MainActivity.TAG, "Messages deleted: " + result);

                            if (result > 0) {
                                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Default APP: onCreateLoader()");
        return new CursorLoader(this, Uri.parse("content://sms"),
                new String[]{BaseColumns._ID, Sms.ADDRESS, Sms.DATE, Sms.BODY}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Log.d(TAG, "Default APP: onLoadFinished()");

        if (data != null) {
            Log.d(TAG, "Default APP: onLoadFinished() count: " + ((Cursor) data).getCount());
            mAdapter.changeCursor((Cursor) data);
        } else {
            Log.w(TAG, "Cursor is null");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "Default APP: onLoaderReset()");
        mAdapter.changeCursor(null);
    }
}
