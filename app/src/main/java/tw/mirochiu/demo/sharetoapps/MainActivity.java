package tw.mirochiu.demo.sharetoapps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static String TAG = MainActivity.class.getSimpleName();
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_fn1);
                    ShareCompat.IntentBuilder.from(MainActivity.this)
                            .setType("text/plain")
                            .setText("Hello, just do a test!")
                            .setChooserTitle("Share Text")
                            .startChooser();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_fn2);
                    Object imgSrc = imageView.getTag();
                    if (null != imgSrc && imgSrc instanceof Uri) {
                        Uri uriToImage = (Uri)imgSrc;
                        Intent intent = ShareCompat.IntentBuilder.from(MainActivity.this)
                                .setType(getContentResolver().getType(uriToImage))
                                .setStream(uriToImage)
                                .getIntent();
                        intent.setData(uriToImage);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "Share Image"));
                    }
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_fn3);
                    ShareCompat.IntentBuilder.from(MainActivity.this)
                            .setType("text/html")
                            .setHtmlText("<htm><head><title>TITLE</title></head><body><h1>NO BODY</h1></body></html>")
                            //.setSubject("Definitely read this")
                            .setChooserTitle("Share HTML")
                            .startChooser();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        imageView = findViewById(R.id.imageView);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                pickFromGallery();
                return true;
            }
        });
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * @ref https://androidclarified.com/pick-image-gallery-camera-android/
     */
    final static int GALLERY_REQUEST_CODE = 0x1000;
    private ImageView imageView;

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK); // launch gallery app
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // launch document app, more complicate UI
        intent.setType("image/*");
        //String[] mimeTypes = {"image/jpeg", "image/png"};
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE: {
                    Uri imgUri = null==data? null : data.getData();
                    if (null != imgUri) {
                        // imgUri has a prefix content://, is not same as file path
                        imageView.setImageURI(imgUri);
                        imageView.setTag(imgUri);
                    }
                    String msg = String.format("selected img URI:%s", imgUri);
                    Log.i(TAG, msg);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    break;
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
