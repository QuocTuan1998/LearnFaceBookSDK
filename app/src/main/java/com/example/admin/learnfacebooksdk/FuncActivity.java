package com.example.admin.learnfacebooksdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FuncActivity extends AppCompatActivity {

    EditText edTitle, edDes, edURL;
    Button btnShareLink, btnShareImage, btnPickVideo, btnShareVideo;
    ImageView imgPicture;
    VideoView videoView;

    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;

    public static int SELECT_IMAGE = 1;
    public static int PICK_VIDEO = 2;
    Bitmap bitmap;
    Uri selectVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func);

        addControls();

        shareDialog = new ShareDialog(FuncActivity.this);
        btnShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shareDialog.canShow(ShareLinkContent.class)){
                    shareLinkContent = new ShareLinkContent.Builder()
                            .setContentTitle(edTitle.getText().toString())
                            .setContentDescription(edDes.getText().toString())
                            .setContentUrl(Uri.parse(edURL.getText().toString())).build();
                }
                shareDialog.show(shareLinkContent);
            }
        });

        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });
        btnShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        });
        btnPickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent, PICK_VIDEO);
            }
        });
        btnShareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareVideo shareVideo = null;
                shareVideo = new ShareVideo.Builder()
                        .setLocalUrl(selectVideo)
                        .build();
                ShareVideoContent content = new ShareVideoContent.Builder()
                        .setVideo(shareVideo)
                        .build();
                shareDialog.show(content);
                videoView.stopPlayback();
            }
        });
    }

    private void addControls() {
        edDes = findViewById(R.id.edDes);
        edTitle = findViewById(R.id.edTitle);
        edURL = findViewById(R.id.edURL);

        btnPickVideo = findViewById(R.id.btnPick);
        btnShareVideo = findViewById(R.id.btnShareVideo);
        btnShareLink = findViewById(R.id.btnShareLink);
        btnShareImage = findViewById(R.id.btnShareImage);

        imgPicture = findViewById(R.id.imgPicture);

        videoView = findViewById(R.id.videoView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK){
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                imgPicture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK){
            selectVideo = data.getData();
            videoView.setVideoURI(selectVideo);
            videoView.start();
        }
    }
}
