package com.example.dmberry.HereIAm;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    final static String _ID = "_id";
    final static String CODENAME = "codeName";
    final static String LON="lon";
    final static String LAT="lat";
    final static String USERNAME="username";
    final static String TIMEDATE="timeDate";


    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    SimpleCursorAdapter myAdapter;
    Cursor mCursor;
    AlertDialog actions;

    final static String[] all_columns = { _ID, CODENAME, USERNAME,TIMEDATE,LAT,LON };

    ListView listView;
    ImageButton addButton;
    Button sendButton;


    FusedLocationProviderClient client;
    double lat=0;
    double lon=0;

    boolean found;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();
        requestPermission();


        if(isFirstTime())
        {
            Toast.makeText(getApplicationContext(), "New Mem ", Toast.LENGTH_SHORT).show();
            //edit first userDatabase entry or add user info
            newMember();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Existing Mem ", Toast.LENGTH_SHORT).show();
        }

        client=LocationServices.getFusedLocationProviderClient(MainActivity.this);

        databaseReference=FirebaseDatabase.getInstance().getReference("users");

        addFriendButton();
        addLongClickListener();
        addSendButton();
        addListClickListener();


        databaseReference.orderByChild("codeName").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String latitude =dataSnapshot.child("lat").getValue().toString();
                String longitude=dataSnapshot.child("lon").getValue().toString();
                String time=dataSnapshot.child("timeDate").getValue().toString();

                String code=dataSnapshot.child("codeName").getValue().toString();

                ContentValues values = new ContentValues();
                db = dbHelper.getWritableDatabase();


                values.put(LAT,latitude);
                values.put(LON,longitude);
                values.put(TIMEDATE,time);

                db.update(dbHelper.NAME,values,CODENAME+" =?",new String[]{code});

                onResume();

                Toast.makeText(getApplicationContext(), "Child Changed: ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //notifies when new user and user updates data
      /* databaseReference.orderByChild("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(!isFirstTime())
               {
                   Toast.makeText(getApplicationContext(), "Here-Update", Toast.LENGTH_SHORT).show();
                   mCursor.moveToFirst();
                   String codeName=null;

                   for(int i=0;i<getNumFriends();i++)
                   {
                       Toast.makeText(getApplicationContext(), "For: "+i, Toast.LENGTH_SHORT).show();
                       mCursor.moveToNext();
                       codeName=mCursor.getString(1);
                       searchUser(codeName,i+1);

                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    public void initialize()
    {
        dbHelper=new DatabaseOpenHelper(this);
        db = dbHelper.getReadableDatabase();

        listView=(ListView)findViewById(R.id.listView);
        addButton=(ImageButton)findViewById(R.id.addFriendButton);
        sendButton=(Button)findViewById(R.id.sendButton);
    }

    public void searchUser(final String codeName, final int cursorNum)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.orderByChild("codeName").equalTo(codeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String lat=datas.child("lat").getValue().toString();
                    String lon=datas.child("lon").getValue().toString();
                    String timeDate=datas.child("timeDate").getValue().toString();

                    ContentValues values = new ContentValues();
                    db = dbHelper.getWritableDatabase();


                    values.put(LAT,lat);
                    values.put(LON,lon);
                    values.put(TIMEDATE,timeDate);
                    String i=Integer.toString(cursorNum);

                    db.update(dbHelper.NAME,values,_ID+"="+i,null);
                    Toast.makeText(getApplicationContext(), "UpdateDB", Toast.LENGTH_SHORT).show();

                }

                /*if (dataSnapshot.getValue() != null) {
                    ContentValues values = new ContentValues();
                    db = dbHelper.getWritableDatabase();

                    User u=dataSnapshot.getValue(User.class);

                    values.put(LAT,u.getLat());
                    values.put(LON,u.getLon());
                    values.put(TIMEDATE,u.getTimeDate());
                    String i=Integer.toString(cursorNum);

                    db.update(dbHelper.NAME,values,_ID+"="+i,null);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public long getNumFriends()
    {
            db=dbHelper.getReadableDatabase();
            long friends=DatabaseUtils.queryNumEntries(db,dbHelper.NAME);
            db.close();
            friends--;//subtract the user
            return friends;
    }

    public void addListClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationManager locateManager=(LocationManager)getSystemService(LOCATION_SERVICE);
                boolean locationEnabled = locateManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!locationEnabled) {
                    Toast.makeText(getApplicationContext(), "Please Turn On Location", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                else {
                        lat=Double.parseDouble(mCursor.getString(4));
                        lon=Double.parseDouble(mCursor.getString(5));

                    Toast.makeText(getApplicationContext(), lat+" "+lon, Toast.LENGTH_SHORT).show();

                    if (lon == 0 && lat == 0) {
                        Toast.makeText(getApplicationContext(), "Latitude and Longitude Invalid", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                        intent.putExtra("lat",lat);
                        intent.putExtra("lon",lon);
                        startActivityForResult(intent,0);

                    }
                }
            }
        });
    }

    public void addSendButton()
    {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Permission Denied: App is exiting", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    return;
                }
                client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lat=0;
                        lon=0;
                        if(location==null)
                        {
                            Toast.makeText(getApplicationContext(), "Location Unknown, Please turn on location or move somewhere else", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            lat=location.getLatitude();
                            lon=location.getLongitude();

                            mCursor.moveToFirst();
                            String codeName=mCursor.getString(1);
                            String longitude=Double.toString(lon);
                            String latitude=Double.toString(lat);
                            String userName=mCursor.getString(2);
                            String timeDate=getTimeDate();

                            //update SQLite DB
                            /*ContentValues values = new ContentValues();
                            db = dbHelper.getWritableDatabase();


                            values.put(LON,longitude);
                            values.put(LAT,latitude);
                            values.put(TIMEDATE,timeDate);
                            db.update(dbHelper.NAME, values, _ID + "=1", null);

                            onResume();//wouldn't update w/ this part
*/

                            User user =new User(userName,longitude,latitude,codeName,timeDate);

                            //update Firebase database
                            databaseReference.child(codeName).setValue(user);

                            //Toast.makeText(getApplicationContext(), codeName+" lon:"+longitude+" lat:"+latitude+" user:"+userName+" date:"+timeDate, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }

    public void addFriendButton()
    {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = dbHelper.getReadableDatabase();
                long count = DatabaseUtils.queryNumEntries(db, dbHelper.NAME);
                db.close();
                if(count<=3) {
                    Intent intent = new Intent(MainActivity.this, NewFriend.class);
                    startActivityForResult(intent, 0);
                }
                else
                    Toast.makeText(getApplicationContext(), "You have reached the max", Toast.LENGTH_SHORT).show();


            }
        });
    }


    public void newMember()
    {
        Intent intent = new Intent(MainActivity.this, NewMember.class);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==0)
        {
            if (resultCode == 1) {
                String userName = data.getStringExtra("userName");
                String codeName = data.getStringExtra("codeName");

                        Toast.makeText(getApplicationContext(), "Good Username", Toast.LENGTH_SHORT).show();
                        ContentValues values = new ContentValues();
                        db = dbHelper.getWritableDatabase();
                        values.put(USERNAME, userName);
                        values.put(CODENAME, codeName);
                        db.update(dbHelper.NAME, values, _ID + "=" + 1, null);
            }

            else if(resultCode==2)
            {
                String userName = data.getStringExtra("friendName");
                String codeName = data.getStringExtra("friendCodeName");

                ContentValues values = new ContentValues();
                db = dbHelper.getWritableDatabase();

                values.put(USERNAME,userName);
                values.put(CODENAME,codeName);
                values.put(TIMEDATE,"MM/dd/yyyy 12:00AM");
                values.put(LAT,"0");
                values.put(LON,"0");
                db.insert(dbHelper.NAME,null,values);
                // new AsyncTaskClass().execute();
                mCursor=db.query(dbHelper.NAME, all_columns, null, null, null, null,
                        null);
                myAdapter.swapCursor(mCursor);
            }
        }
    }

    public void addLongClickListener()
    {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialogBox();
                return true;
            }
        });

    }
    public void dialogBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String id=Integer.toString(mCursor.getInt(0));
                if(id.equalsIgnoreCase("1"))
                {
                    Toast.makeText(getApplicationContext(), "You may not delete your profile", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    deleteItem(id);
                    dialog.dismiss();
                    mCursor = db.query(dbHelper.NAME, all_columns, null, null,
                            null, null, null);
                    myAdapter.swapCursor(mCursor);
                }
            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        actions = builder.create();
        actions.show();
    }

    public Integer deleteItem(String task) {
        db = dbHelper.getWritableDatabase();
        return db.delete(dbHelper.NAME, _ID+"=?", new String[]{task});
    }


    public boolean isFirstTime()
    {
        //check if new or old member
        Cursor cursor = db.query(DatabaseOpenHelper.NAME, new String[] { "*" }, null,
                null, null, null, null, null);
        cursor.moveToFirst();
        if(cursor.getString(1).equalsIgnoreCase("None"))
            return true;
        return false;
    }

    //returns the current Time and Date
    public String getTimeDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date=new Date();
        return dateFormat.format(date);
    }

    public void onResume()
    {
        super.onResume();
        db=dbHelper.getWritableDatabase();
        mCursor=db.query(DatabaseOpenHelper.NAME,all_columns,null,null,null,null,null);
        myAdapter=new SimpleCursorAdapter(this,R.layout.multi_list,mCursor,
                new String[]{"username","codeName","timeDate"},
                new int[]{R.id.listName,R.id.listUser,R.id.listDatTime});
        listView.setAdapter(myAdapter);
    }

    public void onPause()
    {
        super.onPause();
        if(db!=null)
            db.close();
        mCursor.close();
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},1);
    }

}