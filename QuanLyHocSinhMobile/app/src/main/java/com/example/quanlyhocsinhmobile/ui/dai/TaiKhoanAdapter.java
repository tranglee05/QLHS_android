package com.example.quanlyhocsinhmobile.ui.dai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyhocsinhmobile.R;
import com.example.quanlyhocsinhmobile.data.local.Model.TaiKhoan;

import java.util.List;

public class TaiKhoanAdapter extends RecyclerView.Adapter<TaiKhoanAdapter.ViewHolder> {

    private List<TaiKhoan> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TaiKhoan taiKhoan);
    }

    public TaiKhoanAdapter(List<TaiKhoan> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    // Cập nhật dữ liệu
    public void setList(List<TaiKhoan> list) {
        this.list = list;
        notifyDataSetChanged(); // đơn giản, nếu muốn tối ưu có thể dùng DiffUtil
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dai_item_taikhoan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // ⚠️ chống crash nếu list null
        if (list == null || position >= list.size()) return;

        TaiKhoan taiKhoan = list.get(position);

        // Username
        holder.tvUsername.setText(
                taiKhoan.getTenDangNhap() != null ? taiKhoan.getTenDangNhap() : "--"
        );

        // Password (có thể ẩn)
        if (taiKhoan.getMatKhau() != null && !taiKhoan.getMatKhau().isEmpty()) {
            holder.tvPassword.setText(taiKhoan.getMatKhau());
            // 👉 nếu muốn ẩn thì dùng:
            // holder.tvPassword.setText("******");
        } else {
            holder.tvPassword.setText("--");
        }

        // Quyền
        holder.tvQuyen.setText(
                taiKhoan.getQuyen() != null ? taiKhoan.getQuyen() : "--"
        );

        // Mã người dùng
        String maND = taiKhoan.getMaNguoiDung();
        holder.tvMaND.setText(
                (maND != null && !maND.isEmpty()) ? maND : "--"
        );

        // Click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(taiKhoan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvPassword, tvQuyen, tvMaND;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tv_item_username);
            tvPassword = itemView.findViewById(R.id.tv_item_password); // 🔥 phải có trong XML
            tvQuyen = itemView.findViewById(R.id.tv_item_quyen);
            tvMaND = itemView.findViewById(R.id.tv_item_ma_nd);
        }
    }
}