package com.example.tinderforit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


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

                    // Check if user has already existed in another data branch (another gender child)

                    String oppositeGender;

                    if (gender == "Female"){
                        oppositeGender = "Male";
                    }
                    else {
                        oppositeGender = "Female";
                    }

                    mDatabase.child("UserProfile").child(oppositeGender).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            if(task.getResult().child(user.getUid()).exists()){
                                // if exist in another child -> delete
                                mDatabase.child("UserProfile").child(oppositeGender).child(user.getUid()).removeValue();
                            }
                        }
                    });

                    // Upload Object(user) to Firebase

                    UserProfile userProfile = new UserProfile(email, firstName, lastName, dateOfBirth);

                    mDatabase.child("UserProfile").child(gender).child(userid).setValue(userProfile);

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
        // here we need to access the below result code but we can't
        // So to solve it, we will take it as global
        if (imageUri != null){
            StorageReference reference = storage.getReference().child("Image/" + UUID.randomUUID().toString());
            // we are creating a reference to store the image in firebase storage
            // It will be stored inside images folder in firebase storage.
            // You can use user auth id instead of uuid if your app has firebase auth
            // Now using the below code we will store the file

//            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    if (task.isSuccessful()){
//                        // Image uploaded successfully
//                        Toast.makeText(getActivity(), "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

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
                                    mDatabase.child(gender).child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }
}
