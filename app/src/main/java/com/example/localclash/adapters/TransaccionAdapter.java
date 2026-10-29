package com.example.localclash.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localclash.R;
import com.example.localclash.models.Transaccion;

import java.util.List;
import java.util.Locale;

public class TransaccionAdapter extends RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder> {

    private List<Transaccion> transaccionList;

    public TransaccionAdapter(List<Transaccion> transaccionList) {
        this.transaccionList = transaccionList;
    }

    public void setTransaccionList(List<Transaccion> nuevaLista) {
        this.transaccionList = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransaccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaccion, parent, false);
        return new TransaccionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaccionViewHolder holder, int position) {
        Transaccion t = transaccionList.get(position);

        holder.txtDescripcion.setText(t.getDescripcion());
        holder.txtFecha.setText(t.getFecha() + " • " + t.getTipo());

        boolean esIngreso = "Ingreso".equalsIgnoreCase(t.getTipo());
        String signo = esIngreso ? "+" : "-";
        String montoFormateado = String.format(Locale.getDefault(), "%s $ %,.0f", signo, t.getMonto());
        
        holder.txtMonto.setText(montoFormateado);

        if (esIngreso) {
            holder.txtMonto.setTextColor(Color.parseColor("#2E7D32")); // Verde
            holder.imgIcon.setColorFilter(Color.parseColor("#2E7D32"));
        } else {
            holder.txtMonto.setTextColor(Color.parseColor("#D32F2F")); // Rojo
            holder.imgIcon.setColorFilter(Color.parseColor("#D32F2F"));
        }
    }

    @Override
    public int getItemCount() {
        return transaccionList != null ? transaccionList.size() : 0;
    }

    static class TransaccionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtDescripcion;
        TextView txtFecha;
        TextView txtMonto;

        public TransaccionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgTipoIcon);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtFecha = itemView.findViewById(R.id.txtFechaDetalle);
            txtMonto = itemView.findViewById(R.id.txtMonto);
        }
    }
}