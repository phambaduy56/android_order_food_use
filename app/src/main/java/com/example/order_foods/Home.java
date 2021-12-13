package com.example.order_foods;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.order_foods.Common.Common;
import com.example.order_foods.Database.Database;
import com.example.order_foods.Interface.ItemClickListencer;
import com.example.order_foods.Model.Banner;
import com.example.order_foods.Model.Category;
import com.example.order_foods.Model.Rating;
import com.example.order_foods.Service.ListenOrder;
import com.example.order_foods.SignInAndSignUp.SingIn_Page;
import com.example.order_foods.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    CounterFab fab;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    HashMap<String, String> image_list;
    SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(Home.this,Cart_page.class);
              startActivity(intent);
            }
        });

        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as t   op level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_menu, R.id.nav_orders, R.id.nav_log_out, R.id.nav_cart)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Set name
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.textViewFullName);
      //  txtFullName.setText(Common.currentUer.getName());

        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));

        if(Common.isConnectedToInternet(this)) {
            LoadMenu();
        } else {
            Toast.makeText(Home.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_menu) {

                } else if (id == R.id.nav_cart) {
                    Intent intentCart = new Intent(Home.this, Cart_page.class);
                    startActivity(intentCart);
                } else if (id == R.id.nav_orders) {
                    Intent intentOrder = new Intent(Home.this, order_status_page.class);

                    startActivity(intentOrder);
                }else if (id == R.id.nav_log_out) {
                    //delete phone and pass
                    Paper.book().destroy();

                    Intent SingIn = new Intent(Home.this, SingIn_Page.class);
                    SingIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(SingIn);
                    } else if (id == R.id.nav_income_statistics) {
                        Intent intent = new Intent(Home.this, InCome.class);
                        startActivity(intent);
                    }
                return false;
            }
        });

        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);

        //setup slider
        setupSlider();
        
    }

    private void setupSlider() {
        mSlider = (SliderLayout) findViewById(R.id.slider);
        image_list = new HashMap<>();

        DatabaseReference banners = database.getReference("Banner");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren()) {
                    Banner banner = postSnapshot.getValue(Banner.class);
                    image_list.put(banner.getName()+"_"+banner.getId(), banner.getImage());
                }

                for(String key:image_list.keySet()) {
                    String[] keySplit = key.split("_");
                    String nameOfFood = keySplit[0];
                    String idOfFood = keySplit[1];

                    TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Home.this, food_detail_page.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });

                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId", idOfFood);

                    mSlider.addSlider(textSliderView);
                    banners.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
        LoadMenu();
    }

    private void LoadMenu() {
        //Query query = ratingTbl.orderByChild("foodId").equalTo(foodId);
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                 holder.txtMenuName.setText(model.getName());
                 Picasso.with(getBaseContext()).load(model.getImages())
                         .into(holder.imageView);
                 Category clickItem = model;
                 holder.setItemClickListencer(new ItemClickListencer() {
                     @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                         Intent intent = new Intent(Home.this, detail_category_page.class);
                         intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                         startActivity(intent);
                     }
                 });
             }

             @NonNull
             @Override
             public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View itemView = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.menu_item, parent,false);
                 return new MenuViewHolder(itemView);
             }
         };
         adapter.startListening();
        recycler_menu.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter !=null)
            adapter.stopListening();
        mSlider.stopAutoCycle();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh) {
            LoadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}