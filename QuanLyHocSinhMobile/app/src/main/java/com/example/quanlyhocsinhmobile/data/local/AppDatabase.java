package com.example.quanlyhocsinhmobile.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.quanlyhocsinhmobile.data.local.DAO.DiemDAO;
import com.example.quanlyhocsinhmobile.data.local.DAO.HanhKiemDAO;
//import com.example.quanlyhocsinhmobile.data.local.DAO.HocPhiDAO;
import com.example.quanlyhocsinhmobile.data.local.DAO.HocSinhDAO;
import com.example.quanlyhocsinhmobile.data.local.DAO.LichThiDAO;
import com.example.quanlyhocsinhmobile.data.local.DAO.LopDAO;
import com.example.quanlyhocsinhmobile.data.local.DAO.MonHocDAO;
import com.example.quanlyhocsinhmobile.data.local.DAO.PhongHocDAO;
import com.example.quanlyhocsinhmobile.data.local.Model.Diem;
import com.example.quanlyhocsinhmobile.data.local.Model.DoiTuongUuTien;
import com.example.quanlyhocsinhmobile.data.local.Model.GiaoVien;
import com.example.quanlyhocsinhmobile.data.local.Model.HanhKiem;
import com.example.quanlyhocsinhmobile.data.local.Model.HocPhi;
import com.example.quanlyhocsinhmobile.data.local.Model.HocSinh;
import com.example.quanlyhocsinhmobile.data.local.Model.LichThi;
import com.example.quanlyhocsinhmobile.data.local.Model.Lop;
import com.example.quanlyhocsinhmobile.data.local.Model.MonHoc;
import com.example.quanlyhocsinhmobile.data.local.Model.PhongHoc;
import com.example.quanlyhocsinhmobile.data.local.Model.PhucKhao;
import com.example.quanlyhocsinhmobile.data.local.Model.TaiKhoan;
import com.example.quanlyhocsinhmobile.data.local.Model.ThoiKhoaBieu;
import com.example.quanlyhocsinhmobile.data.local.Model.ThongBao;
import com.example.quanlyhocsinhmobile.data.local.Model.ToHopMon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
    ToHopMon.class, PhongHoc.class, MonHoc.class, DoiTuongUuTien.class,
    TaiKhoan.class, GiaoVien.class, Lop.class, HocSinh.class,
    ThoiKhoaBieu.class, Diem.class, HanhKiem.class, LichThi.class,
    HocPhi.class, ThongBao.class, PhucKhao.class
}, version = 17, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // --- 1. Khai báo các DAO ---
    public abstract DiemDAO diemDAO();
    public abstract HocSinhDAO hocSinhDAO();
    public abstract MonHocDAO monHocDAO();
    public abstract LichThiDAO lichThiDAO();
    public abstract PhongHocDAO phongHocDAO();
    public abstract LopDAO lopDAO();
    public abstract HanhKiemDAO hanhKiemDAO();
//    public abstract HocPhiDAO hocPhiDAO();

    //Cấu hình Singleton & Executor (cái này để tối ưu database giúp chạy mượt mà
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "quanlyhocsinh_db")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() 
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- 3. Callback khởi tạo dữ liệu mẫu ---
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Gọi logic nạp dữ liệu từ file KhoiTaoDatabase riêng biệt
            databaseWriteExecutor.execute(() -> KhoiTaoDatabase.checkAndSeedData(db));
        }
    };
}
