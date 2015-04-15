package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import org.opencv.android.OpenCVLoader;

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
                displayPhoto(selectedImageUri);
            }
        }
    }

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
