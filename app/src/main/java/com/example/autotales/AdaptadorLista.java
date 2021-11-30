package com.example.autotales;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class AdaptadorLista extends RecyclerView.Adapter<AdaptadorLista.ViewHolder> {

    private ArrayList<Coche> coches;
    private OnItemSelectedListener itemSelectedListener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblMarca;
        private TextView lblModelo;
        private TextView lblPrecio;
        private ImageView imgCoche;
        private ImageView imgFav;
        ViewHolder(View view) {
            super(view);
            lblMarca = view.findViewById(R.id.lblMarca);
            lblModelo = view.findViewById(R.id.lblModelo);
            lblPrecio = view.findViewById(R.id.lblPrecio);
            imgCoche = view.findViewById(R.id.imgCoche);
            imgFav = view.findViewById(R.id.imgFav);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posicion = getAdapterPosition();
                    if (itemSelectedListener != null) {
                        itemSelectedListener.onCocheSeleccionado(posicion);
                    }
                }
            });

            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.menu_contextual, popup.getMenu());
            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v,
                                                ContextMenu.ContextMenuInfo menuInfo)
                {
                    popup.show();
                }
            });

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener (){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (itemSelectedListener != null) {
                        itemSelectedListener.onMenuContextualCoche(getAdapterPosition(), item);
                    }
                    return true;
                }
            });
        }
    }
    public AdaptadorLista(ArrayList<Coche> coches){
        this.coches = coches;
    }

    @Override
    public AdaptadorLista.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(AdaptadorLista.ViewHolder holder, int position) {
        Coche coche = coches.get(position);
        holder.lblMarca.setText(coches.get(position).getMarca());
        holder.lblModelo.setText(coches.get(position).getModelo());
        holder.lblPrecio.setText(String.valueOf(coches.get(position).getPrecio())+" €");
        switch (coches.get(position).getTipoCombustible())
        {
            case "Diesel": //Cargar imagen de coches diesel
                holder.imgCoche.setImageResource(R.drawable.diesel);
                break;
            case "Gasolina": //Cargar imagen de coches gasolina
                holder.imgCoche.setImageResource(R.drawable.gasolina);
                break;
            case "Híbrido": //Cargar imagen de coches hibridos
                holder.imgCoche.setImageResource(R.drawable.hibrido);
                break;
            case "Eléctrico": //Cargar imagen de coches electricos
                holder.imgCoche.setImageResource(R.drawable.electrico);
                break;
        }
        if(coches.get(position).isFavorito()) holder.imgFav.setVisibility(View.VISIBLE);
    }
    @Override
    public int getItemCount() {
        return coches.size();
    }

    public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }
}
