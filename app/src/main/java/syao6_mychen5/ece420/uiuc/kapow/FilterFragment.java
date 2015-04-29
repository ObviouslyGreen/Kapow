package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImage;
import syao6_mychen5.ece420.uiuc.kapow.GPUImage.GPUImageToonFilter;

import static org.opencv.android.Utils.matToBitmap;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static
    {
        System.loadLibrary("opencv_java");
        if (!OpenCVLoader.initDebug())
        {
            // Handle initialization error
        }
    }

    private OnFragmentInteractionListener mListener;

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

    public FilterFragment()
    {
        // Required empty public constructor
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
        ((Button) v.findViewById(R.id.file_input_button))
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
        // Inflate the layout for this fragment
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
        public void onFragmentInteraction(Uri uri);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), selectedImageUri);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                Uri filteredUri = null;
                try
                {
                    filteredUri = filter(bitmap);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                displayPhoto(filteredUri);
            }
        }
    }

    public Uri filter(Bitmap bmp) throws IOException
    {
        Mat imgMAT = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Mat out = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmp, imgMAT);

        //Make Mats 8-bit 3 Channel
        Mat temp = new Mat();
        Imgproc.cvtColor(imgMAT,temp,Imgproc.COLOR_BGRA2BGR);
        Mat dst = new Mat();
        Imgproc.cvtColor(temp,dst,Imgproc.COLOR_BGR2Luv);


        //bilateralFilter(dst, out, 7, 180, 180);
        TermCriteria termcrit = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS, 5, 1);
        pyrMeanShiftFiltering(dst, out, 30, 30, 2, termcrit);

      /*  Scalar zero = Scalar.all(0);
        Scalar colorDiff = Scalar.all(1);
        Mat mask = new Mat( out.rows()+2, out.cols()+2, CvType.CV_8UC1, zero);
        for( int y = 0; y < out.rows(); y++ )
            {
            for( int x = 0; x < out.cols(); x++ )
            {
                byte buff[] = new byte[(int) (mask.total() * mask.channels())];
                mask.get(x+1, y+1, buff);
                if(0 == buff[0])
                {
                    Scalar newVal = new Scalar( random()%256, random()%256, random()%256 );
                    Point pt = new Point(x,y);
                    Rect test = new Rect();
                    floodFill( out, mask, pt, newVal, test, colorDiff, colorDiff, 4);
                }
            }
        }*/

        //Mat to Bitmap
        Mat newout = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Imgproc.cvtColor(out,newout,Imgproc.COLOR_Luv2BGR);
        Bitmap bmpoutMS = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        matToBitmap(newout, bmpoutMS);

        GPUImage mGPUImage = new GPUImage(MyApplication.getAppContext());
        mGPUImage.setFilter(new GPUImageToonFilter());
        mGPUImage.setImage(bmpoutMS);
        Bitmap bmpout = mGPUImage.getBitmapWithFilterApplied();

        //Bitmap to Uri
        int counter = 0;
        OutputStream fOut = null;
        File file; // the File to save to
        String path = null;
        File sd = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File directory = new File(sd, "Kapow");
        try {
            directory.mkdirs();
        } catch(Exception e) {}
        file = new File(sd, "Kapow/"+"output.png");
        while (file.exists()) {
            counter++;
            file = new File(sd, "Kapow/" + "output" + "(" + counter + ").png");
        }
        try{
            fOut = new FileOutputStream(file);
            bmpout.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            path = MediaStore.Images.Media.insertImage(MyApplication.getAppContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(MyApplication.getAppContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            return null;
        }
        return Uri.parse(path);
    }

    public void displayPhoto(Uri uri)
    {
        ImageView img = (ImageView) getView().findViewById(R.id.display_image);
        img.setImageURI(uri);
    }

}
