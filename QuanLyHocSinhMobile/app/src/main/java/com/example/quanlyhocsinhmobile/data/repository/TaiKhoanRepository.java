package com.example.quanlyhocsinhmobile.data.repository;

import android.app.Application;
import com.example.quanlyhocsinhmobile.data.local.AppDatabase;
import com.example.quanlyhocsinhmobile.data.local.DAO.TaiKhoanDAO;
import com.example.quanlyhocsinhmobile.data.local.Model.TaiKhoan;
import java.util.List;

public class TaiKhoanRepository {
    private TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taiKhoanDAO = db.taiKhoanDAO(); // Đảm bảo bạn đã thêm method taiKhoanDAO() trong AppDatabase
    }

    // Lấy toàn bộ danh sách tài khoản
    public List<TaiKhoan> getAllTaiKhoan() {
        return taiKhoanDAO.getAll();
    }

    // Tìm kiếm tài khoản
    public List<TaiKhoan> searchTaiKhoan(String query) {
        return taiKhoanDAO.search(query);
    }

    // Thêm tài khoản mới
    public void insert(TaiKhoan taiKhoan) {
        taiKhoanDAO.insert(taiKhoan);
    }

    // Cập nhật tài khoản
    public void update(TaiKhoan taiKhoan) {
        taiKhoanDAO.update(taiKhoan);
    }

    // Xóa tài khoản
    public void delete(TaiKhoan taiKhoan) {
        taiKhoanDAO.delete(taiKhoan);
    }

    // Kiểm tra trùng tên đăng nhập
    public int checkTenDangNhap(String user) {
        return taiKhoanDAO.checkTenDangNhap(user);
    }

    // Xử lý đăng nhập
    public TaiKhoan login(String user, String pass) {
        return taiKhoanDAO.login(user, pass);
    }

    // Lấy quyền của tài khoản
    public String getQuyen(String user) {
        return taiKhoanDAO.getQuyen(user);
    }

    public List<String> getAllMaGV() {
        return taiKhoanDAO.getAllMaGV();
    }

    public List<String> getAllMaHS() {
        return taiKhoanDAO.getAllMaHS();
    }
}