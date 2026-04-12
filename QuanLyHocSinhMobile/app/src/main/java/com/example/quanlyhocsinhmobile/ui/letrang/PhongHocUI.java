package com.example.quanlyhocsinhmobile.ui.letrang;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyhocsinhmobile.R;
import com.example.quanlyhocsinhmobile.data.Connection.AppDatabase;
import com.example.quanlyhocsinhmobile.data.DAO.PhongHocDAO;
import com.example.quanlyhocsinhmobile.data.Model.PhongHoc;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PhongHocUI extends AppCompatActivity {

    private Spinner spinnerFilterType;
    private Button btnFilter;
    private RecyclerView rvPhongHoc;
    
    private TextView tvPhongInfo;
    private TextInputEditText etMaPhong, etTenPhong, etSucChua;
    private Spinner spinnerLoaiPhong;
    private Button btnAdd, btnSave, btnDelete, btnRefresh;

    private PhongHocDAO phongHocDAO;
    private PhongHocAdapter adapter;
    private List<PhongHoc> listPhongHoc = new ArrayList<>();
    private PhongHoc selectedPhongHoc = null;

    private final String[] loaiPhongArray = {"Lý thuyết", "Thực hành", "Hội trường", "Phòng thí nghiệm"};
    private final String[] filterLoaiArray = {"Tất cả", "Lý thuyết", "Thực hành", "Hội trường", "Phòng thí nghiệm"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letrang_phonghoc);

        AppDatabase db = AppDatabase.getDatabase(this);
        phongHocDAO = db.phongHocDAO();

        initViews();
        setupRecyclerView();
        setupSpinners();
        loadData();
    }

    private void initViews() {
        spinnerFilterType = findViewById(R.id.spinner_filter_type);
        btnFilter = findViewById(R.id.btn_filter);
        rvPhongHoc = findViewById(R.id.rv_phong_hoc);
        tvPhongInfo = findViewById(R.id.tv_phong_info);
        etMaPhong = findViewById(R.id.et_ma_phong);
        etTenPhong = findViewById(R.id.et_ten_phong);
        etSucChua = findViewById(R.id.et_suc_chua);
        spinnerLoaiPhong = findViewById(R.id.spinner_loai_phong);

        btnAdd = findViewById(R.id.btn_add);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnRefresh = findViewById(R.id.btn_refresh);

        btnFilter.setOnClickListener(v -> loadData());
        btnAdd.setOnClickListener(v -> handleAdd());
        btnSave.setOnClickListener(v -> handleUpdate());
        btnDelete.setOnClickListener(v -> handleDelete());
        btnRefresh.setOnClickListener(v -> {
            spinnerFilterType.setSelection(0);
            loadData();
            clearInputs();
        });
    }

    private void setupRecyclerView() {
        adapter = new PhongHocAdapter(listPhongHoc, phongHoc -> {
            selectedPhongHoc = phongHoc;
            tvPhongInfo.setText("Đang chọn: " + phongHoc.getTenPhong());
            etMaPhong.setText(phongHoc.getMaPhong());
            etTenPhong.setText(phongHoc.getTenPhong());
            etSucChua.setText(String.valueOf(phongHoc.getSucChua()));
            
            for (int i = 0; i < loaiPhongArray.length; i++) {
                if (loaiPhongArray[i].equals(phongHoc.getLoaiPhong())) {
                    spinnerLoaiPhong.setSelection(i);
                    break;
                }
            }
            etMaPhong.setEnabled(false);
        });
        rvPhongHoc.setLayoutManager(new LinearLayoutManager(this));
        rvPhongHoc.setAdapter(adapter);
    }

    private void setupSpinners() {
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterLoaiArray);
        spinnerFilterType.setAdapter(filterAdapter);

        ArrayAdapter<String> updateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, loaiPhongArray);
        spinnerLoaiPhong.setAdapter(updateAdapter);
    }

    private void loadData() {
        String selectedType = spinnerFilterType.getSelectedItem().toString();
        if (selectedType.equals("Tất cả")) {
            listPhongHoc = phongHocDAO.getAll();
        } else {
            List<PhongHoc> all = phongHocDAO.getAll();
            listPhongHoc = new ArrayList<>();
            for (PhongHoc p : all) {
                if (p.getLoaiPhong().equals(selectedType)) {
                    listPhongHoc.add(p);
                }
            }
        }
        adapter.setList(listPhongHoc);
    }

    private void handleAdd() {
        String ma = etMaPhong.getText().toString().trim();
        String ten = etTenPhong.getText().toString().trim();
        String sucChuaStr = etSucChua.getText().toString().trim();
        String loai = spinnerLoaiPhong.getSelectedItem().toString();

        if (ma.isEmpty() || ten.isEmpty() || sucChuaStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phongHocDAO.checkMaPhong(ma) > 0) {
            Toast.makeText(this, "Mã phòng đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phongHocDAO.checkTenPhong(ten) > 0) {
            Toast.makeText(this, "Tên phòng đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        PhongHoc phongHoc = new PhongHoc();
        phongHoc.setMaPhong(ma);
        phongHoc.setTenPhong(ten);
        phongHoc.setSucChua(Integer.parseInt(sucChuaStr));
        phongHoc.setLoaiPhong(loai);
        phongHoc.setTinhTrang("Trống");

        phongHocDAO.insert(phongHoc);
        loadData();
        clearInputs();
        Toast.makeText(this, "Thêm phòng học thành công", Toast.LENGTH_SHORT).show();
    }

    private void handleUpdate() {
        if (selectedPhongHoc == null) {
            Toast.makeText(this, "Vui lòng chọn phòng học để sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        String ten = etTenPhong.getText().toString().trim();
        String sucChuaStr = etSucChua.getText().toString().trim();
        String loai = spinnerLoaiPhong.getSelectedItem().toString();

        if (ten.isEmpty() || sucChuaStr.isEmpty()) {
            Toast.makeText(this, "Thông tin không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!selectedPhongHoc.getTenPhong().equals(ten) && phongHocDAO.checkTenPhong(ten) > 0) {
            Toast.makeText(this, "Tên phòng đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedPhongHoc.setTenPhong(ten);
        selectedPhongHoc.setSucChua(Integer.parseInt(sucChuaStr));
        selectedPhongHoc.setLoaiPhong(loai);

        phongHocDAO.update(selectedPhongHoc);
        loadData();
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
    }

    private void handleDelete() {
        if (selectedPhongHoc == null) {
            Toast.makeText(this, "Vui lòng chọn phòng học để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        phongHocDAO.delete(selectedPhongHoc);
        loadData();
        clearInputs();
        Toast.makeText(this, "Xóa phòng học thành công", Toast.LENGTH_SHORT).show();
    }

    private void clearInputs() {
        selectedPhongHoc = null;
        tvPhongInfo.setText("Phòng học: --");
        etMaPhong.setText("");
        etTenPhong.setText("");
        etSucChua.setText("");
        spinnerLoaiPhong.setSelection(0);
        etMaPhong.setEnabled(true);
    }
}
