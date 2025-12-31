package com.healthhearts.app.ui.tutorials;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.model.ContentItem;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.ui.section.ContentDetailActivity;
import com.healthhearts.app.ui.section.ContentEditActivity;
import com.healthhearts.app.util.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TutorialsChildCareDropdownActivity extends BaseMenuActivity {
    private static final int SEC_DRAIN = 201;
    private static final int SEC_WOUND = 202;
    private static final int SEC_MEDS = 203;
    private static final int SEC_FORM = 204;

    private boolean isAdmin;

    private TutorialsContentAdapter adDrain, adWound, adMeds, adForm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials_childcare_dropdown);

        isAdmin = Prefs.isAdmin(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.tutorials_child_care));

        adDrain = new TutorialsContentAdapter(this::openDetail, item -> showAdminActions(SEC_DRAIN, item));
        adWound = new TutorialsContentAdapter(this::openDetail, item -> showAdminActions(SEC_WOUND, item));
        adMeds = new TutorialsContentAdapter(this::openDetail, item -> showAdminActions(SEC_MEDS, item));
        adForm = new TutorialsContentAdapter(this::openDetail, item -> showAdminActions(SEC_FORM, item));

        setupList(R.id.rvDrain, adDrain);
        setupList(R.id.rvWound, adWound);
        setupList(R.id.rvMeds, adMeds);
        setupList(R.id.rvFormula, adForm);

        hookDropdown(R.id.headerDrain, R.id.rvDrain, R.id.ivDrainChevron, R.id.tvEmptyDrain);
        hookDropdown(R.id.headerWound, R.id.rvWound, R.id.ivWoundChevron, R.id.tvEmptyWound);
        hookDropdown(R.id.headerMeds, R.id.rvMeds, R.id.ivMedsChevron, R.id.tvEmptyMeds);
        hookDropdown(R.id.headerFormula, R.id.rvFormula, R.id.ivFormulaChevron, R.id.tvEmptyFormula);

        View btnDrain = findViewById(R.id.btnAddDrain);
        View btnWound = findViewById(R.id.btnAddWound);
        View btnMeds = findViewById(R.id.btnAddMeds);
        View btnForm = findViewById(R.id.btnAddFormula);

        btnDrain.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnWound.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnMeds.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnForm.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        btnDrain.setOnClickListener(v -> openAdd(SEC_DRAIN));
        btnWound.setOnClickListener(v -> openAdd(SEC_WOUND));
        btnMeds.setOnClickListener(v -> openAdd(SEC_MEDS));
        btnForm.setOnClickListener(v -> openAdd(SEC_FORM));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCategory(SEC_DRAIN, adDrain, R.id.tvEmptyDrain);
        loadCategory(SEC_WOUND, adWound, R.id.tvEmptyWound);
        loadCategory(SEC_MEDS, adMeds, R.id.tvEmptyMeds);
        loadCategory(SEC_FORM, adForm, R.id.tvEmptyFormula);
    }

    private void setupList(int rvId, TutorialsContentAdapter adapter) {
        androidx.recyclerview.widget.RecyclerView rv = findViewById(rvId);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void loadCategory(int sectionId, TutorialsContentAdapter adapter, int emptyTvId) {
        View emptyTv = findViewById(emptyTvId);

        FirestoreRepo.content()
                .whereEqualTo("sectionId", sectionId)
                .get()
                .addOnSuccessListener(snap -> {
                    List<ContentItem> out = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        ContentItem it = d.toObject(ContentItem.class);
                        if (it != null) {
                            it.id = d.getId();
                            out.add(it);
                        }
                    }
                    Collections.sort(out, (a, b) -> Long.compare(b.updatedAt, a.updatedAt));
                    adapter.setItems(out);
                    emptyTv.setVisibility(out.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    emptyTv.setVisibility(View.VISIBLE);
                });
    }

    private void hookDropdown(int headerId, int listId, int chevronId, int emptyId) {
        View header = findViewById(headerId);
        View list = findViewById(listId);
        View chevron = findViewById(chevronId);
        View empty = findViewById(emptyId);

        header.setOnClickListener(v -> {
            boolean open = list.getVisibility() == View.VISIBLE;
            list.setVisibility(open ? View.GONE : View.VISIBLE);
            empty.setVisibility(open ? View.GONE : empty.getVisibility());
            chevron.setRotation(open ? 0f : 90f);
        });
    }

    private void openDetail(String contentId) {
        Intent i = new Intent(this, ContentDetailActivity.class);
        i.putExtra(ContentDetailActivity.EXTRA_CONTENT_ID, contentId);
        startActivity(i);
    }

    private void openAdd(int sectionId) {
        Intent i = new Intent(this, ContentEditActivity.class);
        i.putExtra(ContentEditActivity.EXTRA_SECTION_ID, sectionId);
        startActivity(i);
    }

    private void showAdminActions(int sectionId, ContentItem item) {
        if (!isAdmin || item == null) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_actions)
                .setItems(new CharSequence[]{getString(R.string.edit), getString(R.string.delete)}, (d, which) -> {
                    if (which == 0) {
                        Intent i = new Intent(this, ContentEditActivity.class);
                        i.putExtra(ContentEditActivity.EXTRA_SECTION_ID, sectionId);
                        i.putExtra(ContentEditActivity.EXTRA_CONTENT_ID, item.id);
                        startActivity(i);
                    } else {
                        confirmDelete(item.id);
                    }
                })
                .show();
    }

    private void confirmDelete(String id) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveButton(R.string.delete, (d, w) -> doDelete(id))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void doDelete(String id) {
        FirestoreRepo.content().document(id).delete()
                .addOnSuccessListener(v -> onStart())
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
