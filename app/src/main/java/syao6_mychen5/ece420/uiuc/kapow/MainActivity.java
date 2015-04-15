package syao6_mychen5.ece420.uiuc.kapow;

/*import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import me.uits.aiphial.imaging.FastMatrixMS;
import me.uits.aiphial.imaging.Tools;*/

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.bilateralFilter;

public class MainActivity extends Activity
{
    // File input code: http://stackoverflow.com/a/2636538
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    static
    {
        System.loadLibrary("opencv_java");
        if (!OpenCVLoader.initDebug())
        {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.file_input_button))
                .setOnClickListener(new View.OnClickListener()
                                    {
                                        public void onClick(View view)
                                        {
                                            Intent intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(Intent.createChooser(intent,
                                                    "Select Picture"), SELECT_PICTURE);
                                        }
                                    }
                );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                Log.w("myApp", "pooooooop111111");
                Uri filteredUri = filter(bitmap);
      //          naiveSegmentation(selectedImagePath);
        //        displayPhoto(Uri.parse("./msout.png"));
                displayPhoto(filteredUri);
            }
        }
    }

    public Uri filter(Bitmap bmp){
        Mat imgMAT = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Mat out = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmp, imgMAT);

        //Make Mats 8-bit 3 Channel
        Mat dst = new Mat();
        Imgproc.cvtColor(imgMAT,dst,Imgproc.COLOR_BGRA2BGR);

        bilateralFilter(dst, out, 7, 180, 180);

        //Mat to Bitmap
        Bitmap bmpout = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        matToBitmap(out, bmpout);

        //Bitmap to Uri
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmpout.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(MyApplication.getAppContext().getContentResolver(), bmpout, "Title", null);
        return Uri.parse(path);
    }



    /*public void naiveSegmentation(String path)
    {
        BufferedImage srcimg = ImageIO.read(new File(path));
        // then create a Clusterer, FastMatrixMS is a simple Mean Shift Clusterer for images
        FastMatrixMS a = new FastMatrixMS(Tools.matrixFromImage(srcimg));

        // setup filter parametrs
        a.setColorRange(7f);
        a.setSquareRange((short)20);

        // process
        a.doClustering();

        // paint clusters on image
        BufferedImage img = Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight(), a.getClusters(), false);

        // write results to file
        ImageIO.write(img, "png", new File("./msout.png"));
    }
    */
    public void displayPhoto(Uri uri)
    {
        ImageView img = (ImageView)findViewById(R.id.image_input);
        img.setImageURI(uri);
    }

    public String getPath(Uri uri)
    {
        if( uri == null )
        {
            return null;
        }

        String res = uri.getPath();
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null )
        {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            res = cursor.getString(column_index);
            cursor.close();
        }

        return res;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

