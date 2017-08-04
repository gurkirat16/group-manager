package com.techeducation.groupmanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitaly on 5/3/17.
 */

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.MyViewHolder> {

    List<AllUsersBean> allUsersBeenList;
    List<AllUsersBean> tempList;

    //MyViewHolders initializes the views in listIte
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtName=(TextView)itemView.findViewById(R.id.txtNameAll);
            Log.i("showObj3","view holder recent+ "+itemView.toString());
        }
    }

    public AllUsersAdapter(List<AllUsersBean> allUsersBeenList) {
        this.allUsersBeenList = allUsersBeenList;
        tempList = new ArrayList<>();
        tempList.addAll(this.allUsersBeenList);
        Log.i("showObj2","list of recent "+allUsersBeenList.toString());
    }

    //onCreateViewHolder inflates the listItem
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_users_item,parent,false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder puts the data into views defined in listItem
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllUsersBean allUsersBean = allUsersBeenList.get(position);
       // holder.profileImg.setImageResource(allUsersBean.getProfilePhoto());
        Log.i("showobj2","recent "+allUsersBean.toString());
        holder.txtName.setText(allUsersBean.getusername());
    }

    //Get size of List
    @Override
    public int getItemCount()
    {
        Log.i("showObj2","get item count: "+allUsersBeenList.size());
        return allUsersBeenList.size();
    }

    //Filters the list
    public boolean listUpdate(String str){
        allUsersBeenList.clear();
        if(str.isEmpty()){
            allUsersBeenList.addAll(tempList);
        }
        else{
            for(AllUsersBean object : tempList){
                if(object.getusername().toLowerCase().startsWith(str.toLowerCase())){
                    allUsersBeenList.add(object);
                }
            }
        }
        notifyDataSetChanged();
        if(allUsersBeenList.size()==0)
            return false;
        else
            return true;
    }
}
