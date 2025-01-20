package com.image.ibvtask;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.image.ibvtask.Models.PricesData;
import com.image.ibvtask.ViewModel.IBVViewModel;
import com.image.ibvtask.activities.ChartActivity;
import com.image.ibvtask.activities.LoginActivity;
import com.image.ibvtask.activities.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences s;
    IBVViewModel viewModel;

    SecuredStorage securedStorage;

    public ArrayList<PricesData> mainList = new ArrayList<>();

    ItemsAdapter adapter;
    RecyclerView itemsRecycler;

    String token;

    ChartActivity chartDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        itemsRecycler = (RecyclerView) findViewById(R.id.items_recycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        chartDialog = new ChartActivity();
        chartDialog.setCancelable(false);





        viewModel = new ViewModelProvider(this).get(IBVViewModel.class);

        securedStorage = new SecuredStorage(this);
        token = securedStorage.retrieveData();

        if (token !=null && !token.equals("")) {

            adapter = new ItemsAdapter();
            itemsRecycler.setAdapter(adapter);


            viewModel.getData(token);

            viewModel.getPricesLiveData().observe(this, new Observer<ArrayList<PricesData>>() {
                @Override
                public void onChanged(ArrayList<PricesData> pricesData) {

                    mainList.retainAll(pricesData);
                    //adapter.notifyDataSetChanged();

                    for (PricesData p : pricesData) {

                        if (!mainList.contains(p)) {

                            mainList.add(p);
                            adapter.notifyItemInserted(mainList.size()-1);

                        } else {

                            int index = mainList.indexOf(p);
                            adapter.notifyItemChanged(index);

                        }
                    }

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    private class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {


        @NonNull
        @Override
        public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_item_recycler,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {

            PricesData p = mainList.get(position);

            holder.nameText.setText(p.getName());
            holder.priceText.setText(p.getPrice() + " INR");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView nameText,priceText;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                nameText = (TextView) itemView.findViewById(R.id.nameText);
                priceText = (TextView) itemView.findViewById(R.id.priceText);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_main,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.chart) {

            chartDialog.show(getSupportFragmentManager(),"ChartDialog");

//            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
//            intent.putParcelableArrayListExtra("data",mainList);
//            startActivity(intent);
            return true;


        } else if (itemId == R.id.settings) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.logout) {

            securedStorage.clearToken();
            clearCookies();
//            getSharedPreferences("other_credentials",MODE_PRIVATE)
//                    .edit()
//                    .putBoolean("isDark",false);

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK|FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return false;

    }

    private void clearCookies() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookie();
            CookieManager.getInstance().flush();
            CookieManager.getInstance().removeSessionCookies(null);
            WebStorage.getInstance().deleteAllData();
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }
}