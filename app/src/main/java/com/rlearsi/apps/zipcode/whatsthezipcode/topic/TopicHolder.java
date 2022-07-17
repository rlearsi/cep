package com.rlearsi.apps.zipcode.whatsthezipcode.topic;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.rlearsi.apps.zipcode.whatsthezipcode.R;

class TopicHolder extends RecyclerView.ViewHolder {

    TextView txtStreet, txtCityUf, txtNeighborhood, txtZipCode;
    ImageButton btnCtxMenu, btnDownload;

    TopicHolder(View itemView) {
        super(itemView);
        txtStreet = itemView.findViewById(R.id.txtStreet);
        txtCityUf = itemView.findViewById(R.id.txtCityUf);
        txtNeighborhood = itemView.findViewById(R.id.txtNeighborhood);
        txtZipCode = itemView.findViewById(R.id.txtZipCode);
        btnDownload = itemView.findViewById(R.id.btnDownload);
        btnCtxMenu = itemView.findViewById(R.id.btnCtxMenu);
    }

}