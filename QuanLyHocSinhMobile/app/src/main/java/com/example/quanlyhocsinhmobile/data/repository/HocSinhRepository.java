package com.example.quanlyhocsinhmobile.data.repository;

import android.app.Application;

import com.example.quanlyhocsinhmobile.data.local.AppDatabase;
import com.example.quanlyhocsinhmobile.data.local.DAO.HocSinhDAO;
import com.example.quanlyhocsinhmobile.data.local.Model.HocSinh;

import java.util.List;

public class HocSinhRepository {

    private final HocSinhDAO dao;

    public HocSinhRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.hocSinhDAO();
    }

    // Lấy tất cả học sinh
    public List<HocSinh> getAll() {
        return dao.getAll();
    }

    // Tìm kiếm
    public List<HocSinh> search(String q) {
        return dao.search(q);
    }

    // Thêm
    public void insert(HocSinh hs) {
         dao.insert(hs);
    }

    // Sửa
    public void update(HocSinh hs) {
         dao.update(hs);
    }

    // Xóa
    public void delete(HocSinh hs) {
         dao.delete(hs);
    }

    // Check trùng mã
    public int checkMa(String ma) {
        return dao.checkMaHocSinh(ma);
    }

    // Check trùng tên
    public int checkTen(String ten) {
        return dao.checkTenHocSinh(ten);
    }
}