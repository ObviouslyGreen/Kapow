package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImage;
import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImageGrayscaleFilter;
import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImageSketchFilter;
import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImageSmoothToonFilter;
import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImageToonFilter;

import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.bilateralFilter;
import static org.opencv.imgproc.Imgproc.pyrMeanShiftFiltering;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment
{
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static
    {
        System.loadLibrary("opencv_java");
        if (!OpenCVLoader.initDebug())
        {
            // Handle initialization error
        }
    }

    private String currFilter;
    private ProgressDialog pd;
    private Bitmap currentBitmap = null;
    private Uri currCamPath;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public FilterFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2)
    {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_filter, container, false);

        // Create button for file input, launches file explorer/gallery
        v.findViewById(R.id.file_input_button)
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

        // Create button for camera input
        v.findViewById(R.id.camera_input_button)
                .setOnClickListener(new View.OnClickListener()
                                    {
                                        public void onClick(View view)
                                        {
                                            if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                                            {
                                                Toast.makeText(MyApplication.getAppContext(), "No camera detected", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            if (!hasCameraApp(MediaStore.ACTION_IMAGE_CAPTURE))
                                            {
                                                Toast.makeText(MyApplication.getAppContext(), "No camera app found", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            String photoStorePath = getProductPhotoDirectory().getAbsolutePath();
                                            currCamPath = getPhotoFileUri(photoStorePath);
                                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, currCamPath);
                                            startActivityForResult(intent, TAKE_PICTURE);
                                        }
                                    }
                );

        // Create spinner to choose various filters
        Spinner spinner = (Spinner) v.findViewById(R.id.filter_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.filter_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Object item = parent.getItemAtPosition(pos);
                currFilter = item.toString();
            }

            public void onNothingSelected(AdapterView<?> parent)
            {
                currFilter = "";
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    //Button Action
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                FileInputTask filterTask = new FileInputTask();
                filterTask.execute(selectedImageUri);
            } else if (requestCode == TAKE_PICTURE)
            {
                FileInputTask filterTask = new FileInputTask();
                filterTask.execute(currCamPath);
            }
        }
    }

    public Uri chooseFilter(Bitmap bitmap)
    {
        // FUNCTION RUNS IN BACKGROUND THREAD
        // UI changes will crash the app
        Uri filteredUri = null;
        try
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

            // Use current filter from spinner and apply to input
            if (currFilter.equals("Mean Shift + Toon Filter"))
            {
                double sp, sr;
                int maxLevel;

                sp = Double.parseDouble(prefs.getString("ms_spatial_res", "5"));
                sr = Double.parseDouble(prefs.getString("ms_color_res", "10"));
                maxLevel = Integer.parseInt(prefs.getString("ms_max_level", "3"));
                if (sp > 0 && sr > 0 && maxLevel >= 0 && maxLevel <= 8)
                {
                    filteredUri = mean_shift(bitmap, sp, sr, maxLevel);
                } else
                {
                    Toast.makeText(MyApplication.getAppContext(), "Invalid arguments", Toast.LENGTH_SHORT).show();
                    return null;
                }
            } else if (currFilter.equals("Bilateral Filter"))
            {
                int d;
                double sigmaColor, sigmaSpace;

                d = Integer.parseInt(prefs.getString("bilateral_diameter", "7"));
                sigmaColor = Double.parseDouble(prefs.getString("bilateral_color", "150"));
                sigmaSpace = Double.parseDouble(prefs.getString("bilateral_space", "150"));
                if (d > 0 && sigmaColor > 0 && sigmaSpace > 0)
                {
                    filteredUri = bilateral(bitmap, d, sigmaColor, sigmaSpace);
                } else
                {
                    Toast.makeText(MyApplication.getAppContext(), "Invalid arguments", Toast.LENGTH_SHORT).show();
                    return null;
                }
            } else if (currFilter.equals("Grayscale Filter"))
            {
                filteredUri = grayscale(bitmap);
            } else if (currFilter.equals("Sketch Filter"))
            {
                filteredUri = sketchFilter(bitmap);
            } else if (currFilter.equals("Smooth and Toon Filter"))
            {
                filteredUri = smoothToonFilter(bitmap);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return filteredUri;
    }

    public Uri mean_shift(Bitmap bmp, double sp, double sr, int maxLevel) throws IOException
    {
        Mat imgMAT = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Mat out = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmp, imgMAT);

        //Make Mats 8-bit 3 Channel
        Mat temp = new Mat();
        Imgproc.cvtColor(imgMAT, temp, Imgproc.COLOR_BGRA2BGR);
        Mat dst = new Mat();
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2Luv);

        TermCriteria termcrit = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS, 5, 1);
        pyrMeanShiftFiltering(dst, out, sp, sr, maxLevel, termcrit);

        Bitmap bmpoutMS = matToBmp(out, bmp.getHeight(), bmp.getWidth());

        //Apply Toon Filter for black edges
        GPUImage mGPUImage = new GPUImage(MyApplication.getAppContext());
        mGPUImage.setFilter(new GPUImageToonFilter());
        mGPUImage.setImage(bmpoutMS);
        Bitmap bmpout = mGPUImage.getBitmapWithFilterApplied();

        return bmpToUri(bmpout);
    }

    public Uri bilateral(Bitmap bmp, int d, double sigmaColor, double sigmaSpace) throws IOException
    {
        Mat imgMAT = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Mat out = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmp, imgMAT);

        //Make Mats 8-bit 3 Channel
        Mat temp = new Mat();
        Imgproc.cvtColor(imgMAT, temp, Imgproc.COLOR_BGRA2BGR);
        Mat dst = new Mat();
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2Luv);

        bilateralFilter(dst, out, d, sigmaColor, sigmaSpace);

        Bitmap bmpout = matToBmp(out, bmp.getHeight(), bmp.getWidth());

        return bmpToUri(bmpout);
    }

    public Uri grayscale(Bitmap bmp)
    {
        GPUImage mGPUImage = new GPUImage(MyApplication.getAppContext());
        mGPUImage.setFilter(new GPUImageGrayscaleFilter());
        mGPUImage.setImage(bmp);
        Bitmap bmpout = mGPUImage.getBitmapWithFilterApplied();

        return bmpToUri(bmpout);
    }

    public Uri sketchFilter(Bitmap bmp)
    {
        GPUImage mGPUImage = new GPUImage(MyApplication.getAppContext());
        mGPUImage.setFilter(new GPUImageSketchFilter());
        mGPUImage.setImage(bmp);
        Bitmap bmpout = mGPUImage.getBitmapWithFilterApplied();

        return bmpToUri(bmpout);
    }

    public Uri smoothToonFilter(Bitmap bmp)
    {
        GPUImage mGPUImage = new GPUImage(MyApplication.getAppContext());
        mGPUImage.setFilter(new GPUImageSmoothToonFilter());
        mGPUImage.setImage(bmp);
        Bitmap bmpout = mGPUImage.getBitmapWithFilterApplied();

        return bmpToUri(bmpout);
    }

    public void displayPhoto(Uri uri)
    {
        try
        {
            ImageView img = (ImageView) getView().findViewById(R.id.display_image);
            img.setImageURI(uri);
        } catch (NullPointerException e)
        {
            Log.w("Null Image", "this should never happen");
        }
    }

    public Bitmap matToBmp(Mat in, int height, int width)
    {
        Mat bgrIn = new Mat(height, width, CvType.CV_8UC3);
        Imgproc.cvtColor(in, bgrIn, Imgproc.COLOR_Luv2BGR);
        Bitmap res = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        matToBitmap(bgrIn, res);
        return res;
    }

    public Uri bmpToUri(Bitmap in)
    {
        //Bitmap to Uri
        int counter = 0;
        OutputStream fOut = null;
        File file; // the File to save to
        String path = null;
        File sd = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File directory = new File(sd, "Kapow");
        try
        {
            directory.mkdirs();
        } catch (Exception e)
        {
        }

        // Saves on sd card, Pictures/Kapow
        file = new File(sd, "Kapow/" + "output.png");
        while (file.exists())
        {
            counter++;
            file = new File(sd, "Kapow/" + "output" + "(" + counter + ").png");
        }

        try
        {
            //Save Image to Path
            fOut = new FileOutputStream(file);
            in.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            path = MediaStore.Images.Media.insertImage(MyApplication.getAppContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(MyApplication.getAppContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            return null;
        }

        return Uri.parse(path);
    }

    // helper function to check if android device has camera
    private boolean hasCameraApp(String action)
    {
        final PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    private File getProductPhotoDirectory()
    {
        //get directory where file should be stored
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),
                "Kapow");
    }

    private Uri getPhotoFileUri(final String photoStorePath)
    {

        //timestamp used in file name
        final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.US).format(new Date());

        // file uri with timestamp
        final Uri fileUri = Uri.fromFile(new java.io.File(photoStorePath
                + java.io.File.separator + "IMG_" + timestamp + ".jpg"));

        return fileUri;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // AsyncTask to run the filtering algorithm to avoid blocking the UI thread
    private class FileInputTask extends AsyncTask<Uri, Void, Uri>
    {
        @Override
        protected Uri doInBackground(Uri... param)
        {
            Uri selectedImageUri = param[0];
            Uri filteredUri = null;
            Bitmap bitmap = null;
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), selectedImageUri);
                filteredUri = chooseFilter(bitmap);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return filteredUri;
        }

        @Override
        protected void onPreExecute()
        {
            if (pd != null)
            {
                pd.dismiss();
            }
            pd = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.setMessage("Processing...");
            pd.show();
        }

        @Override
        protected void onPostExecute(Uri filteredUri)
        {
            if (filteredUri != (null))
            {
                displayPhoto(filteredUri);
            }

            if (pd.isShowing() && pd != null)
            {
                pd.dismiss();
            }
        }
    }
}
