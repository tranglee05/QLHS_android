package com.example.quanlyhocsinhmobile.data.local.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlyhocsinhmobile.data.local.Model.TaiKhoan;

import java.util.List;

@Dao
public interface TaiKhoanDAO {

    // Lấy toàn bộ danh sách tài khoản
    @Query("SELECT * FROM TaiKhoan")
    List<TaiKhoan> getAll();

    // Thêm tài khoản mới (Nếu trùng khóa chính sẽ ghi đè)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaiKhoan taiKhoan);

    // Cập nhật thông tin tài khoản
    @Update
    void update(TaiKhoan taiKhoan);

    // Xóa tài khoản
    @Delete
    void delete(TaiKhoan taiKhoan);

    // Kiểm tra tên đăng nhập đã tồn tại chưa
    @Query("SELECT COUNT(*) FROM TaiKhoan WHERE tenDangNhap = :user")
    int checkTenDangNhap(String user);

    // Kiểm tra đăng nhập (Dùng cho chức năng Login)
    @Query("SELECT * FROM TaiKhoan WHERE tenDangNhap = :user AND matKhau = :pass")
    TaiKhoan login(String user, String pass);

    // Tìm kiếm tài khoản theo tên đăng nhập hoặc mã người dùng
    @Query("SELECT * FROM TaiKhoan WHERE tenDangNhap LIKE '%' || :query || '%' OR maNguoiDung LIKE '%' || :query || '%'")
    List<TaiKhoan> search(String query);

    // Lấy quyền của tài khoản dựa trên tên đăng nhập
    @Query("SELECT quyen FROM TaiKhoan WHERE tenDangNhap = :user")
    String getQuyen(String user);

    @Query("SELECT maGV FROM GiaoVien")
    List<String> getAllMaGV();

    @Query("SELECT maHS FROM HocSinh")
    List<String> getAllMaHS();
}