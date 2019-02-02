package com.realtimelocation.vdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DaoForJakes {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JakesEntity... jakesEntities);

    @Query("DELETE FROM jakes_data")
    public void nukeTable();

    @Query("SELECT * FROM jakes_data ")
    List<JakesEntity> getJakesData();

}
