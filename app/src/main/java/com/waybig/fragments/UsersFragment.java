package com.waybig.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.waybig.R;
import com.waybig.adapters.UsersAdapter;
import com.waybig.viewModel.UserViewModel;
import java.util.ArrayList;

public class UsersFragment extends Fragment {
    
    private UserViewModel userViewModel;
    
    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance() {
        return new UsersFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        
        RecyclerView recyclerView = view.findViewById(R.id.users_recyclerview);
        
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        UsersAdapter userAdapter = new UsersAdapter(new ArrayList<>(), requireContext());
        recyclerView.setAdapter(userAdapter);

        userViewModel.getAllUsersLiveData().observe(getViewLifecycleOwner(), userList -> {
            userAdapter.setUsers(userList);
        });
        
        return view;
    }
}
