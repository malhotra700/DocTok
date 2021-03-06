package com.pranks.trialsih.patientActivities.ui.activeAppoint;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranks.trialsih.R;
import com.pranks.trialsih.patientActivities.AddAppointment;
import com.pranks.trialsih.patientActivities.PatientDashboardActivity;
import com.pranks.trialsih.patientAdapters.PatientAppointmentsAdapter;
import com.pranks.trialsih.patientsClasses.PatientAppointments;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View v;

    private ImageView cannotFind;
    private TextView cannotFindText;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private List<PatientAppointments> list;
    private PatientAppointmentsAdapter patientAppointmentsAdapter;

    private FloatingActionButton floatingActionButton;

    private static String UID;
    private final static String USER_TYPE = "patients";
    private final static String ACTIVE_APPOINTMENTS = "activeAppointments";
    private final static String PREVIOUS_APPOINTMENTS = "prevAppointments";
    private final static String PREVIOUS_TRANSACTIONS = "prevTransactions";
    private final static String PROFILE = "profile";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.fragment_home, container, false);

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Home");


        list = new ArrayList<>();

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgressCircle = v.findViewById(R.id.progress_circle);
        cannotFind = v.findViewById(R.id.cannotfind);
        cannotFindText = v.findViewById(R.id.cannotfind_text);

        floatingActionButton = v.findViewById(R.id.add_appointments);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Sure to add appointments?");
                builder.setCancelable(true);

                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        isProfileComplete();

                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                try{alertDialog.show();}catch (Exception e) {alertDialog.dismiss();}
            }
        });

        loadAppointments();

        return v;
    }

    private boolean isProfileComplete()
    {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final boolean isComplete = false;

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(UID).child(PROFILE);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.getChildrenCount() > 3)
                    {

                        // first ask for type of doctor looking for...
                        showTypeOfDoctorDialog();
                        dialog.cancel();

                    }
                    else
                    {
                        dialog.cancel();
                        Snackbar.make(getView(), "Complete your profile first", Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    dialog.cancel();
                    Snackbar.make(getView(), "Complete your profile first", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

        return isComplete;
    }




    private void showTypeOfDoctorDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose which type of doctor you are looking for");
        builder.setCancelable(false);

        String[] doctorTypes = new String[] {"General Practitioner", "Family Physician", "Internal Medicine Physician", "Pediatrician", "Obstetrician/Gynecologist (OB/GYN)", "Surgeon", "Psychiatrist", "Cardiologist", "Dermatologist", "Endocrinologist", "Infectious Disease Physician", "Nephrologist", "Ophthalmologist", "Otolaryngologist", "Pulmonologist", "Neurologist", "Physician Executive", "Radiologist", "Anesthesiologist", "Oncologist", "Gastroenterologist", "Orthopedist"};



        builder.setSingleChoiceItems(doctorTypes, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Bundle bundle = new Bundle();
                bundle.putString("doctorType", doctorTypes[which]);

                Intent intent = new Intent(getContext(), AddAppointment.class);

                intent.putExtras(bundle);

                startActivity(intent);

                dialog.cancel();

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Bundle bundle = new Bundle();
                bundle.putString("doctorType", doctorTypes[0]);

                Intent intent = new Intent(getContext(), AddAppointment.class);

                intent.putExtras(bundle);

                startActivity(intent);

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getContext(), "Operation cancelled by the user", Toast.LENGTH_SHORT).show();

                dialog.cancel();
            }
        });

        builder.show();


    }



    private void loadAppointments()
    {

        list = new ArrayList<>();
        patientAppointmentsAdapter = new PatientAppointmentsAdapter(getContext(), list);

        mRecyclerView.setAdapter(null);

        list.clear();

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(USER_TYPE).child(UID).child(ACTIVE_APPOINTMENTS);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    mRecyclerView.setAdapter(null);

                    cannotFind.setVisibility(View.VISIBLE);
                    cannotFindText.setVisibility(View.VISIBLE);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
                else
                {

                    mRecyclerView.setAdapter(null);
                    list.clear();
                    patientAppointmentsAdapter = null;

                    ArrayList<String> listOfRegNumber = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        listOfRegNumber.add(snapshot.getKey());

                    }

                    for (String regNumber : listOfRegNumber)
                    {
                        list.add(dataSnapshot.child(regNumber).getValue(PatientAppointments.class));
                    }

                    patientAppointmentsAdapter = new PatientAppointmentsAdapter(getContext(), list);

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mRecyclerView.setAdapter(patientAppointmentsAdapter);

                    cannotFind.setVisibility(View.INVISIBLE);
                    mProgressCircle.setVisibility(View.GONE);
                    cannotFindText.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();

        ((PatientDashboardActivity) getActivity()).getSupportActionBar().setTitle("Home");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

    }

    @Override
    public void onResume() {
        super.onResume();

        loadAppointments();

    }
}