package com.example.tinderforit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
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
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
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
import com.firebase.ui.auth.AuthUI;

import com.sendbird.android.poll.PollVoteEvent;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private Button btnLogout;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Plumbing controls
        btnSend = (Button) v.findViewById(R.id.sendinfo);
        btnLogout = (Button) v.findViewById(R.id.logout_button);

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

        user = FirebaseAuth.getInstance().getCurrentUser();

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
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

        // Display data
        mDatabase.child("UserProfile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                    // Get User's data from Firebase and Display on screen
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data: ", task.getException());
                            }
                            else {
                                Log.e("firebase", "Connect UserProfile");
                                if(task.getResult().child(user.getUid()).child("imageUrl").exists()) {
                                    try {
                                        Glide.with(getContext()).load(task.getResult().child(user.getUid()).child("imageUrl").getValue().toString()).into(avatar);
                                    } catch (Exception Ex){
                                        Toast.makeText(getContext(), "Lỗi  hiển thị hình ảnh: " + Ex.toString(), Toast.LENGTH_SHORT);
                                    }
                                }
                                if (task.getResult().child(user.getUid()).child("firstName").exists())
                                    LfName.getEditText().setText(String.valueOf(task.getResult().child(user.getUid()).child("firstName").getValue()));
                                if (task.getResult().child(user.getUid()).child("lastName").exists())
                                    LlName.getEditText().setText(String.valueOf(task.getResult().child(user.getUid()).child("lastName").getValue()));
                                if (task.getResult().child(user.getUid()).child("dateOfBirth").exists())
                                    LDOB.getEditText().setText(String.valueOf(task.getResult().child(user.getUid()).child("dateOfBirth").getValue()));
                                if (task.getResult().child(user.getUid()).child("gender").exists())
                                {
                                    if (task.getResult().child(user.getUid()).child("gender").getValue().toString().equals("Female")) {
                                        radFemale.setChecked(true);
                                    } else{
                                        radMale.setChecked(true);
                                    }
                                }

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
                    // Disable button temporarily to prevent runtime error (enable when firebase update successfully)
                    btnSend.setEnabled(false);
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

                    mDatabase.child("UserProfile").child(userid).updateChildren(userProfile);

                    // Upload Image and Update Image Url

                    uploadImage();

                    // Finish
                    Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_LONG).show();

                    // UnFocus
                    TlName.clearFocus();
                    TfName.clearFocus();
                    TDOB.clearFocus();;

                } catch (Exception error1) {
                    Toast.makeText(getActivity(), "Failure" + error1.getMessage(), Toast.LENGTH_LONG).show();
                    error1.printStackTrace();
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mGetContent.launch("image/*");

                showPictureDialog();

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // user is now signed out
                                    Toast.makeText(getContext(), "Current user is logged out", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getActivity(), Choose_Login_And_Reg.class));
                                    getActivity().finish();
                                }
                                else
                                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }

        });
        return v;
    } // onCreate

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
        btnSend.setEnabled(true);
    }



    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallary()
    {
        mGetContent.launch("image/*");
    }

    private void takePhotoFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == -1 && result.getData() != null ){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    // code này để chuyển bitmap thành link Uri
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "val", null );
                    Uri uri = Uri.parse(path);
                    avatar.setImageURI(uri);
                    imageUri = uri;
                }
            }
    );



}
