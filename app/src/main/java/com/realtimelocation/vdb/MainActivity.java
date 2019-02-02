package com.realtimelocation.vdb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.realtimelocation.vdb.RetroFit.ApiClient;
import com.realtimelocation.vdb.RetroFit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvItem;
    private Context context;
    private ProgressBar pb;
    private AdapterForRecyclerview mAdapter;
    private String URL = "https://api.github.com/users/JakeWharton/";

    private int counter = 1;
    private int conter_ = 0;
    List<JakesEntity> jakesEntityIntial;
    private int listSize = 0;
    private RoomDatabase db;
    private boolean isSaveDataComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        if (new TinyDB(context).getInt("counter") != 0)
            counter = new TinyDB(context).getInt("counter");
        setContentView(R.layout.activity_main);
        db = RoomDatabase.getAppDatabase(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        rvItem = findViewById(R.id.rv_item);
        setSupportActionBar(toolbar);
        pb = findViewById(R.id.progress);
        pb.setVisibility(View.GONE);
        if (new TinyDB(context).getBoolean("dataSaved")) {
            new GetJakesData().execute();
        } else {
            setmAdapter(counter);
        }
        //setmAdapter(counter);

    }


    private void setmAdapter(int count) {
        if (ApiClient.isNetworkAvailable(context)) {
            pb.setVisibility(View.VISIBLE);
            ApiInterface apiService =
                    ApiClient.getJakesData(URL).create(ApiInterface.class);


            Call<List<JakesEntity>> call = apiService.getJakesData(URL + "repos?page=" + String.valueOf(count) + "&per_page=15");

            call.enqueue(new Callback<List<JakesEntity>>() {

                @Override
                public void onResponse(Call<List<JakesEntity>> call, Response<List<JakesEntity>> response) {

                    List<JakesEntity> jakesEntity = (List<JakesEntity>) response.body();
                    pb.setVisibility(View.GONE);
                    if (jakesEntity != null && jakesEntity.size() > 0) {
                        if (counter == 1) {
                            counter = counter + 1;
                            jakesEntityIntial = jakesEntity;
                            new TinyDB(context).putInt("counter", counter);
                            new SaveJakesData(jakesEntityIntial, context).execute();

                        } else {
                            counter = counter + 1;
                            new TinyDB(context).putInt("counter", counter);
                            jakesEntityIntial.addAll(jakesEntity);
                            mAdapter.notifyItemInserted(jakesEntityIntial.size() - 1);
                            if (listSize != 0)
                                rvItem.smoothScrollToPosition(listSize);
                            if (new TinyDB(context).getBoolean("dataSaved")) {
                                new DeleteJakesData(jakesEntityIntial, context).execute();
                            } else
                                new SaveJakesData(jakesEntityIntial, context).execute();
                            return;
                        }


                        if (jakesEntityIntial != null && jakesEntityIntial.size() > 0) {

                            mAdapter = new AdapterForRecyclerview(jakesEntityIntial);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rvItem.setLayoutManager(mLayoutManager);
                            rvItem.setItemAnimator(new DefaultItemAnimator());
                            rvItem.setAdapter(mAdapter);
                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) rvItem.getLayoutParams();
                            marginLayoutParams.setMargins(0, 0, 0, 0);
                            rvItem.setLayoutParams(marginLayoutParams);
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                                @Override
                                public void onBottomReached(int position) {
                                    setmAdapter(counter);
                                    ViewGroup.MarginLayoutParams marginLayoutParams =
                                            (ViewGroup.MarginLayoutParams) rvItem.getLayoutParams();
                                    marginLayoutParams.setMargins(0, 0, 0, 80);
                                    rvItem.setLayoutParams(marginLayoutParams);
                                    listSize = jakesEntityIntial.size();
                                }
                            });
                        }

                    } else {
                        Toast.makeText(context, "No more Data to show", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }


                }

                @Override
                public void onFailure(Call<List<JakesEntity>> call, Throwable t) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to get Data", Toast.LENGTH_SHORT).show();
                }


            });
        } else {

            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public interface OnBottomReachedListener {

        void onBottomReached(int position);

    }


    private class SaveJakesData extends AsyncTask<Void, Void, Void> {


        private RoomDatabase db;
        private List<JakesEntity> jakesEntity;
        private Context context;


        public SaveJakesData(List<JakesEntity> jakesEntity, Context context) {
            super();
            this.jakesEntity = jakesEntity;
            this.context = context;
            db = RoomDatabase.getAppDatabase(context);
            isSaveDataComplete = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (jakesEntity != null && jakesEntity.size() > 0) {
                for (int i = 0; i < jakesEntity.size(); i++) {
                    JakesEntity jakes = new JakesEntity();
                    jakes = jakesEntity.get(i);
                    jakes.setId(i);
                    db.daoForJakes().insert(jakes);
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            RoomDatabase.destroyInstance();
            isSaveDataComplete = true;
            new TinyDB(context).putBoolean("dataSaved", true);
        }
    }

    public class DeleteJakesData extends AsyncTask<Void, Void, Void> {

        private RoomDatabase db;
        private List<JakesEntity> jakesEntities;
        private Context context;

        public DeleteJakesData(List<JakesEntity> jakesEntities, Context context) {
            super();
            this.context = context;
            this.jakesEntities = jakesEntities;
            db = RoomDatabase.getAppDatabase(context);

        }

        @Override
        protected Void doInBackground(Void... voids) {

            db.daoForJakes().nukeTable();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            RoomDatabase.destroyInstance();
            new TinyDB(context).putBoolean("dataSaved", false);
            if (jakesEntities != null) {
                new SaveJakesData(jakesEntities, context).execute();
            }

        }
    }

    private class GetJakesData extends AsyncTask<Void, Void, List<JakesEntity>> {

        String state;
        private RoomDatabase dbRoom;

        public GetJakesData() {
            super();
            dbRoom = RoomDatabase.getAppDatabase(context);

        }

        @Override
        protected List<JakesEntity> doInBackground(Void... voids) {
            List<JakesEntity> jakesEntityList = dbRoom.daoForJakes().getJakesData();
            return jakesEntityList;
        }

        @Override
        protected void onPostExecute(final List<JakesEntity> jakesEntityList) {
            super.onPostExecute(jakesEntityList);
            if (jakesEntityList != null && jakesEntityList.size() > 0) {


                if (jakesEntityList != null && jakesEntityList.size() > 0) {

                    mAdapter = new AdapterForRecyclerview(jakesEntityList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvItem.setLayoutManager(mLayoutManager);
                    rvItem.setItemAnimator(new DefaultItemAnimator());
                    rvItem.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                        @Override
                        public void onBottomReached(int position) {
                            setmAdapter(counter);
                            ViewGroup.MarginLayoutParams marginLayoutParams =
                                    (ViewGroup.MarginLayoutParams) rvItem.getLayoutParams();
                            marginLayoutParams.setMargins(0, 0, 0, 80);
                            rvItem.setLayoutParams(marginLayoutParams);
                            listSize = jakesEntityList.size();
                        }
                    });
                }
            } else {
                setmAdapter(counter);
            }
            RoomDatabase.destroyInstance();
        }
    }
}
