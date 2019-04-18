package com.example.dmberry.HereIAm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewFriend extends AppCompatActivity {

    EditText friendName,friendCodeName;
    Button addFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        friendName=(EditText)findViewById(R.id.friendNameInput);
        friendCodeName=(EditText)findViewById(R.id.friendCodeName);
        addFriend=(Button)findViewById(R.id.newFriendButton);

        friendName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        friendCodeName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=friendName.getText().toString();
                String code=friendCodeName.getText().toString();
                Intent output=new Intent();

                if(isFilled(name)&&isFilled(code))
                {
                    output.putExtra("friendName",name);
                    output.putExtra("friendCodeName",code);
                    setResult(2, output);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "Please fill all", Toast.LENGTH_SHORT).show();

            }
        });
    }
    //checks if editText is filled out or not
    public boolean isFilled(String x)
    {
        if(x.matches(""))
            return false;
        return true;
    }
}
