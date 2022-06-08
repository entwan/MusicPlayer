package com.dam.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.MyViewHolder> {

    //1 Vars
    private Context context;
    private ArrayList<ModelSong> songArrayList;

    // 2 Constructeurs
    public AdapterSong(Context context, ArrayList<ModelSong> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    // La gestion du click
    public OnItemClickListener onItemClickListener;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3 Creation du Layout inflater
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 4 Remplissage du DataBinding
        holder.title.setText(songArrayList.get(position).getSongTitle());
        holder.artist.setText(songArrayList.get(position).getSongArtist());

        // Gestion de l'image
        Uri imgUri = songArrayList.get(position).getSongCover();

        /* utilisation de Glide pour la gestion des images */
        Context context = holder.cover.getContext();

        //methode ultra basique
//        Glide.with(context)
//            // on load l'image depuis le chemin vers le dossier de stockage des covers
//            .load(imgUri)
//            .into(holder.cover);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_note_24_w)
                .placeholder(R.drawable.ic_note_24_w);

        // methode normale
        Glide.with(context)
                .load(imgUri)
                .apply(options)
                .fitCenter()
                .override(150,150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cover);

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView cover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            artist = itemView.findViewById(R.id.tvArtist);
            cover = itemView.findViewById(R.id.ivCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
