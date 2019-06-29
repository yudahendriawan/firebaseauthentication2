package com.example.firebaseauthentication2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddItemsToDatabase extends AppCompatActivity {

    private static final String TAG = "AddItemsToDatabase";

    private Button mAddToDB;
    private EditText mNewFood;

    //add Firebase Database Stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items_to_database);
        mAddToDB = (Button) findViewById(R.id.btnAddNewFood);
        mNewFood = (EditText) findViewById(R.id.add_new_food);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:"+user.getUid());
                    toastMessage("Successfully signed in with: "+user.getEmail());
                } else{
                    //user is signed out
                    Log.d(TAG,"onAuthStatedChanged:signed_out");
                    toastMessage("Successfully signed out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: "+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //failed to read value
                toastMessage("Failed to alter database.");
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        mAddToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to add object to database.");
                String newFood = mNewFood.getText().toString();
                if(!newFood.equals("")){
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    myRef.child(userId).child("Food").child("Favorite foods").child(newFood).setValue(true);
                    toastMessage("Adding "+newFood+" to Database...");

                    //reset text to null
                    mNewFood.setText("");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
