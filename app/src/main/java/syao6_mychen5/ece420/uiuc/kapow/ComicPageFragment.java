package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thuytrinh.android.collageviews.MultiTouchListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComicPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicPageFragment extends Fragment implements View.OnClickListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int SELECT_PICTURE = 1;
    private static final int SAVE_PICTURE = 2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int counter = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ComicPageFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComicPageFragment newInstance(String param1, String param2)
    {
        ComicPageFragment fragment = new ComicPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bitmap loadBitmapFromView(Context context, ViewGroup v)
    {

       /* Toast.makeText(context,
                v.getMeasuredHeight() + "::::::::::::" + v.getMeasuredWidth(),
                Toast.LENGTH_LONG).show();*/
        if (v.getMeasuredHeight() > 0)
        {

            v.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.ARGB_8888);

            v.draw(new Canvas(b));

            return b;

        }

        return null;

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
        View v = inflater.inflate(R.layout.fragment_page, container, false);
        // Creat button for file input
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
        v.findViewById(R.id.save_button)
                .setOnClickListener(this);

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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.save_button:
                try
                {
                    savePage(view);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                displayPhoto(selectedImageUri);
                if (counter == 6)
                    counter = 1;
                else
                    counter++;
            }
        }
    }

    public void displayPhoto(Uri uri)
    {
        ImageView img;
        // Only allow up to six images
        switch (counter)
        {
            case 1:
                img = (ImageView) getView().findViewById(R.id.display_image_1);
                break;
            case 2:
                img = (ImageView) getView().findViewById(R.id.display_image_2);
                break;
            case 3:
                img = (ImageView) getView().findViewById(R.id.display_image_3);
                break;
            case 4:
                img = (ImageView) getView().findViewById(R.id.display_image_4);
                break;
            case 5:
                img = (ImageView) getView().findViewById(R.id.display_image_5);
                break;
            case 6:
                img = (ImageView) getView().findViewById(R.id.display_image_6);
                break;
            default:
                img = (ImageView) getView().findViewById(R.id.display_image);
                break;
        }
        getView().findViewById(R.id.collageBgView).setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                return true;
            }
        });
        img.setOnTouchListener(new MultiTouchListener());
        img.setImageURI(uri);
    }

    public void savePage(View view) throws IOException
    {
        Bitmap bmpout = loadBitmapFromView(MyApplication.getAppContext(), (ViewGroup) getView().findViewById(R.id.ComicPage));
        //Bitmap to Uri
        int counter = 0;
        OutputStream fOut = null;
        File file; // the File to save to
        String path = null;
        File sd = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File directory = new File(sd, "Kapow/Pages");
        try
        {
            directory.mkdirs();
        } catch (Exception e)
        {
        }
        file = new File(sd, "Kapow/Pages" + "ComicPage.png");
        while (file.exists())
        {
            counter++;
            file = new File(sd, "Kapow/Pages" + "ComicPage" + "(" + counter + ").png");
        }
        try
        {
            fOut = new FileOutputStream(file);
            bmpout.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            path = MediaStore.Images.Media.insertImage(MyApplication.getAppContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            Toast.makeText(MyApplication.getAppContext(), "Save Successful", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(MyApplication.getAppContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            return;
        }
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
}
