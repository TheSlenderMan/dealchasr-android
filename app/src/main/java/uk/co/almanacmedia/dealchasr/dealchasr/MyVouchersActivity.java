package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import uk.co.almanacmedia.dealchasr.dealspotr.R;

import static uk.co.almanacmedia.dealchasr.dealchasr.RecyclerAdapter.PREFS_NAME;

public class MyVouchersActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayAdapter<String> mAdapter;
    private ListView lv;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vouchers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ds_toolbar_v);
        toolbar.setNavigationIcon(R.drawable.hamburger_menu);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_v);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        lv = findViewById(R.id.left_drawer_v);
        addDrawerItems();

        recyclerView = (RecyclerView) findViewById(R.id.cardView_v);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        overridePendingTransition(R.anim.slide_in, R.anim.empty);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final Integer userID = settings.getInt("userID", 0);

        new DoGetMyVouchers(MyVouchersActivity.this, userID, findViewById(android.R.id.content), recyclerView).execute();
    }

    private void addDrawerItems() {
        String[] osArray = { "BACK TO MAP", "MY VOUCHERS", "MY INTERESTS", "HOW TO USE DEALCHASR", "LOG OUT" };
        mAdapter = new ArrayAdapter<String>(this, R.layout.menu_item, osArray);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(MyVouchersActivity.this, MainMapActivity.class);
                    MyVouchersActivity.this.startActivity(intent);
                    ((Activity)MyVouchersActivity.this).finish();
                }
                if(position == 2){
                    Intent intent = new Intent(MyVouchersActivity.this, MyInterestsActivity.class);
                    MyVouchersActivity.this.startActivity(intent);
                    ((Activity)MyVouchersActivity.this).finish();
                }
                if(position == 3){
                    Intent intent = new Intent(MyVouchersActivity.this, WelcomeActivity.class);
                    MyVouchersActivity.this.startActivity(intent);
                    ((Activity)MyVouchersActivity.this).finish();
                }
                if(position == 4){
                    final ProgressDialog PD = new ProgressDialog(MyVouchersActivity.this, R.style.CustomDialog);
                    PD.setMessage("Logging Out...");
                    PD.setCancelable(false);
                    PD.show();

                    SharedPreferences settings = MyVouchersActivity.this.getSharedPreferences(MainMapActivity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.apply();

                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    PD.dismiss();

                                    Intent intent = new Intent(MyVouchersActivity.this, LoginActivity.class);
                                    MyVouchersActivity.this.startActivity(intent);
                                    ((Activity)MyVouchersActivity.this).finish();
                                }
                            },
                            400
                    );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyVouchersActivity.this, MainMapActivity.class);
        MyVouchersActivity.this.startActivity(intent);
        ((Activity)MyVouchersActivity.this).finish();
    }
}
