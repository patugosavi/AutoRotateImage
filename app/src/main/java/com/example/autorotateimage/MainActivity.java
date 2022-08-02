package com.example.autorotateimage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.autorotateimage.baseclass.BaseActivity;
import com.example.autorotateimage.di.component.ApplicationComponent;
import com.example.autorotateimage.network_service.ApiInterface;
import com.example.autorotateimage.uploadimage.ImageModel;
import com.example.autorotateimage.utils.BaseApp;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements UploadImageView{

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.iv_uploadimage)
    ImageView iv_uploadimage;
    @BindView(R.id.iv_image)
    ImageView iv_image;

    //upload image
    // =========== Upload image ================
    private static final int GALLERY_IMAGE = 104;
    private static final int CAMERA_IMAGE = 105;
    private static final int MY_CAMERA_CODE = 102;

    Bitmap bitmap, scaledBitmap;
    String imgFileLocation,url,picturePath;
    File imageFile;
    private Uri imageUri;

    AlertDialog addimagealertDialog;

    @Inject
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Inject
    ApiInterface apiInterface;

    Context mContext;

    private UploadImagePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mContext = getApplicationContext();

        presenter = new UploadImagePresenter(this, apiInterface);
        editor = sharedPreferences.edit();

        iv_uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddImage();
                url="";
            }
        });

        setChildActivity(true);
    }

    @Override
    protected void injectDependencies(BaseApp baseApp, ApplicationComponent component) {
        component.inject(this);
    }

    private void DialogAddImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_add_image, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        addimagealertDialog = builder.create();
        addimagealertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addimagealertDialog.show();

        ImageView iv_close=dialogView.findViewById(R.id.iv_close);
        LinearLayout ll_camera=dialogView.findViewById(R.id.ll_camera);
        LinearLayout ll_gallery=dialogView.findViewById(R.id.ll_gallery);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addimagealertDialog.dismiss();
            }
        });

        ll_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requestPermission(CAMERA_IMAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addimagealertDialog.dismiss();
            }
        });

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requestPermission(GALLERY_IMAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addimagealertDialog.dismiss();
            }
        });
    }

    private void requestPermission(final int requestCode) throws IOException {
        if(requestCode==GALLERY_IMAGE){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},requestCode);

            } else {
                selectFromGallery();
            }

        }else if (requestCode==CAMERA_IMAGE){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode);

            } else {
                captureImageUsingCamera();
            }
        }
    }

    private void captureImageUsingCamera() throws IOException {
        Intent takepictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivity(takepictureIntent);
        File image;
        try{
            image =createImageFile();
            Log.d(TAG, "captureImageUsingCamera: "+image);
            if(image !=null){
//                imageUri= FileProvider.getUriForFile(this,"com.example.visitapp.fileprovider",image);
                imageUri= FileProvider.getUriForFile(this,mContext.getPackageName() + ".provider",image);
                takepictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                takepictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if(takepictureIntent.resolveActivity(this.getPackageManager())!=null){
                    startActivityForResult(takepictureIntent,CAMERA_IMAGE);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void selectFromGallery() {
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
        gallaryIntent.setType("image/*");
        startActivityForResult(gallaryIntent, GALLERY_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_IMAGE){
            if(resultCode==RESULT_OK){
                Uri uri =data.getData();
                final InputStream imageStream;
              /*  try {
                    imageStream = getContentResolver().openInputStream(uri);
//                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    //SET HEIGHT AND WIDTH  FOR BITMAP

                    scaledBitmap =getScaledBitmap(bitmap,350,300);

                    new MainActivity.AsyncTaskUploadImage().execute();
                } catch (IOException ex){
                    ex.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }*/

                // Get and resize profile image
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = this.getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgFileLocation = cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options bmOptions =new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds=true;
//                BitmapFactory.decodeFile(imgFileLocation,bmOptions);

//                Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath,bmOptions);
                 BitmapFactory.decodeFile(imgFileLocation,bmOptions);


                rotateImage(setReducedImageSize());

//                ExifInterface exif = null;
//                try {
//                    File pictureFile = new File(picturePath);
//                    exif = new ExifInterface(pictureFile.getAbsolutePath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                int orientation = ExifInterface.ORIENTATION_NORMAL;
//
//                if (exif != null)
//                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                switch (orientation) {
//                    case ExifInterface.ORIENTATION_ROTATE_90:
//                        rotateBitmap(loadedBitmap, 90);
//                        break;
//                    case ExifInterface.ORIENTATION_ROTATE_180:
//                         rotateBitmap(loadedBitmap, 180);
//                        break;
//
//                    case ExifInterface.ORIENTATION_ROTATE_270:
//                        rotateBitmap(loadedBitmap, 270);
//                        break;
//                }


            }

        }
        else if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK) {
            rotateImage(setReducedImageSize());
        }
    }


    public void rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        scaledBitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        new MainActivity.AsyncTaskUploadImage().execute();
    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface=null;
        try{
            exifInterface=new ExifInterface(imgFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation =exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix=new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
//                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
//                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
//                matrix.postRotate(270);
                break;
            default:
//                matrix.setRotate(0);
//                ExifInterface.ORIENTATION_UNDEFINED;
//                break;

        }
//        matrix.postRotate(0);
        scaledBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        new MainActivity.AsyncTaskUploadImage().execute();
    }

    private Bitmap setReducedImageSize() {
        int targetImageViewWidth=300;
        int targetImageHeight=300;

        BitmapFactory.Options bmOptions =new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imgFileLocation,bmOptions);

        int CameraImageWidth=bmOptions.outWidth;
        int CameraImageHeight=bmOptions.outHeight;

        int scaleFactor=Math.min(CameraImageWidth/targetImageViewWidth,CameraImageHeight/targetImageHeight);
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inJustDecodeBounds=false;

        return BitmapFactory.decodeFile(imgFileLocation,bmOptions);
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

        int nWidth = bWidth;
        int nHeight = bHeight;

        if(nWidth > width) {
            int ratio = bWidth / width;
            if(ratio > 0)
            {
                nWidth = width;
                nHeight = bHeight / ratio;
            }
        }

        if(nHeight > height) {
            int ratio = bHeight / height;
            if(ratio > 0) {
                nHeight = height;
                nWidth = bWidth / ratio;
            }
        }


        return Bitmap.createScaledBitmap(bitmap, nWidth, nHeight, true);
    }

    private void convertImageBitmap(Bitmap selectedImage) throws IOException {
        /* */
        File imageFile = createImageFile();
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
//            selectedImage.compress(Bitmap.CompressFormat.WEBP, 60, os);
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 60, os);
            os.flush();
            os.close();

        } catch (Exception e) {

        }
        Log.d(TAG, "convertImageBitmap: "+imageFile);
        uploadProofImage(imageFile);
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);

//        return File.createTempFile(
//                imageFileName,   //prefix
//                ".webp",          //suffixkkkkkmn
//                storageDir       //di rectory
//        );
        //return f;
        File image=File.createTempFile(timeStamp,".jpg",storageDir);
        imgFileLocation=image.getAbsolutePath();
        return  image;
    }




    // Upload Sign image online on server function.
    class  AsyncTaskUploadImage extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog at image upload time.
            showProgressBar();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Dismiss the progress dialog after done uploading.
            hideProgressBar();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                convertImageBitmap(scaledBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void uploadProofImage(File imageFile) {
        presenter.uploadimage(imageFile);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void onFailure(String s) {

    }

    @Override
    public void uploadimage(ImageModel response) {
        if(response!=null){
            url = response.getUrl();

            Picasso.get()
                    .load(Uri.parse(url))
                    .into(iv_image);


            Log.d(TAG, "onProofImage: "+url);
            Toast.makeText(mContext, "Image Uploaded...", Toast.LENGTH_SHORT).show();
//            adapterClick();
        }
    }

}