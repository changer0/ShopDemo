package com.lulu.shopdemo;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.lulu.shopdemo.adpaters.CartListAdapter;
import com.lulu.shopdemo.event.CartOperationEvent;
import com.lulu.shopdemo.model.CartItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private Button mButtonEdit;
    private CartListAdapter mAdapter;

    private List<CartItem> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);


        ListView listView = (ListView) findViewById(R.id.cart_list);

        Button btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(this);

        if (listView != null) {
            mItems = new ArrayList<>();
            mAdapter = new CartListAdapter(this, mItems);
            listView.setAdapter(mAdapter);

            for (int i = 0; i < 30; i++) {
                CartItem item = new CartItem();
                item.setCount(1);
                item.setProductId(i);
                item.setProductName("7天精通Android开发" + i);
                item.setProductPrice(30 + i);
                mItems.add(item);
            }
            mAdapter.notifyDataSetChanged();

            //Adapter 可以指定数据观察者 ,每次notifyDataSetChange 观察者自动小勇
            //mAdapter.registerDataSetObserver(this);
            listView.setOnItemClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        for (CartItem item : mItems) {
            item.setChecked(false);
        }
        mAdapter.switchEditMode();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartOperation(CartOperationEvent event) {
        int id = event.id;
        int count = event.item.getCount();
        switch (id) {
            case R.id.item_cart_inc:
                count++;
                break;
            case R.id.item_cart_dec:
                count--;
                if (count > 0) {
                    event.item.setCount(count);
                }
                break;
            case R.id.item_cart_del:
                mItems.remove(event.item);
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击条目, 查看商品详情
        Snackbar.make(parent, "点击条目"  + position, Snackbar.LENGTH_SHORT).show();
    }



}
