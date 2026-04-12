package com.example.quanlyhocsinhmobile.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlyhocsinhmobile.data.local.Model.PhucKhao;

import java.util.List;

@Dao
public interface PhucKhaoDAO {
    @Query("SELECT * FROM PhucKhao")
    List<PhucKhao> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PhucKhao phucKhao);

    @Update
    void update(PhucKhao phucKhao);

    @Delete
    void delete(PhucKhao phucKhao);
}
