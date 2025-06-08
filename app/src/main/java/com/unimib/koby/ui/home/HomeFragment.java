package com.unimib.koby.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unimib.koby.R;
import com.unimib.koby.model.SpaceStudy;
import com.unimib.koby.ui.spacestudy.SpaceStudyViewModel;
import com.unimib.koby.ui.spacestudy.SpaceStudyViewModelFactory;
import com.unimib.koby.util.ServiceLocator;

import java.text.DateFormat;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private SpaceAdapter adapter;
    private SpaceStudyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerSpaces);
        emptyView    = view.findViewById(R.id.emptyView);
        MaterialButton addBtn = view.findViewById(R.id.buttonAddSpace);

        adapter = new SpaceAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        addBtn.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_home_to_uploadSpaceFragment));

        viewModel = new ViewModelProvider(
                requireActivity(),
                new SpaceStudyViewModelFactory(ServiceLocator.getInstance().getSpaceStudyRepository())
        ).get(SpaceStudyViewModel.class);

        viewModel.getMySpaces().observe(getViewLifecycleOwner(), this::renderList);
    }

    private void renderList(List<SpaceStudy> spaces) {
        adapter.submitList(spaces);
        boolean isEmpty = spaces == null || spaces.isEmpty();
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /* -------------------- RecyclerView Adapter -------------------------- */
    private static class SpaceAdapter extends ListAdapter<SpaceStudy, SpaceAdapter.VH> {
        SpaceAdapter() { super(DIFF_CALLBACK); }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_space_study_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(getItem(position));
        }

        static class VH extends RecyclerView.ViewHolder {
            private final TextView title, date;
            VH(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.textTitle);
                date  = itemView.findViewById(R.id.textDate);
            }
            void bind(SpaceStudy s) {
                title.setText(s.getTitle());
                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                date.setText(df.format(s.getCreatedAt().toDate()));
            }
        }

        private static final DiffUtil.ItemCallback<SpaceStudy> DIFF_CALLBACK =
                new DiffUtil.ItemCallback<SpaceStudy>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull SpaceStudy oldItem, @NonNull SpaceStudy newItem) {
                        return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                    }
                    @Override
                    public boolean areContentsTheSame(@NonNull SpaceStudy oldItem, @NonNull SpaceStudy newItem) {
                        return oldItem.equals(newItem);
                    }
                };
    }
}