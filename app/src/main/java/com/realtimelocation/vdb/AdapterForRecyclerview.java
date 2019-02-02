package com.realtimelocation.vdb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdapterForRecyclerview extends RecyclerView.Adapter<AdapterForRecyclerview.MyViewHolder> {

    private List<JakesEntity> list;
  private   MainActivity.OnBottomReachedListener onBottomReachedListener;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, tv_description, tv1, tv2, tv3;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_title);
            tv_description = (TextView) view.findViewById(R.id.tv_description);
            tv1 = (TextView) view.findViewById(R.id.tv1);
            tv2 = (TextView) view.findViewById(R.id.tv2);
            tv3 = (TextView) view.findViewById(R.id.tv3);
        }
    }
    public void setOnBottomReachedListener(MainActivity.OnBottomReachedListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }

    public AdapterForRecyclerview(List<JakesEntity> moviesList) {
        this.list = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recyclerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (position == list.size() - 2){

            onBottomReachedListener.onBottomReached(position);

        }
        JakesEntity jakesEntity = list.get(position);
        if (jakesEntity.getFullName() != null)
            holder.title.setText(jakesEntity.getFullName());
        if (jakesEntity.getDescription() != null)
            holder.tv_description.setText(jakesEntity.getDescription());
        if (jakesEntity.getLanguage() != null && !jakesEntity.getLanguage().equalsIgnoreCase(""))
            holder.tv1.setText(jakesEntity.getLanguage());
        else
            holder.tv1.setVisibility(View.GONE);
        if (jakesEntity.getOpenIssuesCount() != null)
            holder.tv2.setText(String.valueOf(jakesEntity.getOpenIssuesCount()));
        else
            holder.tv2.setVisibility(View.GONE);
        if (jakesEntity.getForksCount() != null)
            holder.tv3.setText(String.valueOf(jakesEntity.getForksCount()));
        else
            holder.tv3.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}