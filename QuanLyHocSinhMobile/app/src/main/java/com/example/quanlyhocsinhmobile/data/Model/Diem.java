package com.example.quanlyhocsinhmobile.data.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName = "Diem",
        primaryKeys = {"maHS", "maMH", "hocKy"},
        foreignKeys = {
                @ForeignKey(entity = HocSinh.class, parentColumns = "maHS", childColumns = "maHS", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
                @ForeignKey(entity = MonHoc.class, parentColumns = "maMH", childColumns = "maMH", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
        },
        indices = {@Index("maMH")})
public class Diem {
    @NonNull
    private String maHS;
    @Ignore
    private String tenHS; 
    @NonNull
    private String maMH;
    @Ignore
    private String tenMH;
    @NonNull
    private int hocKy;

    private Double diem15p;  
    private Double diem1Tiet;
    private Double diemGiuaKy;
    private Double diemCuoiKy;    
    private Double diemTongKet;

    public Diem() {}

    @Ignore
    public Diem(String maHS, String maMH, int hocKy, Double diem15p, Double diem1Tiet, Double diemGiuaKy, Double diemCuoiKy) {
        this.maHS = maHS;
        this.maMH = maMH;
        this.hocKy = hocKy;
        this.diem15p = diem15p;
        this.diem1Tiet = diem1Tiet;
        this.diemGiuaKy = diemGiuaKy;
        this.diemCuoiKy = diemCuoiKy;
    }

    @NonNull
    public String getMaHS() { return maHS; }
    public void setMaHS(@NonNull String maHS) { this.maHS = maHS; }

    public String getTenHS() { return tenHS; }
    public void setTenHS(String tenHS) { this.tenHS = tenHS; }

    @NonNull
    public String getMaMH() { return maMH; }
    public void setMaMH(@NonNull String maMH) { this.maMH = maMH; }

    public String getTenMH() { return tenMH; }
    public void setTenMH(String tenMH) { this.tenMH = tenMH; }

    public int getHocKy() { return hocKy; }
    public void setHocKy(int hocKy) { this.hocKy = hocKy; }

    public Double getDiem15p() { return diem15p; }
    public void setDiem15p(Double diem15p) { this.diem15p = diem15p; }

    public Double getDiem1Tiet() { return diem1Tiet; }
    public void setDiem1Tiet(Double diem1Tiet) { this.diem1Tiet = diem1Tiet; }

    public Double getDiemGiuaKy() { return diemGiuaKy; }
    public void setDiemGiuaKy(Double diemGiuaKy) { this.diemGiuaKy = diemGiuaKy; }

    public Double getDiemCuoiKy() { return diemCuoiKy; }
    public void setDiemCuoiKy(Double diemCuoiKy) { this.diemCuoiKy = diemCuoiKy; }

    public Double getDiemTongKet() { return diemTongKet; }
    public void setDiemTongKet(Double diemTongKet) { this.diemTongKet = diemTongKet; }

    public double calculateDiemTongKet() {
        if (diem15p == null || diem1Tiet == null || diemGiuaKy == null || diemCuoiKy == null) return 0.0;
        return (diem15p + diem1Tiet + diemGiuaKy * 2 + diemCuoiKy * 3) / 7.0;
    }
}