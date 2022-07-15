package com.codepath.nurivan.lostandfound.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codepath.nurivan.lostandfound.activities.LogInActivity;
import com.codepath.nurivan.lostandfound.activities.SignUpActivity;
import com.codepath.nurivan.lostandfound.databinding.FragmentProfileBinding;
import com.parse.GetCallback;
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

    private String formatEmail(String email) {
        if(email.length() > 18) {
            email = email.substring(0, 15);
            email += "...";
        }
        return email;
    }

    private String formatHomeAddress(String homeAddress) {
        if(homeAddress == null) {
            homeAddress = "";
        }
        String out = homeAddress;
        int comma = homeAddress.indexOf(',');

        if(comma >= 0 && comma < homeAddress.length() - 1) {
            out = homeAddress.substring(0, comma + 1);
            out += '\n';
            out += homeAddress.substring(comma + 1);
        }
        return out;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ParseUser user = ParseUser.getCurrentUser();
        binding.tvUsername.setText(user.getUsername());
        binding.tvEmail.setText(formatEmail(user.getEmail()));
        binding.tvHomeAddress.setText(formatHomeAddress((String) user.get("homeAddress")));
        binding.bLogOut.setOnClickListener(v -> logOutUser());
        binding.bEditProfile.setOnClickListener(v -> showSignUpActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser.getCurrentUser().fetchInBackground((GetCallback<ParseUser>) (user, e) -> {
            if(e != null) {
                Toast.makeText(getContext(), "Failed to fetch user details.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(binding != null) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvEmail.setText(formatEmail(user.getEmail()));
                binding.tvHomeAddress.setText(formatHomeAddress((String) user.get("homeAddress")));
            }
        });
    }

    private void showSignUpActivity() {
        Intent i = new Intent(getContext(), SignUpActivity.class);
        startActivity(i);
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