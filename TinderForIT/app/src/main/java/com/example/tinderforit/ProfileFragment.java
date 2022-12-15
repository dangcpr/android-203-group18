package com.example.tinderforit;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

///////////////////////////////////////////////////////////////////////////
///////////////////////////////// UNNECESSARY /////////////////////////////
///////////////////////////////////////////////////////////////////////////
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
///////////////////////////////////////////////////////////////////////////
///////////////////////////////// UNNECESSARY /////////////////////////////
///////////////////////////////////////////////////////////////////////////

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Plumbing controls

        btnSend = (Button) v.findViewById(R.id.sendinfo);

        TfName = (TextInputEditText) v.findViewById(R.id.textFirstName);
        LfName = (TextInputLayout) v.findViewById(R.id.layouttextFirstName);

        TlName = (TextInputEditText) v.findViewById(R.id.textLastName);
        LlName = (TextInputLayout)v.findViewById(R.id.layouttextLastName);

        LDOB = (TextInputLayout) v.findViewById(R.id.layouttextDOB);
        TDOB = (TextInputEditText) v.findViewById(R.id.textDOB);

        radFemale = (RadioButton) v.findViewById(R.id.radio_female);
        radMale = (RadioButton) v.findViewById(R.id.radio_male);

        avatar = v.findViewById(R.id.avatar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        // Check if user has choose gender or not

        // check if user is Female
        mDatabase.child("UserProfile").child("Female").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.getResult().child(user.getUid()).exists()){
                    // if not exist: this user is first time login -> let them input profile and click "Send"
                }
                else { // if exist: get their data from Firebase and display

                    // Get User's data from Firebase and Display on screen
                    mDatabase.child("UserProfile").child("Female").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data: ", task.getException());
                            }
                            else {
                                //Log.d("firebase", String.valueOf(task.getResult().child("imageUrl").getValue()));
                                if(task.getResult().child("imageUrl").exists()) {
                                    try {
                                        Glide.with(getContext()).load(task.getResult().child("imageUrl").getValue().toString()).placeholder(R.drawable.noimage).into(avatar);
                                    } catch (Exception Ex){
                                        Toast.makeText(getContext(), "Lỗi hiển thị hình ảnh: " + Ex.toString(), Toast.LENGTH_SHORT);
                                    }
                                }
                                if (task.getResult().child("firstName").exists())
                                    LfName.getEditText().setText(String.valueOf(task.getResult().child("firstName").getValue()));
                                if (task.getResult().child("lastName").exists())
                                    LlName.getEditText().setText(String.valueOf(task.getResult().child("lastName").getValue()));
                                if (task.getResult().child("dateOfBirth").exists())
                                    LDOB.getEditText().setText(String.valueOf(task.getResult().child("dateOfBirth").getValue()));
                                //avatar.setImageURI(Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue())));
                                radFemale.setChecked(true);
                            }
                        }
                    });
                }
            }

        });

        // check if user is Male
        mDatabase.child("UserProfile").child("Male").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.getResult().child(user.getUid()).exists()){
                    // if not exist: this user is first time login -> let them input profile and click "Send"
                }
                else { // if exist: get their data from Firebase and display

                    // Get User's data from Firebase and Display on screen
                    mDatabase.child("UserProfile").child("Male").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data: ", task.getException());
                            }
                            else {
                                //Log.d("firebase", String.valueOf(task.getResult().child("imageUrl").getValue()));
                                if(task.getResult().child("imageUrl").exists()) {
                                    try {
                                        Glide.with(getContext()).load(task.getResult().child("imageUrl").getValue().toString()).placeholder(R.drawable.noimage).into(avatar);
                                    } catch (Exception Ex){
                                        Toast.makeText(getContext(), "Lỗi hiển thị hình ảnh: " + Ex.toString(), Toast.LENGTH_SHORT);
                                    }
                                }
                                if (task.getResult().child("firstName").exists())
                                    LfName.getEditText().setText(String.valueOf(task.getResult().child("firstName").getValue()));
                                if (task.getResult().child("lastName").exists())
                                    LlName.getEditText().setText(String.valueOf(task.getResult().child("lastName").getValue()));
                                if (task.getResult().child("dateOfBirth").exists())
                                    LDOB.getEditText().setText(String.valueOf(task.getResult().child("dateOfBirth").getValue()));
                                //avatar.setImageURI(Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue())));
                                radMale.setChecked(true);
                            }
                        }
                    });
                }
            }
        });

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
                        getActivity(),
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
                    Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_LONG).show();
                } catch (Exception error1) {
                    Toast.makeText(getActivity(), "Failure" + error1.getMessage(), Toast.LENGTH_LONG).show();
                    error1.printStackTrace();
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");

            }
        });
        return v;
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
                                    Toast.makeText(getActivity(), "Add image successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Add image failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    // this result is the result of uri
                    if (result != null){
                        avatar.setImageURI(result);
                        // result will be set in imageUri
                        imageUri = result;
                    }
                }
    });

}

@IgnoreExtraProperties
class UserProfile {

    public String userid;
    public String email;
    public String firstName;
    public String lastName;
    public String dateOfBirth;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserProfile(String email, String firstName, String lastName, String dateOfBirth) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }
}