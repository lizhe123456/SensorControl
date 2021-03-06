package com.sensorcontrol.ui.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.sensorcontrol.R;
import com.sensorcontrol.bean.CmdBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhe on 2017/9/21 0021.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BtnAdapter extends RecyclerView.Adapter<BtnAdapter.ViewHolder>{

    private Context mContext;

    private List<CmdBean> mList;

    private OnItemClickListener OnItemClickListener;
    private OnLongClickListener OnLongClickListener;
    private OnAddItemListener OnAddItemListener;
    private OnSelectFileListener OnSelectFileListener;
    private boolean flag = true;

    public BtnAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
        mList.add(null);
        mList.add(null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.btn_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(position);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setmList(List<CmdBean> list) {
        if (list != null) {
            this.mList = new ArrayList<>();
            mList.addAll(list);
            mList.add(null);
            mList.add(null);
        }
        notifyDataSetChanged();
    }

    public void upData(CmdBean cmdBean){
        mList.add(0,cmdBean);
        notifyDataSetChanged();
    }

    public void upItem(List<CmdBean> list){
        if (list != null) {
            mList = list;
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Button textView;
        Button imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_text);
            imageView = itemView.findViewById(R.id.add_item);
        }

        public void update(final int position){

            if (mList.get(position) == null) {
                if (mList.size()-1 == position){
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("发送文件");
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OnSelectFileListener.onSelectFile();
                        }
                    });
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OnAddItemListener.onAddItem(mList.get(position));
                        }
                    });
                }
            }else {
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(mList.get(position).getName());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnItemClickListener.onClick(position, mList.get(position));
                    }
                });
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        OnLongClickListener.onLongClick(position, mList);
                        return true;
                    }
                });

            }
        }
    }

    public List<CmdBean> getmList() {
        List list = new ArrayList();
        list.addAll(mList);
        list.remove(list.size() -1);
        return list;
    }

    public void setOnItemClickListener(BtnAdapter.OnItemClickListener onItemClickListener) {
        OnItemClickListener = onItemClickListener;
    }

    public void setOnLongClickListener(BtnAdapter.OnLongClickListener onLongClickListener) {
        OnLongClickListener = onLongClickListener;
    }

    public void setOnAddItemListener(BtnAdapter.OnAddItemListener onAddItemListener) {
        OnAddItemListener = onAddItemListener;
    }

    public void setOnSelectFileListener(BtnAdapter.OnSelectFileListener onSelectFileListener) {
        OnSelectFileListener = onSelectFileListener;
    }

    public interface OnItemClickListener{
        void onClick(int position,CmdBean cmdBean);
    }

    public interface OnLongClickListener{
        void onLongClick(int position,List<CmdBean> mList);
    }

    public interface OnAddItemListener{
        void onAddItem(CmdBean cmdBean);
    }

    public interface OnSelectFileListener{
        void onSelectFile();
    }
}
