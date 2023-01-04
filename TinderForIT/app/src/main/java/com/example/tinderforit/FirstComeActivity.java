package com.example.tinderforit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirstComeActivity extends Activity {

    ImageView imgGallery;
    Button btnGallery;
    private int GALlERY_REG_CODE = 1000 ;

    private Button btnUploadImg;

    private String userid;
    private String email;

    private TextInputLayout LfName;
    private TextInputEditText TfName;
    private String firstName;

    private TextInputLayout LlName;
    private TextInputEditText TlName;
    private String lastName;

    DatePicker picker;
    private TextInputEditText TDOB;
    private TextInputLayout LDOB;
    private String dateOfBirth;

    private Button btnSend;

    private ImageView avatar;

    private RadioButton radFemale;
    private RadioButton radMale;
    private String gender;

    Uri imageUri;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference mDatabase, dataUser;
    FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_come);

        // Plumbing controls

        btnSend = (Button) findViewById(R.id.fc_sendinfo);

        TfName = (TextInputEditText) findViewById(R.id.textFirstName);
        LfName = (TextInputLayout) findViewById(R.id.layouttextFirstName);

        TlName = (TextInputEditText) findViewById(R.id.textLastName);
        LlName = (TextInputLayout) findViewById(R.id.layouttextLastName);

        LDOB = (TextInputLayout) findViewById(R.id.layouttextDOB);
        TDOB = (TextInputEditText) findViewById(R.id.textDOB);

        radFemale = (RadioButton) findViewById(R.id.radio_female);
        radMale = (RadioButton) findViewById(R.id.radio_male);

        avatar = findViewById(R.id.avatar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();


        LDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        FirstComeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                TDOB.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Check valid Input

                    if (TfName.getText().toString().isEmpty()) {
                        TfName.setError("First name is missing");
                        return;
                    }

                    if (TlName.getText().toString().isEmpty()) {
                        TfName.setError("Last name is missing");
                        return;
                    }

                    if (TDOB.getText().toString().isEmpty()) {
                        TDOB.setError("Last name is missing");
                        return;
                    }

                    // Get input Value

                    if (radFemale.isChecked()){
                        gender = "Female";
                    }

                    if (radMale.isChecked()){
                        gender = "Male";
                    }

                    firstName = String.valueOf(LfName.getEditText().getText());
                    lastName = String.valueOf(LlName.getEditText().getText());
                    dateOfBirth = String.valueOf(LDOB.getEditText().getText());
                    email = user.getEmail();
                    userid = user.getUid();

                    // Upload Object(user) to Firebase

                    Map userProfile = new HashMap();
                    userProfile.put("email",email);
                    userProfile.put("firstName",firstName);
                    userProfile.put("lastName",lastName);
                    userProfile.put("dateOfBirth",dateOfBirth);
                    userProfile.put("gender",gender);

                    mDatabase.child("UserProfile").child(userid).setValue(userProfile);

                    // Upload Image and Update Image Url

                    uploadImage();

                    // Finish
                    Toast.makeText(FirstComeActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(FirstComeActivity.this,MainActivity.class);
                    startActivity(i);
                } catch (Exception error1) {
                    Toast.makeText(FirstComeActivity.this, "Failure" + error1.getMessage(), Toast.LENGTH_LONG).show();
                    error1.printStackTrace();
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mGetContent.launch("image/*");
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });
    } // onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            avatar.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null){
            StorageReference reference = storage.getReference().child("Image/" + user.getUid());

            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    mAuth = FirebaseAuth.getInstance();
                                    String userId;
                                    if (mAuth != null && mAuth.getCurrentUser() != null)
                                        userId = mAuth.getCurrentUser().getUid();
                                    else
                                        return;

                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("UserProfile");

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("imageUrl", String.valueOf(uri));
                                    mDatabase.child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(FirstComeActivity.this, "Add image successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FirstComeActivity.this, "Add image failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else { // imgUri == null
            Map userProfile = new HashMap();
            userProfile.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/tinder-it-e576d.appspot.com/o/defaultAvatar.png?alt=media&token=a3e00442-9f63-4463-ab6e-31910a768c88");

            DatabaseReference mDatabase_2 = FirebaseDatabase.getInstance().getReference();
            mDatabase_2.child("UserProfile").child(user.getUid()).updateChildren(userProfile);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharePref = getSharedPreferences("DetectUser",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("Detect","newUser");
        editor.apply();
        editor.commit();
    }
}
