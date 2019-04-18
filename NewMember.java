package com.example.dmberry.HereIAm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewMember extends AppCompatActivity {

    DatabaseReference fBase;


    EditText userName,codeName;
    Button doneButton;

    TextView codeFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        fBase=FirebaseDatabase.getInstance().getReference("users");

        userName=(EditText)findViewById(R.id.userNameInput);
        codeName=(EditText)findViewById(R.id.codeNameInput);
        doneButton=(Button)findViewById(R.id.newMemButton);
        codeFound=(TextView)findViewById(R.id.codeFound);

        codeFound.setText("");

        //set username and codename char limit
        userName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        codeName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});


        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                codeFound.setText("");
                String name=userName.getText().toString();
                String code=codeName.getText().toString();
                code=code.trim();


                if(isFilled(name)&&isFilled(code))
                {
                    if(code.equalsIgnoreCase("None")||name.equalsIgnoreCase("None"))
                        Toast.makeText(getApplicationContext(), "You may not use: None", Toast.LENGTH_SHORT).show();
                    else
                        searchUser(code,name);
                }
                else
                    Toast.makeText(getApplicationContext(), "Please fill all", Toast.LENGTH_SHORT).show();

            }
        });

    }

    //prevent new members from skipping this part
    @Override
    public void onBackPressed()
    {
        Toast.makeText(getApplicationContext(), "Back Button Disabled", Toast.LENGTH_SHORT).show();
    }

    //checks if editText is filled out or not
    public boolean isFilled(String x)
    {
        if(x.matches(""))
            return false;
        return true;
    }

    public void searchUser(final String codeName, final String username)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.orderByChild("codeName").equalTo(codeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    //user exists, do something
                    codeFound.setText("Username Already in Use");
                } else {
                    //user does not exist, do something else
                    Intent output=new Intent();
                    output.putExtra("codeName",codeName);
                    output.putExtra("userName",username);
                    setResult(1, output);
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}


