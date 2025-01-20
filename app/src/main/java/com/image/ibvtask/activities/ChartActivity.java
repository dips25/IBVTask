package com.image.ibvtask.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.image.ibvtask.MainActivity;
import com.image.ibvtask.Models.PricesData;
import com.image.ibvtask.MyGradient;
import com.image.ibvtask.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.function.Function;

public class ChartActivity extends DialogFragment {

    ProgressBar chartProgress;
    ArrayList<PricesData> dataArrayList;

    LayerDrawable chartDrawable;

    long highestPrice = 100000;

    LinearLayout indexHolder;
    TextView close;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.sample_progress,null);



        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setCancelable(false)
                ;

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);





        indexHolder = (LinearLayout) view.findViewById(R.id.index_holder);
        close = (TextView) view.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });




//

        dataArrayList = ((MainActivity) requireActivity()).mainList;

        chartProgress = (ProgressBar) view.findViewById(R.id.chart_progress);

        chartDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.chart_layer_background);

        prepareChartData();
        return dialog;


    }

    //    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sample_progress);
//
//        indexHolder = (LinearLayout) findViewById(R.id.index_holder);
//
////        for (int i = 0 ; i<3 ; i++) {
////
////
////
////
////        }
//
//        dataArrayList = getIntent().getParcelableArrayListExtra("data");
//
//        chartProgress = (ProgressBar) findViewById(R.id.chart_progress);
//
//        chartDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.chart_layer_background);
//
//        prepareChartData();
//    }

    int price;

    private void prepareChartData() {

        int prevValue = 0;
        int value = 0;

        TreeSet<PricesData> treeSet = new TreeSet<>(new MyComparator());

        TypedArray t = getResources().obtainTypedArray(R.array.chart_colors);

        LayerDrawable progressDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.progress_drawable);
        Drawable.ConstantState constantState = progressDrawable.getConstantState();

        ListIterator<PricesData> pIterator = dataArrayList.listIterator();

        while (pIterator.hasNext()) {

            PricesData p = pIterator.next();
            if (p.getPrice()>10000) {

                p.setPrice(p.getPrice()/10);

            }
//            else if (p.getPrice()<1000) {
//
//                p.setPrice(p.getPrice()+1000);
//            }

            treeSet.add(p);

            price+=p.getPrice();

        }

        ArrayList<PricesData> chartData = new ArrayList<>(treeSet);
        //Collections.reverse(chartData);
        Drawable[] drawables = new Drawable[chartData.size()];
        Log.d(ChartActivity.class.getName(), "prepareChartData: "+chartData);

        for (int i=0 ; i<chartData.size() ; i++) {

            if (i == 0) {

                GradientDrawable gd1 = (GradientDrawable) ((LayerDrawable) constantState.newDrawable()).findDrawableByLayerId(R.id.progress_drawable);
                gd1.setColor(t.getColor(0, 0));
                gd1.setLevel(10000);

                drawables[i] = gd1;

                View view = getLayoutInflater().inflate(R.layout.single_item_index,null,false);
                LinearLayout indexColor = view.findViewById(R.id.index_color);
                TextView indexText = view.findViewById(R.id.index_text);

                indexColor.setBackgroundColor(t.getColor(i,0));
                indexText.setText(chartData.get(i).getName());


                indexHolder.addView(view);

            } else {

                if (prevValue!=0) {

                    int currentValue = chartData.get(i).getPrice();
                    if (prevValue-currentValue>=250) {

                        currentValue-=300;
                        value = currentValue;

                    } else {

                        value = chartData.get(i).getPrice();
                    }


                } else {

                    value = chartData.get(i).getPrice();
                }

                GradientDrawable gd1 = (GradientDrawable) ((LayerDrawable) constantState.newDrawable()).findDrawableByLayerId(R.id.progress_drawable);
                gd1.setColor(t.getColor(i, 0));
                gd1.setLevel(value);

                drawables[i] = gd1;

                prevValue = value;

                View view = getLayoutInflater().inflate(R.layout.single_item_index,null,false);

                LinearLayout indexColor = view.findViewById(R.id.index_color);
                TextView indexText = view.findViewById(R.id.index_text);

                indexColor.setBackgroundColor(t.getColor(i,0));
                indexText.setText(chartData.get(i).getName());


                indexHolder.addView(view);

            }

        }

        LayerDrawable l = new LayerDrawable(drawables);
        chartProgress.setProgressDrawable(l);

    }

    private class MyComparator implements Comparator {


        @Override
        public int compare(Object o1, Object o2) {

            Integer i1 = ((PricesData) o1).getPrice();
            Integer i2 = ((PricesData) o2).getPrice();

            return i2.compareTo(i1);
        }
    }


}
