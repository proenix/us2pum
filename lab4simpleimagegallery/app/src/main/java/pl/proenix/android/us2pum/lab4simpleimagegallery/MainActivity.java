package pl.proenix.android.us2pum.lab4simpleimagegallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ACTION_OPEN_DOCUMENT_TREE = 2;

    private ImageView imageView;
    private ViewGroup horizontalScrollView;

    private DocumentFile[] documentFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        horizontalScrollView = findViewById(R.id.viewGroup);

        Button buttonOpen = findViewById(R.id.buttonOpen);
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_CODE_ACTION_OPEN_DOCUMENT_TREE);
            }
        });

        // Start from choosing dir to display.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_ACTION_OPEN_DOCUMENT_TREE);
    }

    /**
     * Load Images from selected directory to current Activity.
     * @param uri URI of selected directory.
     */
    private void loadImages(Uri uri) {
        // TODO: 19/04/2020 Class DocumentFile is not maintained from Android X <- find other solution.
        DocumentFile documentFile = DocumentFile.fromTreeUri(getApplicationContext(), uri);
        if (documentFile != null) {
            documentFiles = documentFile.listFiles();
        }
        ContentResolver cr = getContentResolver();
        InputStream ins;

        horizontalScrollView.removeAllViews();
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        boolean first = true;
        for(int i=0; i < documentFiles.length; i++) {
            if (documentFiles[i].isFile()) {
                try {
                    if (Objects.requireNonNull(documentFiles[i].getType()).startsWith("image/")) {
                        if (first) {
                            showLargeImage(i);
                            first = false;
                        }
                        final View singleFrame = getLayoutInflater().inflate(R.layout.frame_icon_caption, null);
                        singleFrame.setId(i);
                        ImageView icon = singleFrame.findViewById(R.id.icon);

                        ins = cr.openInputStream(documentFiles[i].getUri());
                        final int THUMB_SIZE = 128;
                        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                                BitmapFactory.decodeStream(ins),
                                THUMB_SIZE,
                                THUMB_SIZE);
                        icon.setImageBitmap(thumbImage);

                        singleFrame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showLargeImage(singleFrame.getId());
                            }
                        });

                        horizontalScrollView.addView(singleFrame);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error, loading mini image unsuccessful.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Show image in imageView.
     * @param id Index in documentFiles array.
     */
    private void showLargeImage(int id) {
        ContentResolver cr = getContentResolver();
        InputStream ins;
        try {
            ins = cr.openInputStream(documentFiles[id].getUri());
            Bitmap bitmap =  BitmapFactory.decodeStream(ins);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "Error, loading image unsuccessful.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Process returned activity results.
     * @param requestCode Returned activity code.
     * @param resultCode Returned activity result code.
     * @param data Data transferred from closed activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result from Android Storage Access Framework
        if (requestCode == REQUEST_CODE_ACTION_OPEN_DOCUMENT_TREE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    loadImages(uri);
                }
            }
        }
    }
}
