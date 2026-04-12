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
import com.example.quanlyhocsinhmobile.data.DAO.GiaoVienDAO;
import com.example.quanlyhocsinhmobile.data.DAO.LopDAO;
import com.example.quanlyhocsinhmobile.data.DAO.MonHocDAO;
import com.example.quanlyhocsinhmobile.data.DAO.PhongHocDAO;
import com.example.quanlyhocsinhmobile.data.DAO.TKBDAO;
import com.example.quanlyhocsinhmobile.data.Model.GiaoVien;
import com.example.quanlyhocsinhmobile.data.Model.Lop;
import com.example.quanlyhocsinhmobile.data.Model.MonHoc;
import com.example.quanlyhocsinhmobile.data.Model.PhongHoc;
import com.example.quanlyhocsinhmobile.data.Model.ThoiKhoaBieu;

import java.util.ArrayList;
import java.util.List;

public class TKBUI extends AppCompatActivity {

    private Spinner spinnerClass, spinnerSubject, spinnerThu;
    private Button btnFilter;
    private RecyclerView rvTkb;

    private TextView tvTkbInfo;
    private Spinner spUpdateTietBD, spUpdateTietKT, spUpdateClass, spUpdateSubject, spUpdateRoom, spUpdateTeacher, spUpdateThu;
    private Button btnAdd, btnSave, btnDelete, btnRefresh, btnExcel;

    private TKBDAO tkbDAO;
    private LopDAO lopDAO;
    private MonHocDAO monHocDAO;
    private GiaoVienDAO giaoVienDAO;
    private PhongHocDAO phongHocDAO;

    private TKBAdapter adapter;
    private List<ThoiKhoaBieu.Display> currentList = new ArrayList<>();
    private List<Lop> listLop = new ArrayList<>();
    private List<MonHoc> listMonHoc = new ArrayList<>();
    private List<GiaoVien> listGiaoVien = new ArrayList<>();
    private List<PhongHoc> listPhongHoc = new ArrayList<>();

    private ThoiKhoaBieu currentSelectedTKB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letrang_tkb);

        AppDatabase db = AppDatabase.getDatabase(this);
        tkbDAO = db.tkbDAO();
        lopDAO = db.lopDAO();
        monHocDAO = db.monHocDAO();
        giaoVienDAO = db.giaoVienDAO();
        phongHocDAO = db.phongHocDAO();

        initViews();
        setupRecyclerView();
        setupSpinners();
        loadData();
    }

    private void initViews() {
        spinnerClass = findViewById(R.id.spinner_class);
        spinnerSubject = findViewById(R.id.spinner_subject);
        spinnerThu = findViewById(R.id.spinner_thu);
        btnFilter = findViewById(R.id.btn_filter);
        rvTkb = findViewById(R.id.rv_tkb);
        rvTkb.setLayoutManager(new LinearLayoutManager(this));

        tvTkbInfo = findViewById(R.id.tv_tkb_info);
        spUpdateTietBD = findViewById(R.id.spinner_tietBD);
        spUpdateTietKT = findViewById(R.id.spinner_tietKT);
        spUpdateClass = findViewById(R.id.spinner_update_class);
        spUpdateSubject = findViewById(R.id.spinner_update_subject);
        spUpdateRoom = findViewById(R.id.spinner_update_room);
        spUpdateTeacher = findViewById(R.id.spinner_update_teacher);
        spUpdateThu = findViewById(R.id.spinner_update_thu);

        btnAdd = findViewById(R.id.btn_add);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnExcel = findViewById(R.id.btn_excel);

        btnFilter.setOnClickListener(v -> loadData());

        btnAdd.setOnClickListener(v -> handleAdd());
        btnSave.setOnClickListener(v -> handleUpdate());
        btnDelete.setOnClickListener(v -> {
            if (currentSelectedTKB != null) {
                tkbDAO.delete(currentSelectedTKB);
                loadData();
                Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(this, "Vui lòng chọn một dòng để xóa", Toast.LENGTH_SHORT).show();
            }
        });
        btnRefresh.setOnClickListener(v -> {
            loadData();
            clearInputs();
            Toast.makeText(this, "Đã làm mới danh sách", Toast.LENGTH_SHORT).show();
        });

        btnExcel.setOnClickListener(v -> {
            Toast.makeText(this, "Xuất file Excel...", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleAdd() {
        ThoiKhoaBieu tkb = new ThoiKhoaBieu();
        if (fillTKBFromInputs(tkb)) {
            // Kiểm tra chồng chéo lịch (Thứ, Lớp, GV, Phòng, Khoảng tiết)
            if (tkbDAO.checkOverlap(0, tkb.getThu(), tkb.getMaLop(), tkb.getMaGV(), tkb.getMaPhong(), 
                                  tkb.getTietBatDau(), tkb.getTietKetThuc()) > 0) {
                Toast.makeText(this, "Trùng lịch! Lớp, Giáo viên hoặc Phòng đã có lịch trong khoảng tiết này.", Toast.LENGTH_LONG).show();
                return;
            }
            
            tkbDAO.insert(tkb);
            loadData();
            Toast.makeText(this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
            clearInputs();
        }
    }

    private void handleUpdate() {
        if (currentSelectedTKB == null) {
            Toast.makeText(this, "Vui lòng chọn một dòng để sửa", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ThoiKhoaBieu tkb = new ThoiKhoaBieu();
        tkb.setMaTKB(currentSelectedTKB.getMaTKB());
        if (fillTKBFromInputs(tkb)) {
            // Kiểm tra chồng chéo lịch (ngoại trừ chính nó)
            if (tkbDAO.checkOverlap(tkb.getMaTKB(), tkb.getThu(), tkb.getMaLop(), tkb.getMaGV(), tkb.getMaPhong(), 
                                  tkb.getTietBatDau(), tkb.getTietKetThuc()) > 0) {
                Toast.makeText(this, "Trùng lịch! Khoảng tiết mới xung đột với lịch đã có.", Toast.LENGTH_LONG).show();
                return;
            }

            tkbDAO.update(tkb);
            loadData();
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean fillTKBFromInputs(ThoiKhoaBieu tkb) {
        if (listLop.isEmpty() || listMonHoc.isEmpty() || listGiaoVien.isEmpty() || listPhongHoc.isEmpty()) {
            Toast.makeText(this, "Dữ liệu danh mục đang trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        int lopPos = spUpdateClass.getSelectedItemPosition();
        int monPos = spUpdateSubject.getSelectedItemPosition();
        int gvPos = spUpdateTeacher.getSelectedItemPosition();
        int phongPos = spUpdateRoom.getSelectedItemPosition();
        int thuPos = spUpdateThu.getSelectedItemPosition();
        int tbdPos = spUpdateTietBD.getSelectedItemPosition();
        int tktPos = spUpdateTietKT.getSelectedItemPosition();

        if (lopPos == 0 || monPos == 0 || gvPos == 0 || phongPos == 0 || thuPos == 0 || tbdPos == 0 || tktPos == 0) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tbdPos >= tktPos) {
            Toast.makeText(this, "Tiết kết thúc phải lớn hơn tiết bắt đầu", Toast.LENGTH_SHORT).show();
            return false;
        }

        tkb.setMaLop(listLop.get(lopPos - 1).getMaLop());
        tkb.setMaMH(listMonHoc.get(monPos - 1).getMaMH());
        tkb.setMaGV(listGiaoVien.get(gvPos - 1).getMaGV());
        tkb.setMaPhong(listPhongHoc.get(phongPos - 1).getMaPhong());
        tkb.setThu(thuPos + 1);
        tkb.setTietBatDau(tbdPos);
        tkb.setTietKetThuc(tktPos);
        
        return true;
    }

    private void clearInputs() {
        currentSelectedTKB = null;
        tvTkbInfo.setText("Tiết học: --");
        spUpdateClass.setSelection(0);
        spUpdateSubject.setSelection(0);
        spUpdateTeacher.setSelection(0);
        spUpdateRoom.setSelection(0);
        spUpdateThu.setSelection(0);
        spUpdateTietBD.setSelection(0);
        spUpdateTietKT.setSelection(0);
    }

    private void setupRecyclerView() {
        adapter = new TKBAdapter(currentList, display -> {
            currentSelectedTKB = display.getThoiKhoaBieu();
            tvTkbInfo.setText("Đang chọn: " + display.getTenMH() + " - Lớp " + display.getTenLop());
            
            setSpinnerSelection(spUpdateClass, display.getTenLop());
            setSpinnerSelection(spUpdateSubject, display.getTenMH());
            setSpinnerSelection(spUpdateTeacher, display.getTenGV());
            setSpinnerSelection(spUpdateRoom, display.getTenPhong());
            
            spUpdateTietBD.setSelection(currentSelectedTKB.getTietBatDau());
            spUpdateTietKT.setSelection(currentSelectedTKB.getTietKetThuc());
            
            int thuIndex = currentSelectedTKB.getThu() - 1; 
            if (thuIndex >= 0 && thuIndex < spUpdateThu.getCount()) {
                spUpdateThu.setSelection(thuIndex);
            }

            Toast.makeText(this, "Đã chọn: " + display.getTenMH(), Toast.LENGTH_SHORT).show();
        });
        rvTkb.setAdapter(adapter);
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (spinner.getAdapter() instanceof ArrayAdapter) {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            if (adapter != null && value != null) {
                int position = adapter.getPosition(value);
                if (position >= 0) {
                    spinner.setSelection(position);
                }
            }
        }
    }

    private void setupSpinners() {
        listLop = lopDAO.getAll();
        listMonHoc = monHocDAO.getAll();
        listGiaoVien = giaoVienDAO.getAll();
        listPhongHoc = phongHocDAO.getAll();

        List<String> lopNamesFilter = new ArrayList<>();
        lopNamesFilter.add("--- Lớp ---");
        for (Lop l : listLop) lopNamesFilter.add(l.getTenLop());
        spinnerClass.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lopNamesFilter));

        List<String> monNamesFilter = new ArrayList<>();
        monNamesFilter.add("--- Môn ---");
        for (MonHoc m : listMonHoc) monNamesFilter.add(m.getTenMH());
        spinnerSubject.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monNamesFilter));

        String[] listThuFilter = {"--- Thứ ---", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        spinnerThu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listThuFilter));

        List<String> lopNamesUpdate = new ArrayList<>();
        lopNamesUpdate.add("--- Chọn lớp ---");
        for (Lop l : listLop) lopNamesUpdate.add(l.getTenLop());
        spUpdateClass.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lopNamesUpdate));

        List<String> monNamesUpdate = new ArrayList<>();
        monNamesUpdate.add("--- Chọn môn ---");
        for (MonHoc m : listMonHoc) monNamesUpdate.add(m.getTenMH());
        spUpdateSubject.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monNamesUpdate));

        List<String> gvNamesUpdate = new ArrayList<>();
        gvNamesUpdate.add("--- Chọn GV ---");
        for (GiaoVien g : listGiaoVien) gvNamesUpdate.add(g.getHoTen());
        spUpdateTeacher.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, gvNamesUpdate));

        List<String> roomNamesUpdate = new ArrayList<>();
        roomNamesUpdate.add("--- Chọn phòng ---");
        for (PhongHoc p : listPhongHoc) roomNamesUpdate.add(p.getTenPhong());
        spUpdateRoom.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roomNamesUpdate));

        String[] listThuUpdate = {"--- Chọn thứ ---", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        spUpdateThu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listThuUpdate));

        String[] listTietBD = {"--- Tiết BD ---", "Tiết 1", "Tiết 2", "Tiết 3", "Tiết 4", "Tiết 5", "Tiết 6", "Tiết 7", "Tiết 8", "Tiết 9", "Tiết 10"};
        spUpdateTietBD.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTietBD));

        String[] listTietKT = {"--- Tiết KT ---", "Tiết 1", "Tiết 2", "Tiết 3", "Tiết 4", "Tiết 5", "Tiết 6", "Tiết 7", "Tiết 8", "Tiết 9", "Tiết 10"};
        spUpdateTietKT.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTietKT));
    }

    private void loadData() {
        String maLop = "";
        int lopPos = spinnerClass.getSelectedItemPosition();
        if (lopPos > 0 && !listLop.isEmpty()) {
            maLop = listLop.get(lopPos - 1).getMaLop();
        }

        String maMH = "";
        int monPos = spinnerSubject.getSelectedItemPosition();
        if (monPos > 0 && !listMonHoc.isEmpty()) {
            maMH = listMonHoc.get(monPos - 1).getMaMH();
        }

        int thuPos = spinnerThu.getSelectedItemPosition();
        int queryThu = (thuPos == 0) ? 0 : thuPos + 1;

        currentList = tkbDAO.filterTKB(queryThu, maLop, maMH);
        adapter.setTkbList(currentList);
    }
}
