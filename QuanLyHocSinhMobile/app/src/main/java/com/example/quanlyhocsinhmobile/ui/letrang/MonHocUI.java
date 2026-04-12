package com.example.quanlyhocsinhmobile.ui.letrang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyhocsinhmobile.R;
import com.example.quanlyhocsinhmobile.data.Connection.AppDatabase;
import com.example.quanlyhocsinhmobile.data.DAO.MonHocDAO;
import com.example.quanlyhocsinhmobile.data.Model.MonHoc;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MonHocUI extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvMonHoc;
    private TextView tvMonHocInfo;
    private TextInputEditText etMaMon, etTenMon;
    private Button btnAdd, btnSave, btnDelete, btnRefresh;

    private MonHocDAO monHocDAO;
    private MonHocAdapter adapter;
    private List<MonHoc> currentList = new ArrayList<>();
    private MonHoc selectedMonHoc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letrang_monhoc);

        AppDatabase db = AppDatabase.getDatabase(this);
        monHocDAO = db.monHocDAO();

        initViews();
        setupRecyclerView();
        loadData();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        rvMonHoc = findViewById(R.id.rv_mon_hoc);
        tvMonHocInfo = findViewById(R.id.tv_mon_hoc_info);
        etMaMon = findViewById(R.id.et_ma_mon);
        etTenMon = findViewById(R.id.et_ten_mon);
        btnAdd = findViewById(R.id.btn_add);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnRefresh = findViewById(R.id.btn_refresh);

        btnSearch.setOnClickListener(v -> searchData());
        btnAdd.setOnClickListener(v -> handleAdd());
        btnSave.setOnClickListener(v -> handleUpdate());
        btnDelete.setOnClickListener(v -> handleDelete());
        btnRefresh.setOnClickListener(v -> {
            loadData();
            clearInputs();
        });
    }

    private void setupRecyclerView() {
        adapter = new MonHocAdapter(currentList, monHoc -> {
            selectedMonHoc = monHoc;
            tvMonHocInfo.setText("Môn học: " + monHoc.getTenMH());
            etMaMon.setText(monHoc.getMaMH());
            etTenMon.setText(monHoc.getTenMH());
            etMaMon.setEnabled(false); // Không cho sửa mã môn khi đang chọn
        });
        rvMonHoc.setLayoutManager(new LinearLayoutManager(this));
        rvMonHoc.setAdapter(adapter);
    }

    private void loadData() {
        currentList = monHocDAO.getAll();
        adapter.setList(currentList);
    }

    private void searchData() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            loadData();
        } else {
            currentList = monHocDAO.search(query);
            adapter.setList(currentList);
        }
    }

    private void handleAdd() {
        String ma = etMaMon.getText().toString().trim();
        String ten = etTenMon.getText().toString().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (monHocDAO.checkMaMonHoc(ma) > 0) {
            Toast.makeText(this, "Mã môn học đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (monHocDAO.checkTenMonHoc(ten) > 0) {
            Toast.makeText(this, "Tên môn học đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        MonHoc monHoc = new MonHoc(ma, ten);
        monHocDAO.insert(monHoc);
        loadData();
        clearInputs();
        Toast.makeText(this, "Thêm môn học thành công", Toast.LENGTH_SHORT).show();
    }

    private void handleUpdate() {
        if (selectedMonHoc == null) {
            Toast.makeText(this, "Vui lòng chọn môn học để sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        String ten = etTenMon.getText().toString().trim();
        if (ten.isEmpty()) {
            Toast.makeText(this, "Tên môn học không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!selectedMonHoc.getTenMH().equals(ten) && monHocDAO.checkTenMonHoc(ten) > 0) {
            Toast.makeText(this, "Tên môn học đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedMonHoc.setTenMH(ten);
        monHocDAO.update(selectedMonHoc);
        loadData();
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
    }

    private void handleDelete() {
        if (selectedMonHoc == null) {
            Toast.makeText(this, "Vui lòng chọn môn học để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        monHocDAO.delete(selectedMonHoc);
        loadData();
        clearInputs();
        Toast.makeText(this, "Xóa môn học thành công", Toast.LENGTH_SHORT).show();
    }

    private void clearInputs() {
        selectedMonHoc = null;
        tvMonHocInfo.setText("Môn học: --");
        etMaMon.setText("");
        etTenMon.setText("");
        etMaMon.setEnabled(true);
        etSearch.setText("");
    }
}
