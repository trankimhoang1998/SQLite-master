package com.example.tnhieu.sqlite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.service.media.MediaBrowserService;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Mydatabase db;
    private List<Contact> contacts;
    static private Contact c;
    private  int posClick = -1;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private ArrayList<String> mlist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Mydatabase(this);
        contacts = new ArrayList<>();
        getData();
        handle();
    }

    private void getData(){
        contacts.clear();
        contacts = db.getAllContacts();
        db.close();
    }

    private void handle(){
       mListView=(ListView)findViewById(R.id.lvcontact) ;
       for(int i=0 ;i<contacts.size();i++)
       {
           mlist.add(contacts.get(i).getmName());
       }
       mAdapter = new ArrayAdapter<>( MainActivity.this,android.R.layout.simple_list_item_1,mlist);
       mListView.setAdapter(mAdapter);
       mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_optiont,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.addContact:
                Intent intent = new Intent(MainActivity.this, activity_add.class);
                startActivityForResult(intent,1);
                break;
            case R.id.deleteAll:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("DROP ALL DATA");
                alertBuilder.setMessage("This action will deleta all data in SQLite. Are you sure?");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAll();
                    }
                });
                alertBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteAll(){
        db.deleteAllContact();
        contacts.clear();
        mAdapter.notifyDataSetChanged();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== 1 && resultCode == RESULT_OK) {
            Contact contact = (Contact) data.getExtras().getSerializable("RETURN");
            contacts.add(contact);
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode == 2 && resultCode == RESULT_OK){
            String contactID = data.getStringExtra("ID");
            int id = Integer.parseInt(contactID);
            int pos = 0;
            for (int i=0;i<contacts.size();i++){
                if (contacts.get(i).getmId()==id) {
                    pos = i;
                    break;
                }
            }
            contacts.remove(pos);
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode == 2 && resultCode == 1){
            String editID = data.getStringExtra("ID");
            int id = Integer.parseInt(editID);
            int pos=0;
            for (int i=0;i<contacts.size();i++){
                if (contacts.get(i).getmId()==id) {
                    pos = i;
                    break;
                }
            }
            contacts.get(pos).setmName(db.getContact(id).getmName());
            db.close();
            mAdapter.notifyDataSetChanged();
        }
    }




}