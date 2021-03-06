package com.sejong.unknown.view.main;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sejong.unknown.R;
import com.sejong.unknown.base.BaseActivity;
import com.sejong.unknown.databinding.ActivityMainBinding;
import com.sejong.unknown.view.detail.DetailActivity;
import com.sejong.unknown.view.information.InformationActivity;
import com.sejong.unknown.view.main.adapter.CategoryAdapter;
import com.sejong.unknown.view.main.adapter.LostAdapter;
import com.sejong.unknown.view.main.data.MainRepositoryImpl;
import com.sejong.unknown.view.main.entity.CategoryItem;
import com.sejong.unknown.view.main.entity.LostItem;
import com.sejong.unknown.view.market.MarketActivity;
import com.sejong.unknown.view.setting.SettingActivity;

import java.util.ArrayList;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    public static String EXTRA_LOST_ITEM = "EXTRA_LOST_ITEM";

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel = new MainViewModel(new MainRepositoryImpl());

    private int before = 0;

    @Override
    protected void onDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(mainViewModel);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void setupView() {
        initToolbar();
        initRecyclerViewCategory();
        initSwipeRefreshLayout();
        initRecyclerViewLost();
        initBottomNavigationView();
        observeMainViewModel();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.bottomView.setSelectedItemId(R.id.menu_found_item);
    }

    private void initToolbar() {

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(getString(R.string.main_title));
    }

    private void initRecyclerViewCategory() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        CategoryAdapter categoryAdapter = new CategoryAdapter(mainViewModel);
        categoryAdapter.addAll(initCategoryAdapter());

        binding.rcvCategory.setLayoutManager(linearLayoutManager);
        binding.rcvCategory.setAdapter(categoryAdapter);
    }

    private ArrayList<CategoryItem> initCategoryAdapter() {

        ArrayList<CategoryItem> items = new ArrayList<>();
        items.add(CategoryItem.ALL);
        items.add(CategoryItem.BOOK);
        items.add(CategoryItem.ELECTRONICS);
        items.add(CategoryItem.PHONE);
        items.add(CategoryItem.CLOTHING);
        items.add(CategoryItem.WALLET);
        items.add(CategoryItem.ETC);
        return items;
    }

    private void initSwipeRefreshLayout() {

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            mainViewModel.requestLostItems(CategoryItem.fromValue(before).getName());
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initRecyclerViewLost() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration decoration = new DividerItemDecoration(
                binding.rcvLost.getContext(),
                linearLayoutManager.getOrientation()
        );

        LostAdapter lostAdapter = new LostAdapter(mainViewModel);

        binding.rcvLost.setLayoutManager(linearLayoutManager);
        binding.rcvLost.setAdapter(lostAdapter);
        binding.rcvLost.addItemDecoration(decoration);
    }

    private void initBottomNavigationView() {

        binding.bottomView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_found_item:
                    return true;
                case R.id.menu_information:
                    goToInformationActivity();
                    return true;
                case R.id.menu_market:
                    goToMarketActivity();
                    return true;
                case R.id.menu_setting:
                    goToSettingActivity();
                    return true;
            }
            return false;
        });
    }

    private void observeMainViewModel() {

        mainViewModel.categoryItemLiveData.observe(this, categoryItem -> {
            selectCategoryItem(categoryItem);
            mainViewModel.requestLostItems(categoryItem.getName());
        });

        mainViewModel.lostItemsLiveData.observe(this, lostItems -> {
            LostAdapter lostAdapter = ((LostAdapter) binding.rcvLost.getAdapter());
            lostAdapter.clear();
            lostAdapter.addAll(lostItems);
        });

        mainViewModel.lostItemLiveData.observe(this, this::goToDetailActivity);
    }

    private void selectCategoryItem(CategoryItem categoryItem) {

        binding.rcvCategory.getLayoutManager().findViewByPosition(before).setSelected(false);
        before = categoryItem.getValue();
        binding.rcvCategory.getLayoutManager().findViewByPosition(before).setSelected(true);
    }

    private void initView() {
//        mainViewModel.onClickCategoryItem(CategoryItem.ALL);
        mainViewModel.requestLostItems("");
    }

    private void goToInformationActivity() {
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
    }

    private void goToMarketActivity() {
        Intent intent = new Intent(this, MarketActivity.class);
        startActivity(intent);
    }

    private void goToSettingActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void goToDetailActivity(LostItem lostItem) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_LOST_ITEM, lostItem);
        startActivity(intent);
    }
}