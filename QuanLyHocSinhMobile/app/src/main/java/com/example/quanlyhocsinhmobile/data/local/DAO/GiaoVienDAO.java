package com.example.quanlyhocsinhmobile.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlyhocsinhmobile.data.local.Model.GiaoVien;

import java.util.List;

@Dao
public interface GiaoVienDAO {
    @Query("SELECT * FROM GiaoVien")
    List<GiaoVien> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GiaoVien giaoVien);

    @Update
    void update(GiaoVien giaoVien);

    @Delete
    void delete(GiaoVien giaoVien);
}
