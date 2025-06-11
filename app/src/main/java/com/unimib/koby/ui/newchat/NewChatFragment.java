package com.unimib.koby.ui.newchat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.koby.databinding.FragmentNewChatBinding;
import com.unimib.koby.model.Result;

import java.io.InputStream;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    private NewChatViewModel vm;
    private ActivityResultLauncher<Intent> pdfPicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentNewChatBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        vm = new ViewModelProvider(this).get(NewChatViewModel.class);
        pdfPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), res->{
            if(res.getResultCode()== Activity.RESULT_OK && res.getData()!=null){
                Uri uri = res.getData().getData();
                try{
                    InputStream in = requireContext().getContentResolver().openInputStream(uri);
                    vm.createChatFromPdf(in);
                }catch(Exception e){
                    Toast.makeText(requireContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btnPickPdf.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            pdfPicker.launch(intent);
        });

        vm.getResult().observe(getViewLifecycleOwner(), r->{
            if(r instanceof Result.Success){
                Toast.makeText(requireContext(),"Chat creata!",Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }else if(r instanceof Result.Error){
                Toast.makeText(requireContext(),((Result.Error)r).getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}