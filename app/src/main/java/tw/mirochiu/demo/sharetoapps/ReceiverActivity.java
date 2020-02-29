package tw.mirochiu.demo.sharetoapps;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiverActivity extends AppCompatActivity {
    static String TAG = ReceiverActivity.class.getSimpleName();

    ImageView imageView;
    TextView textView;

    void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        /**
         * @ref https://developer.android.com/reference/android/support/v4/app/ShareCompat.IntentReader
         */
        ShareCompat.IntentReader reader =
                ShareCompat.IntentReader.from(ReceiverActivity.this);
        if (reader.isMultipleShare()) {
            showMessage("found multiple share, count:" + reader.getStreamCount());
        }
        if (reader.isShareIntent()) {
            String type = reader.getType();
            if (null == type) {
                type = getContentResolver().getType(reader.getStream(0));
                Log.e(TAG, "getType()=" + type);
                if (null == type) type = "null";
            }
            assert type != null;
            if ("text/html".equals(type)) {
                showMessage("got HTML:" + reader.getHtmlText());
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                /**
                 * @ref https://alvinalexander.com/android/how-show-html-string-in-android-textview-webview
                 */
                Spanned spanned = Html.fromHtml(reader.getHtmlText(), Html.FROM_HTML_MODE_LEGACY);
                textView.setText(spanned);
            } else if (type.startsWith("text/")) {
                showMessage(String.format("got text type:%s, content:%s", type, reader.getText()));
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(reader.getText());
            } else if (type.startsWith("image/")) {
                Uri uri = reader.getStream(0);
                showMessage(String.format("got image type:%s URI:%s", type, uri));
                if (null != uri) {
                    textView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(uri);
                }
            } else {
                showMessage("got not supported type:" + type);
            }
        }
    }

}
