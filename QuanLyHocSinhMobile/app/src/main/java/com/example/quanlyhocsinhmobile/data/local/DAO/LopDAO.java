package com.example.quanlyhocsinhmobile.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlyhocsinhmobile.data.local.Model.Lop;

import java.util.List;

@Dao
public interface LopDAO {
    @Query("SELECT * FROM Lop")
    List<Lop> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lop lop);

    @Update
    void update(Lop lop);

    @Delete
    void delete(Lop lop);

    @Query("SELECT * FROM Lop WHERE MaLop LIKE '%' || :query || '%' OR TenLop LIKE '%' || :query || '%' OR nienKhoa LIKE '%' || :query || '%' OR maGVCN LIKE '%' || :query || '%'")
    List<Lop> searchLop(String query);

    @Query("SELECT COUNT(*) FROM Lop WHERE MaLop = :maLop")
    int checkMaLop(String maLop);

    @Query("SELECT COUNT(*) FROM Lop WHERE TenLop = :tenLop")
    int checkTenLop(String tenLop);

    @Query("SELECT COUNT(*) FROM GiaoVien WHERE maGV = :maGV")
    int checkGiaoVienTonTai(String maGV);

    @Query("SELECT COUNT(*) FROM Lop WHERE maGVCN = :maGV")
    int checkGVCNDaPhanCong(String maGV);

    @Query("SELECT COUNT(*) FROM Lop WHERE maGVCN = :maGV")
    int checkGVCNTonTai(String maGV);

    @Query("SELECT MaLop FROM Lop")
    List<String> getAllMaLop();
}


