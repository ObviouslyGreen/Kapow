package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import com.thuytrinh.android.collageviews.MultiTouchListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BubbleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BubbleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BubbleFragment extends Fragment implements View.OnClickListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int SELECT_PICTURE = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Message> messages;
    AwesomeAdapter adapter;
    EditText text;
    static String sender;
    private String imageUrl;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BubbleFragment newInstance(String param1, String param2)
    {
        BubbleFragment fragment = new BubbleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BubbleFragment()
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_panel, container, false);
        text = (EditText) v.findViewById(R.id.text);
        Drawable bubble = getResources().getDrawable(R.drawable.speech_left);
        bubble.setAlpha(0);
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
        text.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    text.setText("");
                    return true;
                }
                return false;
            }
        });


        ((Button) v.findViewById(R.id.done_button))
                .setOnClickListener((View.OnClickListener) this);
        ((Button) v.findViewById(R.id.save_panel_button))
                .setOnClickListener((View.OnClickListener) this);
        /*messages = new ArrayList<Message>();
        messages.add(new Message("testyoyoswag.", true));
        adapter = new AwesomeAdapter(MyApplication.getAppContext(), messages);
        ListView listV = (ListView) v.findViewById(R.id.listView1);
        listV.setAdapter(adapter);*/
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
        switch(view.getId()){
            case R.id.done_button:
                try
                {
                    finishBubble(view);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.save_panel_button:
                try
                {
                    savePanel(view);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void savePanel(View view) throws IOException
    {
        Bitmap bmpout = loadBitmapFromView(MyApplication.getAppContext(), (ViewGroup) getView().findViewById(R.id.ComicPanel));
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
        file = new File(sd, "Kapow/"+"ComicPanel.png");
        while (file.exists()) {
            counter++;
            file = new File(sd, "Kapow/" + "ComicPanel" + "(" + counter + ").png");
        }
        try{
            fOut = new FileOutputStream(file);
            bmpout.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            Log.w("myAPP","SWAG");
            path = MediaStore.Images.Media.insertImage(MyApplication.getAppContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(MyApplication.getAppContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            return;
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
            }
        }
    }

    public void displayPhoto(Uri uri)
    {
        ImageView img;
        img = (ImageView) getView().findViewById(R.id.display_image);
        getView().findViewById(R.id.collageBgView).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return true;
            }
        });
        img.setOnTouchListener(new MultiTouchListener());
        img.setImageURI(uri);
    }

    private void finishBubble(View v) throws IOException
    {
        TextView tv = (TextView) getView().findViewById(R.id.text);
        tv.setAlpha(0);
        //Bitmap bmpout = loadBitmapFromView(MyApplication.getAppContext(), tv);
        EditText et = (EditText) getView().findViewById(R.id.text);
        ImageView img = (ImageView) getView().findViewById(R.id.test);
        et.buildDrawingCache();
        img.setImageBitmap(et.getDrawingCache());
        img.setOnTouchListener(new MultiTouchListener());
    }

    public static Bitmap loadBitmapFromView(Context context, ViewGroup v) {

       /* Toast.makeText(context,
                v.getMeasuredHeight() + "::::::::::::" + v.getMeasuredWidth(),
                Toast.LENGTH_LONG).show();*/

        if (v.getMeasuredHeight() > 0) {

            v.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.ARGB_8888);

            v.draw(new Canvas(b));

            return b;

        }

        return null;
        /*img.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        img.layout(0, 0, img.getMeasuredWidth(), img.getMeasuredHeight());
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache(true);
        Bitmap bmp = Bitmap.createBitmap(img.getDrawingCache());
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap b = bmp.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == 0xffff0000) {
                pixels[i] = Color.alpha(Color.TRANSPARENT);
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;*/
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

    void addNewMessage(Message m)
    {
        adapter.notifyDataSetChanged();
    }
}
