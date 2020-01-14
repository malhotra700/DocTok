package com.pranks.trialsih.doctorActivities.navigationDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.pranks.trialsih.R;
import com.pranks.trialsih.doctorActivities.DoctorDashboardActivity;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        v = inflater.inflate(R.layout.fragment_share, container, false);

        ((DoctorDashboardActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        final TextView textView = v.findViewById(R.id.text_share);
        settingsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return v;
    }
}