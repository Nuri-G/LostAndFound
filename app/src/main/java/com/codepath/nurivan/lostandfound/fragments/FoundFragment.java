package com.codepath.nurivan.lostandfound.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nurivan.lostandfound.activities.ItemNameActivity;
import com.codepath.nurivan.lostandfound.databinding.FragmentFoundBinding;
import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.Item;

public class FoundFragment extends Fragment {

    private FragmentFoundBinding binding;

    public FoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFoundBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.bAdd.setOnClickListener(v -> showItemNameActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showItemNameActivity() {
        Item item = new FoundItem();
        Intent i = new Intent(getActivity(), ItemNameActivity.class);
        i.putExtra(Item.class.getSimpleName(), item);

        startActivity(i);
    }
}