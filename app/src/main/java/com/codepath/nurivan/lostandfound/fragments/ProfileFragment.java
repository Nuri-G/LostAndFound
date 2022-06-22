package com.codepath.nurivan.lostandfound.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codepath.nurivan.lostandfound.activities.LogInActivity;
import com.codepath.nurivan.lostandfound.databinding.FragmentProfileBinding;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.bLogOut.setOnClickListener(v -> logOutUser());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void logOutUser() {
        ParseUser.logOut();

        Intent i = new Intent(getActivity(), LogInActivity.class);
        startActivity(i);

        Activity currentActivity = getActivity();
        if(currentActivity != null) {
            currentActivity.finish();
        }

    }

}