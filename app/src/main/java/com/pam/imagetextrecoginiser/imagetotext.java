package com.pam.imagetextrecoginiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.util.List;

public class imagetotext extends AppCompatActivity {
    TextView textView;
    Button capture,detect,searchonline;
    ImageView imageView;
    String Text;
    Dialog mydialog;
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onStart() {
        super.onStart();
        final Dialog dialog;
        dialog = new Dialog(this);
        TextView close;
        dialog.setContentView(R.layout.popup);
        close = (TextView) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagetotext);
        mydialog = new Dialog(this);

        textView = (TextView) findViewById(R.id.textView);
        capture = (Button)findViewById(R.id.button);
        detect = (Button)findViewById(R.id.button2);
        imageView = (ImageView)findViewById(R.id.imageView);
        searchonline = (Button)findViewById(R.id.button3);



        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textView.setText("");
            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detecttext();
            }
        });
        searchonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(imagetotext.this,webview.class);
                i.putExtra("Text",Text);
                startActivity(i);
            }
        });
    }




    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


    private void detecttext() {

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displaytext(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(imagetotext.this, "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void displaytext(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocksList = firebaseVisionText.getBlocks();
        if(blocksList.size() == 0){
            Toast.makeText(this, "No text Detected", Toast.LENGTH_SHORT).show();
        }
        else {
            for(FirebaseVisionText.Block block: firebaseVisionText.getBlocks()){
                Text = block.getText();
                textView.setText(Text);
            }
        }
    }
    public void showpopup(View v){
        TextView close,txt_link;
        mydialog.setContentView(R.layout.popup_about);
        txt_link = (TextView) mydialog.findViewById(R.id.txt_link);
        txt_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebase.google.com/docs/ml-kit/recognize-text"));
                startActivity(browserIntent);
            }
        });
        close = (TextView) mydialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
            }
        });
        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();

    }
}
