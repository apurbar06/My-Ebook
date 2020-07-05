package com.example.myebook.handler;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myebook.R;

public class ReadableEbookListAdapter extends BaseAdapter {


    private final Activity mContext;
    private static String[] mEbooks;
    private boolean mForDelete;
    public boolean[] mCheckBoxState;

    public ReadableEbookListAdapter(Activity context, String[] ebooks, boolean forDelete) {
        super();

        mContext = context;
        mEbooks = ebooks;
        mForDelete = forDelete;
        mCheckBoxState= new boolean[ebooks.length];
    }

    public static String getItemAtPosition(int position) {
        return mEbooks[position];
    }


    @Override
    public int getCount() {
        return mEbooks.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // inflate the layout for each item of mGridView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.readable_ebook, parent, false);

        // get the references
        TextView titleText = (TextView) convertView.findViewById(R.id.ebook_text);
        ImageView iconImage = (ImageView) convertView.findViewById(R.id.ebook_image);
        final CheckBox checkBox = convertView.findViewById(R.id.ebook_check_box);

        //extracting the extension like pdf/ppt/pptx
        String last3 = ((mEbooks[position] == null) || (mEbooks[position].length() < 3)) ? mEbooks[position] : mEbooks[position].substring(mEbooks[position].length() - 3);
        String last4 = ((mEbooks[position] == null) || (mEbooks[position].length() < 4)) ? mEbooks[position] : mEbooks[position].substring(mEbooks[position].length() - 4);

        Log.d(String.valueOf(mContext), "getView: " + last3);

        // if for delete add external listener in list view and check box
        if (mForDelete) {
            // view listener
            View.OnClickListener viewListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = checkBox.isChecked();
                    if (checked) {
                        // if checkbox was previously checked make mCheckBoxState false and uncheck the checkbox
                        checkBox.setChecked(false);
                        mCheckBoxState[position]=false;
                    } else {
                        // if checkbox was previously unchecked make mCheckBoxState true and check the checkbox
                        checkBox.setChecked(true);
                        mCheckBoxState[position]=true;
                    }
                }
            };
            // checkbox listener
            View.OnClickListener checkBoxListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = checkBox.isChecked();
                    if (checked) {
                        // if checkbox is checked make mCheckBoxState true
                        mCheckBoxState[position]=true;
                    } else {
                        // if checkbox is unchecked make mCheckBoxState false
                        mCheckBoxState[position]=false;
                    }
                }
            };
            //  if it is for delete show checkbox
            checkBox.setVisibility(View.VISIBLE);
            // attach the listener
            convertView.setOnClickListener(viewListener);
            // attach the listener
            checkBox.setOnClickListener(checkBoxListener);
        } else {
            //  if it is not for delete hide checkbox
            checkBox.setVisibility(View.GONE);
        }


        // Set the title and imageIcon
        titleText.setText(mEbooks[position]);
        //set pdf/ppt icon dynamically if the file is an pdf/ppt file
        if(last3.equals("pdf")) {
            String uri = "@drawable/ic_pdf_read";
            int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
            Drawable res = mContext.getResources().getDrawable(imageResource);
            iconImage.setImageDrawable(res);
        } else if(last3.equals("ppt") || last4.equals("pptx")) {
            String uri = "@drawable/ic_ppt_read";
            int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
            Drawable res = mContext.getResources().getDrawable(imageResource);
            iconImage.setImageDrawable(res);
        }


        return convertView;
    }
}
