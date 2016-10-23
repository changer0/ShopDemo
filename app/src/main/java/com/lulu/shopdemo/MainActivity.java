package com.lulu.shopdemo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lulu.shopdemo.adpaters.CartListAdapter;
import com.lulu.shopdemo.event.CartOperationEvent;
import com.lulu.shopdemo.model.CartItem;
import com.lulu.shopdemo.pay.OrderInfoUtil2_0;
import com.lulu.shopdemo.pay.PayResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    /** 商户私钥，pkcs8格式 */
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMU25cYJJZ75x43z" +
            "rRpl+ACzPGk0CWe+MHRRQEJ24m2bgzttFpJYhepOfhcJfb9K7Dz9Dz0Fv4qpLKw9" +
            "9rTSij9z4T17uZeP7ySO/wzNAdT8mw8cTPwzrmr+tJySV/SiRM7BRbTK2z/Vuv0G" +
            "sT3EavJLT1xqjQiCWN4YnaFdgxGVAgMBAAECgYAcd5QrRd68V+nKP8ZY7ScjUrwB" +
            "o5VH+FgeMixIa8i7cZs71L9RWJ3b+ICS6rfQCQyYsR6l/FJtvYXJRxh5eXqi9RU3" +
            "WIZb779R9gLmyP+Z5i6eYlrOCyCrcY9S09mnOl0/fAg/ORUkAbOr2Iunx4QMhtdV" +
            "NUgScFKExTF8orXu8QJBAPJjf2ZnwG/Mk1zyiDS/I67tyjNLf7ZoMTbSpIMpF4Nz" +
            "HrU+TKvialdTa4eM794WTDaPvyOkR6VVhBbmwYkwHtsCQQDQSf1cEOF1xvIJ+Y2w" +
            "qMw1TsF5X26wng0fmD5VNGjBm2gjM1/bvetISmiS9tUe1t3bfZVbxBDYjJO1jo6/" +
            "lGRPAkEAiZWQq0AZK1ykCQ5h4g7c4l53d8ZTJ+bciJHob8rTXfnqZFaIjshmNEtV" +
            "rguB3D4r5IyToleNk3uHDrjNAmIeIQJAfm2q4AtPDxJnMC7OFoEfEuxu+6E1qvcE" +
            "uDzM+SMKwxn1qAgzE0rAWezwdORmkIWTvPsJgd4M66TIs9eIRjloeQJBAI/3maBj" +
            "//YeTJbnFP/4AXgQ9s/JRz4SBt6jKBMAsNZUQ8TrKVrsu5sgS6v4YXDEmE2Ql7Lz" +
            "5dpOa5ifefuJTP0=";

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2016102202282242";

    private Button mButtonEdit;
    private Button mButtonPay;
    private Button mSelectAll;
    private boolean isSelectAll;
    private CartListAdapter mAdapter;
    private List<CartItem> mItems;
    private CartDataObserver mCartDataObserver;
    private TextView mTextTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //注册EventBus事件
        EventBus.getDefault().register(this);
        mButtonEdit = (Button) findViewById(R.id.main_btn_edit);
        mButtonPay = (Button) findViewById(R.id.main_btn_pay);
        mSelectAll = (Button) findViewById(R.id.main_select_all);
        mTextTotal = (TextView) findViewById(R.id.main_text_total);
        ListView listView = (ListView) findViewById(R.id.main_cart_list);

        mButtonEdit.setOnClickListener(this);
        mButtonPay.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);

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

            //Adapter 可以指定数据观察者 ,每次notifyDataSetChange 观察者自动调用
            mCartDataObserver = new CartDataObserver();
            mAdapter.registerDataSetObserver(mCartDataObserver);
            mCartDataObserver.onChanged();
            listView.setOnItemClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.main_btn_edit:

                for (CartItem item : mItems) {
                    item.setChecked(false);
                }

                mAdapter.switchEditMode();
                if (mAdapter.isEditMode) {
                    mSelectAll.setVisibility(View.VISIBLE);
                } else {
                    mSelectAll.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.main_btn_pay:
                //点击支付按钮
                startAliPay();
                break;
            case R.id.main_select_all:
                if (!isSelectAll) {
                    //全选按钮
                    for (CartItem item : mItems) {
                        item.setChecked(true);
                    }
                    mAdapter.notifyDataSetChanged();
                    mSelectAll.setText("全不选");
                    isSelectAll = true;
                } else {
                    //全选按钮
                    for (CartItem item : mItems) {
                        item.setChecked(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    mSelectAll.setText("全选");
                    isSelectAll = false;
                }

                break;
        }

    }

    /**
     * 开始支付
     */
    private void startAliPay() {

        if (TextUtils.isEmpty(APPID) || TextUtils.isEmpty(RSA_PRIVATE)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MainActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartOperation(CartOperationEvent event) {
        int id = event.id;
        int count = event.item.getCount();
        switch (id) {
            case R.id.item_cart_inc:
                count++;
                event.item.setCount(count);
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
        EventBus.getDefault().unregister(this);
        mAdapter.unregisterDataSetObserver(mCartDataObserver);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击条目, 查看商品详情
        Snackbar.make(parent, "点击条目"  + position, Snackbar.LENGTH_SHORT).show();
    }


    private class CartDataObserver extends DataSetObserver{
        @Override
        public void onChanged() {
            // TODO: 2016/10/23 计算金额
            float total = 0;
            for (CartItem item :mItems) {
                total = total + (item.getCount() * item.getProductPrice());
            }
            mTextTotal.setText(String.format(Locale.CANADA, "￥ %.2f元", total));
        }
    }


    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                default:
                    break;
            }
        };
    };

}
