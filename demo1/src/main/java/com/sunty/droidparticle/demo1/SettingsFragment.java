package com.sunty.droidparticle.demo1;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunty on 16-6-5.
 */
public class SettingsFragment extends Fragment {


    public interface OnConfigUpdateListener {
        void onConfigUpdate(String jsonConfig);
        void onOtherUpdate(String key, int value);
    }

    static class Item {

        public String name;
        public float baseMin;
        public float baseMax;
        public float biasMin;
        public float biasMax;
        public String fmt;

        public Item(float baseMax, float baseMin, float biasMax, float biasMin, String fmt, String name) {
            this.baseMax = baseMax;
            this.baseMin = baseMin;
            this.biasMax = biasMax;
            this.biasMin = biasMin;
            this.name = name;
            this.fmt = fmt;
        }
    }

    class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        final ArrayList<Item> mList = new ArrayList<>();

        public ItemAdapter() {
            mList.add(new Item(15, 0, 0, 0, "%d", "image"));
            mList.add(new Item(1, 0, 0, 0, "%d", "blend"));
            mList.add(new Item(60, 1, 0, 0, "%d", "fps"));
            mList.add(new Item(200, 1, 0, 0, "%d", "pps"));
            mList.add(new Item(3000, 0, 3000, 0, "%.0f", "duration"));
            mList.add(new Item(360, 0, 360, 0, "%.1f", "theta"));
            mList.add(new Item(360, 0, 360, 0, "%.1f", "rotation"));
            mList.add(new Item(2000, 0, 2000, 0, "%.1f", "start" + "Velocity"));
            mList.add(new Item(2000, 0, 2000, 0, "%.1f", "end" + "Velocity"));
            mList.add(new Item(1080, 0, 1080, 0, "%.1f", "start" + "AngularRate"));
            mList.add(new Item(1080, 0, 1080, 0, "%.1f", "end" + "AngularRate"));
            mList.add(new Item(1080, 0, 1080, 0, "%.1f", "start" + "SpinRate"));
            mList.add(new Item(1080, 0, 1080, 0, "%.1f", "end" + "SpinRate"));
            mList.add(new Item(10, 0, 10, 0, "%.2f", "start" + "Scale"));
            mList.add(new Item(10, 0, 10, 0, "%.2f", "end" + "Scale"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "start" + "Alpha"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "end" + "Alpha"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "start" + "Red"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "end" + "Red"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "start" + "Green"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "end" + "Green"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "start" + "Blue"));
            mList.add(new Item(1, 0, 1, 0, "%.3f", "end" + "Blue"));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new VH0(LayoutInflater.from(getActivity()).inflate(
                            R.layout.layout_settings_item, parent, false));
                case 1:
                    return new VH1(LayoutInflater.from(getActivity()).inflate(
                            R.layout.layout_settings_item_image, parent, false));

                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (holder instanceof VH0) {
                bindVH0((VH0)holder, position);
            } else if (holder instanceof VH1) {
                bindVH1((VH1)holder, position);
            }

        }

        private void bindVH0(VH0 holder, int position) {
            final Item item = mList.get(position);
            holder.setItem(item);
        }
        private void bindVH1(VH1 holder, int position) {
            final Item item = mList.get(position);
            holder.setItem(item);
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 4) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class VH0 extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvBaseValue;
        SeekBar barBaseValue;
        TextView tvBiasValue;
        SeekBar barBiasValue;

        Item mItem;

        public VH0(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvBaseValue = (TextView) itemView.findViewById(R.id.tv_item_base_value);
            barBaseValue = (SeekBar) itemView.findViewById(R.id.bar_item_base_value);
            tvBiasValue = (TextView) itemView.findViewById(R.id.tv_item_bias_value);
            barBiasValue = (SeekBar) itemView.findViewById(R.id.bar_item_bias_value);
        }

        public void setItem(Item item) {
            mItem = item;

            tvName.setText(mItem.name);
            barBaseValue.setMax(10000);
            barBaseValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        double r = (double) progress / 10000.0;
                        double value = mItem.baseMin + (mItem.baseMax - mItem.baseMin) * r;
                        String str = String.format(mItem.fmt, value);
                        tvBaseValue.setText(str);
                        Map range = (Map) mConfig.get(mItem.name);
                        range.put("base", value);
                        if (mOnConfigUpdateListener != null) {
                            String jsonConfig = new Gson().toJson(mConfig);
                            mOnConfigUpdateListener.onConfigUpdate(jsonConfig);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            barBiasValue.setMax(10000);
            barBiasValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        double r = (double) progress / 10000.0;
                        double value = mItem.biasMin + (mItem.biasMax - mItem.biasMin) * r;
                        String str = String.format(mItem.fmt, value);
                        tvBiasValue.setText(str);
                        Map range = (Map) mConfig.get(mItem.name);
                        range.put("bias", value);
                        if (mOnConfigUpdateListener != null) {
                            String jsonConfig = new Gson().toJson(mConfig);
                            mOnConfigUpdateListener.onConfigUpdate(jsonConfig);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            Map range = (Map)mConfig.get(item.name);
            Double dbase = (Double)range.get("base");
            Double dbias = (Double)range.get("bias");

            float base = dbase.floatValue();
            float bias = dbias.floatValue();

            float baseProgress = 10000.0f * (base - item.baseMin) / (item.baseMax - item.baseMin);
            barBaseValue.setProgress(Math.round(baseProgress));
            float biasProgress = 10000.0f * (bias - item.biasMin) / (item.biasMax - item.biasMin);
            barBiasValue.setProgress(Math.round(biasProgress));

            tvBaseValue.setText(String.format(mItem.fmt, base));
            tvBiasValue.setText(String.format(mItem.fmt, bias));
        }
    }
    class VH1 extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvBaseValue;
        SeekBar barBaseValue;

        Item mItem;

        public VH1(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvBaseValue = (TextView) itemView.findViewById(R.id.tv_item_base_value);
            barBaseValue = (SeekBar) itemView.findViewById(R.id.bar_item_base_value);
        }

        public void setItem(Item item) {
            mItem = item;
            tvName.setText(mItem.name);
            barBaseValue.setMax(10000);
            barBaseValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        float r = (float) progress / 10000.0f;
                        float value = mItem.baseMin + (mItem.baseMax - mItem.baseMin) * r;
                        int ivalue = Math.round(value);
                        String str = String.format(mItem.fmt, ivalue);
                        tvBaseValue.setText(str);
                        mOtherConfig.put(mItem.name, ivalue);
                        if (mOnConfigUpdateListener != null) {
                            mOnConfigUpdateListener.onOtherUpdate(mItem.name, (int) ivalue);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    float r = (float) seekBar.getProgress() / 10000.0f;
                    float value = mItem.baseMin + (mItem.baseMax - mItem.baseMin) * r;
                    int ivalue = Math.round(value);
                    float baseProgress = 10000.0f * (ivalue - mItem.baseMin) / (mItem.baseMax - mItem.baseMin);
                    seekBar.setProgress(Math.round(baseProgress));
                    String str = String.format(mItem.fmt, ivalue);
                    tvBaseValue.setText(str);
                }
            });
            int value = (Integer)mOtherConfig.get(item.name);
            float baseProgress = 10000.0f * (value - item.baseMin) / (item.baseMax - item.baseMin);
            int ivalue = Math.round(baseProgress);
            barBaseValue.setProgress(ivalue);
            String str = String.format(mItem.fmt, value);
            tvBaseValue.setText(str);
        }
    }

    Map mConfig;
    Map mOtherConfig;
    ItemAdapter mAdapter;
    RecyclerView mListView;
    OnConfigUpdateListener mOnConfigUpdateListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnConfigUpdateListener) {
            mOnConfigUpdateListener = (OnConfigUpdateListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String jsonConfig = getArguments().getString("config");
        mConfig = new Gson().fromJson(jsonConfig, Map.class);
        mOtherConfig = new HashMap();
        for (String key : getArguments().keySet()) {
            if (!key.equals("config")) {
                mOtherConfig.put(key, getArguments().getInt(key));
            }
        }

        View view = inflater.inflate(R.layout.layout_settings, null);

        mListView = (RecyclerView) view.findViewById(R.id.list_items);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ItemAdapter();
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnConfigUpdateListener = null;
    }
}
