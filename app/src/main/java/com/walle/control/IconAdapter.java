package com.walle.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 *
 */

public class IconAdapter extends BaseAdapter {
    private Context context;
    private List<AppEntity> entities;

    public IconAdapter(Context context,List<AppEntity> entities){
        this.context = context;
        this.entities =entities;
    }
    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.icon_item,null);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
            viewHolder.tv_appName = (TextView) view.findViewById(R.id.tv_appName);
            viewHolder.tv_packageName = (TextView) view.findViewById(R.id.tv_packageName);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_appName.setText(entities.get(i).appName);
        viewHolder.tv_packageName.setText(entities.get(i).packageName);
        viewHolder.imageView.setImageDrawable(entities.get(i).drawable);
        return view;
    }

    class ViewHolder{
        ImageView imageView;
        TextView tv_appName,tv_packageName;
    }
}
