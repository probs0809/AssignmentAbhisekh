package com.example.assignment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<Employees> al = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lv = findViewById(R.id.list_view);

        new Thread(() -> new Handler(getMainLooper()).post(() -> FirebaseDatabase.getInstance().getReference("Employees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                al.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Employees emp = ds.getValue(Employees.class);
                    al.add(emp);
                }
                Student_adapter sa = new Student_adapter(al);
                lv.setAdapter(sa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }))).start();

    }
}


class Student_adapter extends BaseAdapter {

    ArrayList<Employees> al;
    Student_adapter(ArrayList<Employees> al){
        this.al = al;
    }

    public int getCount(){
        return al.size();
    }
    public Object getItem(int position)
    {
        return  al.get(position);
    }
    public long getItemId(int position)
    {
        return position;
    }


    public View getView(int position, View singleView, ViewGroup parent)
    {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        singleView=inflater.inflate(R.layout.list_containt,parent,false);
        TextView empid=singleView.findViewById(R.id.empid);
        TextView designation=singleView.findViewById(R.id.designation);
        TextView name=singleView.findViewById(R.id.name);
        ImageView img=singleView.findViewById(R.id.im1);
        empid.setText(("Emp Id: "+al.get(position).EmpId));
        name.setText(("Name : "+al.get(position).Name));
        designation.setText(("Designation : " + al.get(position).Designation));
        Picasso.get().load(al.get(position).PhotoUrl).fit().centerCrop().into(img);
        return singleView;
    }
}