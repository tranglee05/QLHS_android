package com.example.quanlyhocsinhmobile.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.quanlyhocsinhmobile.data.local.Model.Lop;

import java.util.List;

@Dao
public interface LopDAO {
    @Query("SELECT * FROM Lop")
    List<Lop> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lop lop);
}
