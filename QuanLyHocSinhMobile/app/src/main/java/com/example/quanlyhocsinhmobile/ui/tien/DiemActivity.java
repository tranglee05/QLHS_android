package com.example.quanlyhocsinhmobile.ui.tien;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quanlyhocsinhmobile.R;
import com.example.quanlyhocsinhmobile.data.local.Model.Diem;
import com.example.quanlyhocsinhmobile.data.local.Model.Lop;
import com.example.quanlyhocsinhmobile.data.local.Model.MonHoc;
import com.example.quanlyhocsinhmobile.databinding.TienActivityQuanlydiemBinding;
import com.example.quanlyhocsinhmobile.utils.ExcelHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiemActivity extends AppCompatActivity {

    private TienActivityQuanlydiemBinding binding;
    private DiemViewModel viewModel;
    private DiemAdapter adapter;
    private List<Lop> listLop = new ArrayList<>();
    private List<MonHoc> listMonHoc = new ArrayList<>();
    private Diem selectedDiem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TienActivityQuanlydiemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DiemViewModel.class);

        setupRecyclerView();
        setupSpinners();
        observeViewModel();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        binding.rvGrades.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiemAdapter(new ArrayList<>(), display -> {
            selectedDiem = display.getDiem();
            displaySelectedDiem(display);
        });
        binding.rvGrades.setAdapter(adapter);
    }

    private void setupSpinners() {
        String[] semesters = {"--- Tất cả HK ---", "HK: 1", "HK: 2"};
        binding.spinnerSemester.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, semesters));
    }

    private void observeViewModel() {
        viewModel.getLopList().observe(this, lops -> {
            this.listLop = lops;
            List<String> names = new ArrayList<>();
            names.add("--- Tất cả lớp ---");
            for (Lop l : lops) names.add("Lớp: " + l.getTenLop());
            binding.spinnerClass.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, names));
        });

        viewModel.getMonHocList().observe(this, monHocs -> {
            this.listMonHoc = monHocs;
            List<String> names = new ArrayList<>();
            names.add("--- Tất cả môn ---");
            for (MonHoc m : monHocs) names.add("Môn: " + m.getTenMH());
            binding.spinnerSubject.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, names));
        });

        viewModel.getDiemList().observe(this, list -> {
            adapter.setDiemList(list);
        });
    }

    private void setupClickListeners() {
        binding.btnFilter.setOnClickListener(v -> {
            String maMH = "";
            int subjectPos = binding.spinnerSubject.getSelectedItemPosition();
            if (subjectPos > 0) maMH = listMonHoc.get(subjectPos - 1).getMaMH();

            int hocKy = binding.spinnerSemester.getSelectedItemPosition();
            
            String maLop = "";
            int lopPos = binding.spinnerClass.getSelectedItemPosition();
            if (lopPos > 0) maLop = listLop.get(lopPos - 1).getMaLop();

            viewModel.filter(maMH, hocKy, maLop);
            Toast.makeText(this, "Đã cập nhật danh sách", Toast.LENGTH_SHORT).show();
        });

        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearch.getText().toString();
            if (!query.isEmpty()) viewModel.search(query);
            else viewModel.filter("", 0, "");
        });

        binding.btnUpdateScore.setOnClickListener(v -> updateScore());

        binding.btnExportExcel.setOnClickListener(v -> exportToExcel());
    }

    private void displaySelectedDiem(Diem.Display display) {
        if (selectedDiem != null) {
            binding.etMahsUpdate.setText("Mã HS: " + selectedDiem.getMaHS());
            binding.etHotenUpdate.setText("Học sinh: " + (display.getTenHS() != null ? display.getTenHS() : "---"));
            binding.etDiem15p.setText(selectedDiem.getDiem15p() != null ? String.format(Locale.US, "%.1f", selectedDiem.getDiem15p()) : "");
            binding.etDiem1tiet.setText(selectedDiem.getDiem1Tiet() != null ? String.format(Locale.US, "%.1f", selectedDiem.getDiem1Tiet()) : "");
            binding.etDiemgk.setText(selectedDiem.getDiemGiuaKy() != null ? String.format(Locale.US, "%.1f", selectedDiem.getDiemGiuaKy()) : "");
            binding.etDiemck.setText(selectedDiem.getDiemCuoiKy() != null ? String.format(Locale.US, "%.1f", selectedDiem.getDiemCuoiKy()) : "");
        }
    }

    private void updateScore() {
        if (selectedDiem == null) {
            Toast.makeText(this, "Vui lòng chọn một học sinh", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            selectedDiem.setDiem15p(Double.parseDouble(binding.etDiem15p.getText().toString()));
            selectedDiem.setDiem1Tiet(Double.parseDouble(binding.etDiem1tiet.getText().toString()));
            selectedDiem.setDiemGiuaKy(Double.parseDouble(binding.etDiemgk.getText().toString()));
            selectedDiem.setDiemCuoiKy(Double.parseDouble(binding.etDiemck.getText().toString()));
            selectedDiem.setDiemTongKet(selectedDiem.calculateDiemTongKet());
            
            viewModel.update(selectedDiem);
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            clearForm();
            binding.btnFilter.performClick();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi định dạng điểm!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        selectedDiem = null;
        binding.etMahsUpdate.setText("Mã HS: --");
        binding.etHotenUpdate.setText("Học sinh: --");
        binding.etDiem15p.setText("");
        binding.etDiem1tiet.setText("");
        binding.etDiemgk.setText("");
        binding.etDiemck.setText("");
    }

    private void exportToExcel() {
        ExcelHelper.exportToExcel(this, "BangDiem", "BangDiem",
            new String[]{"Mã HS", "Họ Tên", "Môn", "HK", "15p", "1 Tiết", "Giữa Kỳ", "Cuối Kỳ", "Trung Bình"},
            adapter.getDiemList(), (row, display) -> {
                Diem d = display.getDiem();
                row.createCell(0).setCellValue(d.getMaHS());
                row.createCell(1).setCellValue(display.getTenHS());
                row.createCell(2).setCellValue(display.getTenMH());
                row.createCell(3).setCellValue(d.getHocKy());
                row.createCell(4).setCellValue(d.getDiem15p() != null ? d.getDiem15p() : 0.0);
                row.createCell(5).setCellValue(d.getDiem1Tiet() != null ? d.getDiem1Tiet() : 0.0);
                row.createCell(6).setCellValue(d.getDiemGiuaKy() != null ? d.getDiemGiuaKy() : 0.0);
                row.createCell(7).setCellValue(d.getDiemCuoiKy() != null ? d.getDiemCuoiKy() : 0.0);
                row.createCell(8).setCellValue(d.getDiemTongKet() != null ? d.getDiemTongKet() : 0.0);
            });
    }
}
