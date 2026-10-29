package com.example.localclash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localclash.R;
import com.example.localclash.models.Cuenta;

import java.util.List;
import java.util.Locale;

public class CuentaAdapter extends RecyclerView.Adapter<CuentaAdapter.CuentaViewHolder> {

    private List<Cuenta> cuentaList;

    public CuentaAdapter(List<Cuenta> cuentaList) {
        this.cuentaList = cuentaList;
    }

    public void setCuentaList(List<Cuenta> nuevaLista) {
        this.cuentaList = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CuentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuenta, parent, false);
        return new CuentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuentaViewHolder holder, int position) {
        Cuenta cuenta = cuentaList.get(position);
        holder.txtNombre.setText(cuenta.getNombre());
        holder.txtTipo.setText(cuenta.getTipo());
        
        String saldoFormateado = String.format(Locale.getDefault(), "$ %,.0f", cuenta.getSaldo());
        holder.txtSaldo.setText(saldoFormateado);
    }

    @Override
    public int getItemCount() {
        return cuentaList != null ? cuentaList.size() : 0;
    }

    static class CuentaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtTipo;
        TextView txtSaldo;

        public CuentaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCuenta);
            txtTipo = itemView.findViewById(R.id.txtTipoCuenta);
            txtSaldo = itemView.findViewById(R.id.txtSaldoCuenta);
        }
    }
}