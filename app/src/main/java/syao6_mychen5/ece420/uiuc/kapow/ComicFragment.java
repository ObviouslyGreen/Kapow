package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicFragment extends Fragment
{

    private static final int SELECT_PAGE = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ArrayList<String> pages = new ArrayList<String>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ComicFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComicFragment newInstance(String param1, String param2)
    {
        ComicFragment fragment = new ComicFragment();
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
        // Button to add a page to comic book
        View v = inflater.inflate(R.layout.fragment_comic, container, false);
        v.findViewById(R.id.add_page_button)
                .setOnClickListener(new View.OnClickListener()
                                    {
                                        public void onClick(View view)
                                        {
                                            Intent intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(Intent.createChooser(intent,
                                                    "Select Picture"), SELECT_PAGE);
                                        }
                                    }
                );

        // Button to create the pdf
        v.findViewById(R.id.create_comic_button)
                .setOnClickListener(new View.OnClickListener()
                                    {
                                        public void onClick(View view)
                                        {
                                            ProgressDialog pd = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            pd.setCancelable(false);
                                            pd.setMessage("Processing...");
                                            pd.show();
                                            String comicTitle = ((EditText) getView().findViewById(R.id.comic_book_title)).getText().toString();

                                            // Create the PDF
                                            File sd = Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_PICTURES);
                                            File directory = new File(sd, "Kapow/Comics");
                                            directory.mkdirs();

                                            String comicPath = directory.getAbsolutePath() + "/" + comicTitle + ".pdf";
                                            File currComic = new File(comicPath);

                                            Document doc = new Document();
                                            try
                                            {
                                                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(comicPath));
                                            } catch (DocumentException e)
                                            {
                                                e.printStackTrace();
                                            } catch (FileNotFoundException e)
                                            {
                                                e.printStackTrace();
                                            }
                                            doc.open();
                                            Image img = null;
                                            for (int i = 0; i < pages.size(); i++)
                                            {
                                                try
                                                {
                                                    img = Image.getInstance(pages.get(i));
                                                } catch (BadElementException e)
                                                {
                                                    e.printStackTrace();
                                                } catch (IOException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                                float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                                                        - doc.rightMargin()) / img.getWidth()) * 100;

                                                img.scalePercent(scaler);
                                                try
                                                {
                                                    doc.add(img);
                                                } catch (DocumentException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                            doc.close();
                                            pages.clear();
                                            pd.dismiss();
                                        }
                                    }
                );
        // Inflate the layout for this fragment
        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SELECT_PAGE)
            {
                // Add file path of image to pages arraylist
                Uri selectedImageUri = data.getData();
                pages.add(getImagePath(selectedImageUri));
                displayPhoto(selectedImageUri);
            }
        }
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

    public void displayPhoto(Uri uri)
    {
        try
        {
            ImageView img = (ImageView) getView().findViewById(R.id.display_page);
            img.setImageURI(uri);
        } catch (NullPointerException e)
        {
            Log.w("Null Image", "this should never happen");
        }
    }

    public String getImagePath(Uri uri)
    {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
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
