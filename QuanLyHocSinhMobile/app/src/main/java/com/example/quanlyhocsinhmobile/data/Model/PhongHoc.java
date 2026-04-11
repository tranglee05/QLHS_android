package com.example.quanlyhocsinhmobile.data.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PhongHoc")
public class PhongHoc {
    @PrimaryKey
    @NonNull
    private String maPhong;
    private String tenPhong;
    private Integer sucChua;
    private String loaiPhong;
    private String tinhTrang = "Trống";

    public PhongHoc() {}

    @NonNull
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(@NonNull String maPhong) { this.maPhong = maPhong; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public Integer getSucChua() { return sucChua; }
    public void setSucChua(Integer sucChua) { this.sucChua = sucChua; }

    public String getLoaiPhong() { return loaiPhong; }
    public void setLoaiPhong(String loaiPhong) { this.loaiPhong = loaiPhong; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}
