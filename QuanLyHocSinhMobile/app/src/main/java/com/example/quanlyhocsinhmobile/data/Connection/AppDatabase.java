package com.example.quanlyhocsinhmobile.data.Connection;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.quanlyhocsinhmobile.data.DAO.*;
import com.example.quanlyhocsinhmobile.data.Model.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
    ToHopMon.class,
    PhongHoc.class,
    MonHoc.class,
    DoiTuongUuTien.class,
    TaiKhoan.class,
    GiaoVien.class,
    Lop.class,
    HocSinh.class,
    ThoiKhoaBieu.class,
    Diem.class,
    HanhKiem.class,
    LichThi.class,
    HocPhi.class,
    ThongBao.class,
    PhucKhao.class
}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract DiemDAO diemDAO();
    public abstract HocSinhDAO hocSinhDAO();
    public abstract MonHocDAO monHocDAO();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "quanlyhocsinh_db")
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                if (isTableEmpty(db, "HocSinh")) {
                    seedData(db);
                }
            });
        }

        private boolean isTableEmpty(SupportSQLiteDatabase db, String tableName) {
            try (Cursor cursor = db.query("SELECT COUNT(*) FROM " + tableName)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getInt(0) == 0;
                }
            } catch (Exception e) {
                return true;
            }
            return true;
        }

        private void seedData(SupportSQLiteDatabase db) {
            db.beginTransaction();
            try {
                // 1. DANH MỤC CƠ BẢN
                db.execSQL("INSERT INTO DoiTuongUuTien (maDT, tenDT, tiLeGiamHocPhi) VALUES ('DT01', 'Hộ nghèo', 0.5), ('DT02', 'Con thương binh', 1.0);");
                db.execSQL("INSERT INTO ToHopMon (maToHop, tenToHop) VALUES ('KHTN', 'Khoa học Tự nhiên'), ('KHXH', 'Khoa học Xã hội'), ('CB', 'Cơ bản');");
                db.execSQL("INSERT INTO MonHoc (maMH, tenMH) VALUES " +
                        "('MH01', 'Toán học'), ('MH02', 'Ngữ văn'), ('MH03', 'Tiếng Anh'), " +
                        "('MH04', 'Vật lý'), ('MH05', 'Hóa học'), ('MH06', 'Sinh học'), " +
                        "('MH07', 'Lịch sử'), ('MH08', 'Địa lý'), ('MH09', 'Giáo dục công dân'), ('MH10', 'Tin học');");
                db.execSQL("INSERT INTO PhongHoc (maPhong, tenPhong, sucChua, loaiPhong, tinhTrang) VALUES " +
                        "('P101', 'Phòng 101', 45, 'Lý thuyết', 'Trống'), ('P102', 'Phòng 102', 45, 'Lý thuyết', 'Trống'), " +
                        "('P201', 'Phòng 201', 45, 'Lý thuyết', 'Trống'), ('LAB1', 'Máy tính 1', 50, 'Thực hành', 'Trống');");

                // 2. NHÂN SỰ
                db.execSQL("INSERT INTO GiaoVien (maGV, hoTen, ngaySinh, sdt) VALUES " +
                        "('GV01', 'Nguyễn Bá Đạt', '1985-05-20', '0901234567'), ('GV02', 'Trần Thu Trang', '1990-11-15', '0912345678');");
                db.execSQL("INSERT INTO Lop (maLop, tenLop, nienKhoa, maGVCN) VALUES " +
                        "('L10A1', '10A1', '2023-2026', 'GV01'), ('L10A2', '10A2', '2023-2026', 'GV02'), ('L11A1', '11A1', '2022-2025', 'GV01');");

                // 100 HỌC SINH
                String hsSql = "INSERT INTO HocSinh (maHS, hoTen, ngaySinh, gioiTinh, diaChi, maLop, maDT) VALUES ";
                db.execSQL(hsSql + "('HS001', 'Nguyễn Thị Mai', '2005-10-01', 'Nữ', 'Hà Nội', 'L10A1', null), ('HS002', 'Trần Thị Lan', '2008-02-20', 'Nữ', 'Bắc Ninh', 'L10A2', null), ('HS003', 'Lê Văn Tuấn', '2007-01-28', 'Nam', 'Thái Nguyên', 'L10A2', 'DT02'), ('HS004', 'Phạm Văn Minh', '2006-05-03', 'Nam', 'Hải Phòng', 'L10A2', 'DT02'), ('HS005', 'Hoàng Thị Thu', '2008-07-11', 'Nữ', 'Nam Định', 'L11A1', null);");
                db.execSQL(hsSql + "('HS006', 'Vũ Thị Hoa', '2007-01-15', 'Nữ', 'Thái Bình', 'L11A1', 'DT02'), ('HS007', 'Võ Văn Dũng', '2006-10-13', 'Nam', 'Hưng Yên', 'L11A1', null), ('HS008', 'Đặng Văn Thành', '2007-05-24', 'Nam', 'Hải Dương', 'L10A1', null), ('HS009', 'Bùi Văn Hùng', '2006-09-27', 'Nam', 'Vĩnh Phúc', 'L11A1', null), ('HS010', 'Đỗ Thị Hằng', '2008-07-08', 'Nữ', 'Hà Nam', 'L10A2', 'DT02');");
                // ... Thêm tiếp các HS khác để đủ 100 nếu cần, ở đây tôi chèn đại diện các dải để test CROSS JOIN
                for(int i=11; i<=100; i++) {
                    String id = String.format("HS%03d", i);
                    String lop = (i % 3 == 0) ? "L10A1" : (i % 3 == 1 ? "L10A2" : "L11A1");
                    db.execSQL("INSERT INTO HocSinh (maHS, hoTen, ngaySinh, gioiTinh, diaChi, maLop, maDT) VALUES ('"+id+"', 'Học sinh "+i+"', '2007-01-01', 'Nam', 'Địa chỉ "+i+"', '"+lop+"', null);");
                }

                // 3. ĐIỂM (CROSS JOIN)
                db.execSQL("INSERT INTO Diem (maHS, maMH, hocKy, diem15p, diem1Tiet, diemGiuaKy, diemCuoiKy) " +
                        "SELECT h.maHS, m.maMH, 1, (ABS(RANDOM()) % 101) / 10.0, (ABS(RANDOM()) % 101) / 10.0, (ABS(RANDOM()) % 101) / 10.0, (ABS(RANDOM()) % 101) / 10.0 " +
                        "FROM HocSinh h CROSS JOIN MonHoc m;");
                db.execSQL("UPDATE Diem SET diemTongKet = ROUND((diem15p + diem1Tiet * 2 + diemGiuaKy * 2 + diemCuoiKy * 3) / 8.0, 1);");

                // HẠNH KIỂM & HỌC PHÍ
                db.execSQL("INSERT INTO HanhKiem (maHS, hocKy, namHoc, xepLoai, nhanXet) SELECT maHS, 1, '2025-2026', 'Tốt', 'Ngoan.' FROM HocSinh;");
                db.execSQL("INSERT INTO HocPhi (maHS, hocKy, namHoc, tongTien, mienGiam, phaiDong, trangThai) " +
                        "SELECT maHS, 1, '2025-2026', 2000000, 0, 2000000, 'Chưa đóng' FROM HocSinh;");

                // 4. HỆ THỐNG
                db.execSQL("INSERT INTO TaiKhoan (tenDangNhap, matKhau, quyen, maNguoiDung) VALUES ('admin', '123456', 'Admin', 'AD01');");
                db.execSQL("INSERT INTO ThongBao (tieuDe, noiDung, nguoiGui) VALUES ('Chào mừng', 'Chào mừng năm học mới.', 'AD01');");
                
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    };
}
