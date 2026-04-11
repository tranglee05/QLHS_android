package com.example.quanlyhocsinhmobile.data.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "GiaoVien",
        foreignKeys = {@ForeignKey(entity = ToHopMon.class,
                parentColumns = "maToHop",
                childColumns = "maToHop",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE)},
        indices = {@Index("maToHop")})
public class GiaoVien {
    @PrimaryKey
    @NonNull
    private String maGV;
    @NonNull
    private String hoTen;
    private String ngaySinh;
    private String sdt;
    private String maToHop;

    public GiaoVien() {}

    @NonNull
    public String getMaGV() { return maGV; }
    public void setMaGV(@NonNull String maGV) { this.maGV = maGV; }

    @NonNull
    public String getHoTen() { return hoTen; }
    public void setHoTen(@NonNull String hoTen) { this.hoTen = hoTen; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getMaToHop() { return maToHop; }
    public void setMaToHop(String maToHop) { this.maToHop = maToHop; }
}
