package com.example.localclash.adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localclash.R;
import com.example.localclash.models.Deuda;

import java.util.List;
import java.util.Locale;

public class DeudaAdapter extends RecyclerView.Adapter<DeudaAdapter.DeudaViewHolder> {

    public interface OnDeudaStatusChangeListener {
        void onStatusChange(Deuda deuda, String nuevoEstado);
    }

    private List<Deuda> deudaList;
    private OnDeudaStatusChangeListener statusListener;

    public DeudaAdapter(List<Deuda> deudaList, OnDeudaStatusChangeListener statusListener) {
        this.deudaList = deudaList;
        this.statusListener = statusListener;
    }

    public void setDeudaList(List<Deuda> nuevaLista) {
        this.deudaList = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deuda, parent, false);
        return new DeudaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeudaViewHolder holder, int position) {
        Deuda d = deudaList.get(position);

        String contacto = TextUtils.isEmpty(d.getContactoNombre()) ? "Contacto General" : d.getContactoNombre();
        holder.txtContacto.setText(contacto);
        
        String detalle = d.getDescripcion() + " • " + d.getFecha();
        holder.txtDescripcionFecha.setText(detalle);

        String montoFormateado = String.format(Locale.getDefault(), "$ %,.0f", d.getMonto());
        holder.txtMonto.setText(montoFormateado);

        boolean esPagado = "Pagado".equalsIgnoreCase(d.getEstado());
        if (esPagado) {
            holder.txtEstado.setText("Pagado");
            holder.txtEstado.setTextColor(Color.parseColor("#2E7D32")); // Verde
            holder.btnCambiarEstado.setText(R.string.btn_marcar_pendiente);
        } else {
            holder.txtEstado.setText("Pendiente");
            holder.txtEstado.setTextColor(Color.parseColor("#D32F2F")); // Rojo
            holder.btnCambiarEstado.setText(R.string.btn_marcar_pagado);
        }

        holder.btnCambiarEstado.setOnClickListener(v -> {
            if (statusListener != null) {
                String nuevoEstado = esPagado ? "Pendiente" : "Pagado";
                statusListener.onStatusChange(d, nuevoEstado);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deudaList != null ? deudaList.size() : 0;
    }

    static class DeudaViewHolder extends RecyclerView.ViewHolder {
        TextView txtContacto;
        TextView txtDescripcionFecha;
        TextView txtMonto;
        TextView txtEstado;
        Button btnCambiarEstado;

        public DeudaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContacto = itemView.findViewById(R.id.txtContactoNombre);
            txtDescripcionFecha = itemView.findViewById(R.id.txtDescripcionFecha);
            txtMonto = itemView.findViewById(R.id.txtMontoDeuda);
            txtEstado = itemView.findViewById(R.id.txtEstadoDeuda);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstado);
        }
    }
}