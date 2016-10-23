package com.lulu.shopdemo.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lulu.shopdemo.R;
import com.lulu.shopdemo.event.CartOperationEvent;
import com.lulu.shopdemo.model.CartItem;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;

/**
 * Created by Lulu on 2016/10/22.
 */

public class CartListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CartItem> mItems;
    private boolean isEditMode;


    public CartListAdapter(Context context, List<CartItem> items) {
        mContext = context;
        mItems = items;
    }

    public void switchEditMode() {
        isEditMode = !isEditMode;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (mItems != null) {
            ret = mItems.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getProductId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;
        if (convertView != null) {
            ret = convertView;
        } else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ret = inflater.inflate(R.layout.item_cart, parent, false);
        }
        ViewHolder holder = (ViewHolder) ret.getTag();
        if (holder == null) {
            holder = new ViewHolder(ret);
            ret.setTag(holder);
        }

        CartItem cartItem = mItems.get(position);
        holder.setCartItem(cartItem);
        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        holder.mTextName.setText(cartItem.getProductName());
        holder.mTextPrice.setText(String.format(Locale.CANADA, "￥ %.2f", cartItem.getProductPrice()));
        int count = cartItem.getCount();
        holder.mTextCount.setText(String.valueOf(count));

        holder.mCheckBox.setChecked(cartItem.isChecked());

        //如果Adapter中, 根据状态一个控件显示不同状态, 那么条件必须要判断全 true/false


        holder.mButtonDec.setClickable(count != 1);


        if (isEditMode) {
            //Edit
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mButtonDel.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
            holder.mButtonDel.setVisibility(View.INVISIBLE);
        }


        return ret;
    }


    private static class ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private CheckBox mCheckBox;
        private ImageView mImageView;
        private TextView mTextName;
        private TextView mTextPrice;

        private Button mButtonDec;//-
        private Button mButtonInc;//+
        private TextView mTextCount;//count

        private Button mButtonDel;

        private CartItem mCartItem;

        public ViewHolder(View itemView) {
            mCheckBox = ((CheckBox) itemView.findViewById(R.id.item_cart_check));
            mImageView = (ImageView) itemView.findViewById(R.id.item_cart_icon);
            mTextName = (TextView) itemView.findViewById(R.id.item_cart_name);
            mTextPrice = (TextView) itemView.findViewById(R.id.item_cart_price);
            mButtonDec = (Button) itemView.findViewById(R.id.item_cart_dec);
            mButtonInc = (Button) itemView.findViewById(R.id.item_cart_inc);
            mTextCount = (TextView) itemView.findViewById(R.id.item_cart_count);
            mButtonDel = (Button) itemView.findViewById(R.id.item_cart_del);

            mCheckBox.setOnCheckedChangeListener(this);
            mButtonInc.setOnClickListener(this);
        }

        public void setCartItem(CartItem cartItem) {
            mCartItem = cartItem;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCheckBox.setChecked(mCartItem.isChecked());
        }

        @Override
        public void onClick(View v) {
            int count = mCartItem.getCount();
            int id = v.getId();
            CartOperationEvent event = new CartOperationEvent();
            event.id = id;
            event.item = mCartItem;

            EventBus.getDefault().post(event);

        }
    }

}















