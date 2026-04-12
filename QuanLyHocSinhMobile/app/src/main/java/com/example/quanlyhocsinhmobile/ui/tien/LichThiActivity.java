package com.example.quanlyhocsinhmobile.ui.tien;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quanlyhocsinhmobile.data.local.Model.LichThi;
import com.example.quanlyhocsinhmobile.data.local.Model.MonHoc;
import com.example.quanlyhocsinhmobile.data.local.Model.PhongHoc;
import com.example.quanlyhocsinhmobile.databinding.TienActivityLichthiBinding;
import com.example.quanlyhocsinhmobile.utils.ExcelHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LichThiActivity extends AppCompatActivity {

    private TienActivityLichthiBinding binding;
    private LichThiViewModel viewModel;
    private LichThiAdapter adapter;
    private LichThi selectedLichThi;

    private List<MonHoc> listMonHoc = new ArrayList<>();
    private List<PhongHoc> listPhongHoc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TienActivityLichthiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LichThiViewModel.class);

        setupRecyclerView();
        observeViewModel();
        setupDateTimePickers();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        binding.rvLichthi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LichThiAdapter(new ArrayList<>(), display -> {
            selectedLichThi = display.getLichThi();
            displaySelectedLichThi();
        });
        binding.rvLichthi.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getMonHocList().observe(this, monHocs -> {
            this.listMonHoc = monHocs;
            List<String> monNames = new ArrayList<>();
            List<String> filterMonNames = new ArrayList<>();
            filterMonNames.add("-- Tất cả môn học --");
            for (MonHoc mh : monHocs) {
                monNames.add(mh.getTenMH());
                filterMonNames.add(mh.getTenMH());
            }
            binding.spinnerFormMon.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, monNames));
            binding.spinnerFilterMon.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterMonNames));
        });

        viewModel.getPhongHocList().observe(this, phongHocs -> {
            this.listPhongHoc = phongHocs;
            List<String> phongNames = new ArrayList<>();
            List<String> filterPhongNames = new ArrayList<>();
            filterPhongNames.add("-- Tất cả phòng --");
            for (PhongHoc ph : phongHocs) {
                phongNames.add(ph.getTenPhong());
                filterPhongNames.add(ph.getTenPhong());
            }
            binding.spinnerFormPhong.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phongNames));
            binding.spinnerFilterPhong.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterPhongNames));
        });

        viewModel.getLichThiList().observe(this, list -> {
            adapter.setLichThiList(list);
        });
    }

    private void setupDateTimePickers() {
        binding.etFormNgaythi.setFocusable(false);
        binding.etFormNgaythi.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                binding.etFormNgaythi.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.etFormGiobd.setFocusable(false);
        binding.etFormGiobd.setOnClickListener(v -> showTimePicker(binding.etFormGiobd));

        binding.etFormGiokt.setFocusable(false);
        binding.etFormGiokt.setOnClickListener(v -> showTimePicker(binding.etFormGiokt));
    }

    private void showTimePicker(EditText editText) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            editText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void setupClickListeners() {
        binding.btnFilterLichthi.setOnClickListener(v -> {
            String maMH = binding.spinnerFilterMon.getSelectedItemPosition() > 0 ? 
                         listMonHoc.get(binding.spinnerFilterMon.getSelectedItemPosition() - 1).getMaMH() : "";
            String maPhong = binding.spinnerFilterPhong.getSelectedItemPosition() > 0 ? 
                            listPhongHoc.get(binding.spinnerFilterPhong.getSelectedItemPosition() - 1).getMaPhong() : "";
            viewModel.filter(maMH, maPhong);
        });

        binding.btnSearchLichthi.setOnClickListener(v -> {
            String query = binding.etSearchLichthi.getText().toString();
            if (!query.isEmpty()) viewModel.search(query);
            else viewModel.loadAll();
        });

        binding.btnAddLichthi.setOnClickListener(v -> {
            LichThi lt = getFormData();
            if (lt != null) {
                viewModel.insert(lt);
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                refreshForm();
            }
        });

        binding.btnSaveLichthi.setOnClickListener(v -> {
            if (selectedLichThi == null) return;
            LichThi lt = getFormData();
            if (lt != null) {
                lt.setMaLT(selectedLichThi.getMaLT());
                viewModel.update(lt);
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDeleteLichthi.setOnClickListener(v -> {
            if (selectedLichThi != null) {
                viewModel.delete(selectedLichThi);
                refreshForm();
            }
        });

        binding.btnRefreshLichthi.setOnClickListener(v -> {
            refreshForm();
            viewModel.loadAll();
        });

        binding.btnExportExcelLichthi.setOnClickListener(v -> exportToExcel());
    }

    private LichThi getFormData() {
        String ten = binding.etFormTenkythi.getText().toString();
        if (ten.isEmpty()) return null;
        LichThi lt = new LichThi();
        lt.setTenKyThi(ten);
        lt.setNgayThi(binding.etFormNgaythi.getText().toString());
        lt.setGioBatDau(binding.etFormGiobd.getText().toString());
        lt.setGioKetThuc(binding.etFormGiokt.getText().toString());
        lt.setMaMH(listMonHoc.get(binding.spinnerFormMon.getSelectedItemPosition()).getMaMH());
        lt.setMaPhong(listPhongHoc.get(binding.spinnerFormPhong.getSelectedItemPosition()).getMaPhong());
        return lt;
    }

    private void displaySelectedLichThi() {
        if (selectedLichThi == null) return;
        binding.etFormTenkythi.setText(selectedLichThi.getTenKyThi());
        binding.etFormNgaythi.setText(selectedLichThi.getNgayThi());
        binding.etFormGiobd.setText(selectedLichThi.getGioBatDau());
        binding.etFormGiokt.setText(selectedLichThi.getGioKetThuc());
        for (int i = 0; i < listMonHoc.size(); i++) 
            if (listMonHoc.get(i).getMaMH().equals(selectedLichThi.getMaMH())) binding.spinnerFormMon.setSelection(i);
        for (int i = 0; i < listPhongHoc.size(); i++) 
            if (listPhongHoc.get(i).getMaPhong().equals(selectedLichThi.getMaPhong())) binding.spinnerFormPhong.setSelection(i);
    }

    private void refreshForm() {
        selectedLichThi = null;
        binding.etFormTenkythi.setText("");
        binding.etFormNgaythi.setText("");
        binding.etFormGiobd.setText("");
        binding.etFormGiokt.setText("");
    }

    private void exportToExcel() {
        ExcelHelper.exportToExcel(this, "LichThi", "LichThi",
            new String[]{"Mã LT", "Tên Kỳ Thi", "Môn Học", "Phòng", "Ngày Thi", "Giờ Bắt Đầu", "Giờ Kết Thúc"},
            adapter.getLichThiList(), (row, display) -> {
                LichThi lt = display.getLichThi();
                row.createCell(0).setCellValue(lt.getMaLT());
                row.createCell(1).setCellValue(lt.getTenKyThi());
                row.createCell(2).setCellValue(display.getTenMH());
                row.createCell(3).setCellValue(display.getTenPhong());
                row.createCell(4).setCellValue(lt.getNgayThi());
                row.createCell(5).setCellValue(lt.getGioBatDau());
                row.createCell(6).setCellValue(lt.getGioKetThuc());
            });
    }
}
