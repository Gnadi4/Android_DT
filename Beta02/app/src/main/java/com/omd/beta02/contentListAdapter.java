package com.omd.beta02;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class contentListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<content> contentlist;

    public contentListAdapter(Context context, int layout, ArrayList<content> contentlist){
        this.context = context;
        this.layout = layout;
        this.contentlist = contentlist;
    }

    @Override
    public int getCount() {
        return contentlist.size();
    }

    @Override
    public Object getItem(int position) {
        return contentlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView2;
        TextView textView_retags1;
        TextView textView_retags2;
        TextView textView_retags3;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);

            holder.textView_retags1 = (TextView) row.findViewById(R.id.textView_retags1);
            holder.textView_retags2 = (TextView) row.findViewById(R.id.textView_retags2);
            holder.textView_retags3 = (TextView) row.findViewById(R.id.textView_retags3);
            holder.imageView2= (ImageView) row.findViewById(R.id.imageView2);
            holder.imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        content content = contentlist.get(position);

        holder.textView_retags1.setText(content.getTags1());
        holder.textView_retags2.setText(content.getTags2());
        holder.textView_retags3.setText(content.getTags3());
        holder.imageView2.setImageURI(Uri.parse(content.getUri()));
        holder.imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        return row;


        //layout inflater 는 레이아웃의 동적 할당을 도와주는 역할을 한다
        //즉, layout inflater는 xml에 미리 정의된 자원 들을 view의 형태로 반환해 준다.
        //content에서 받아온 정보를 토대로 새로운 view를 동적 할당 해주는 코드이다.
        //holder 에서는 불러온 view에 content에서 받아온 정보를 넘겨주게 된다



    }
}
