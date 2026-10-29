package com.example.localclash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localclash.R;
import com.example.localclash.models.Contacto;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    public interface OnContactoDeleteListener {
        void onDeleteClick(Contacto contacto);
    }

    private List<Contacto> contactoList;
    private OnContactoDeleteListener deleteListener;

    public ContactoAdapter(List<Contacto> contactoList, OnContactoDeleteListener deleteListener) {
        this.contactoList = contactoList;
        this.deleteListener = deleteListener;
    }

    public void setContactoList(List<Contacto> nuevaLista) {
        this.contactoList = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto c = contactoList.get(position);
        holder.txtNombre.setText(c.getNombre());
        
        String detalle = c.getTelefono();
        if (c.getEmail() != null && !c.getEmail().isEmpty()) {
            detalle += " • " + c.getEmail();
        }
        holder.txtDetalle.setText(detalle);

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactoList != null ? contactoList.size() : 0;
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtDetalle;
        ImageButton btnDelete;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreContacto);
            txtDetalle = itemView.findViewById(R.id.txtDetalleContacto);
            btnDelete = itemView.findViewById(R.id.btnDeleteContacto);
        }
    }
}