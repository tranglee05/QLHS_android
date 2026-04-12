package com.example.quanlyhocsinhmobile.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlyhocsinhmobile.data.local.Model.HocPhi;

import java.util.List;

@Dao
public interface HocPhiDAO {
    @Query("SELECT * FROM HocPhi")
    List<HocPhi> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HocPhi hocPhi);

    @Update
    void update(HocPhi hocPhi);

    @Delete
    void delete(HocPhi hocPhi);
}
