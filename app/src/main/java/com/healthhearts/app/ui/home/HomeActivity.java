package com.healthhearts.app.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.R;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.ui.contact.ContactsActivity;
import com.healthhearts.app.ui.hospital.HospitalInfoActivity;
import com.healthhearts.app.ui.section.SectionListActivity;
import com.healthhearts.app.ui.spiritual.SpiritualNeedActivity;
import com.healthhearts.app.ui.support.CaregiverSupportActivity;
import com.healthhearts.app.ui.track.TrackYourChildActivity;
import com.healthhearts.app.ui.tutorials.TutorialsChildCareDropdownActivity;
import com.healthhearts.app.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseMenuActivity implements HomeAdapter.OnSectionClickListener {

    public static final int SEC_GENERAL = 1;
    public static final int SEC_TUTORIALS = 2;
    public static final int SEC_SPIRITUAL = 3;
    public static final int SEC_HOSPITAL = 4;
    public static final int SEC_SUPPORT = 5;
    public static final int SEC_ABOUT = 6;

    private final List<HomeSection> sections = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupToolbar();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        sections.clear();
        sections.addAll(buildSections());

        HomeAdapter adapter = new HomeAdapter(sections, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setTitle(getString(R.string.title_home));
        }
    }

    private List<HomeSection> buildSections() {
        List<HomeSection> list = new ArrayList<>();
        list.add(new HomeSection(getString(R.string.home_general_childcare_info), R.drawable.ic_info, R.drawable.tile_bg_general));
        list.add(new HomeSection(getString(R.string.home_childcare_tutorials), R.drawable.ic_tutorials, R.drawable.tile_bg_tutorials));
        list.add(new HomeSection(getString(R.string.home_spiritual_needs), R.drawable.ic_spiritual, R.drawable.tile_bg_spiritual));
        list.add(new HomeSection(getString(R.string.home_hospital_information), R.drawable.ic_hospital, R.drawable.tile_bg_hospital));
        list.add(new HomeSection(getString(R.string.home_caregiver_support), R.drawable.ic_support, R.drawable.tile_bg_support));
        list.add(new HomeSection(getString(R.string.home_about_child_chd), R.drawable.ic_heart, R.drawable.tile_bg_about));
        list.add(new HomeSection(getString(R.string.home_contacts), R.drawable.ic_contacts, R.drawable.tile_bg_contacts));

        if (!Prefs.isAdmin(this)) {
            list.add(new HomeSection(getString(R.string.home_track_child), R.drawable.ic_track, R.drawable.tile_bg_track));
        }

        return list;
    }

    @Override
    public void onSectionClicked(int position) {
        if (position < 0 || position >= sections.size()) return;

        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, SectionListActivity.class);
                intent.putExtra(SectionListActivity.EXTRA_SECTION_ID, SEC_GENERAL);
                break;
            case 1:
                intent = new Intent(this, TutorialsChildCareDropdownActivity.class);
                break;
            case 2:
                intent = new Intent(this, SpiritualNeedActivity.class);
                break;
            case 3:
                intent = new Intent(this, HospitalInfoActivity.class);
                break;
            case 4:
                intent = new Intent(this, CaregiverSupportActivity.class);
                break;
            case 5:
                intent = new Intent(this, SectionListActivity.class);
                intent.putExtra(SectionListActivity.EXTRA_SECTION_ID, SEC_ABOUT);
                break;
            case 6:
                intent = new Intent(this, ContactsActivity.class);
                break;
            case 7:
            default:
                intent = new Intent(this, TrackYourChildActivity.class);
                break;
        }
        startActivity(intent);
    }
}
