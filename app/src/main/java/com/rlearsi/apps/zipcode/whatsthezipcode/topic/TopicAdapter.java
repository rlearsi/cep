package com.rlearsi.apps.zipcode.whatsthezipcode.topic;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rlearsi.apps.zipcode.whatsthezipcode.InterfaceUpdates;
import com.rlearsi.apps.zipcode.whatsthezipcode.R;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicHolder> {

    private List<Topic> topics, topicsCache;
    private Context context;
    private InterfaceUpdates interfaceUpdates;

    public TopicAdapter(List<Topic> topics, Context context, InterfaceUpdates interfaceUpdates) {
        this.topics = topics;
        this.topicsCache = topics;
        this.context = context;
        this.interfaceUpdates = interfaceUpdates;
    }

    @NonNull
    @Override
    public TopicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TopicHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final TopicHolder holder, final int position) {

        final Topic topic = topics.get(position);
        final String cep = topic.getCep();
        final String city = topic.getCidade();
        final String bairro = topic.getBairro();
        final String logradouro = topic.getLogradouro();
        final String uf = topic.getUf();
        final boolean exist = topic.getExist();
        final boolean active = topic.getActive();
        final int toogleSaveAdress;

        final String zipZipped = (cep.length() == 8) ? String.format("%s-%s", cep.substring(0, 5), cep.substring(5, 8)) : cep;

        holder.txtNeighborhood.setText(bairro);
        holder.txtStreet.setText(logradouro);
        holder.txtZipCode.setText(zipZipped);
        holder.txtCityUf.setText(String.format("%s/%s", city, uf));

        holder.btnDownload.setImageResource(R.drawable.ic_download_24);
        holder.btnDownload.setOnClickListener(v -> saveAdress(v, position, cep, uf, city, bairro, logradouro, exist, active));
        // ## FAVORITES ##

        // # SEE MAP #
        //holder.btnGoMap.setOnClickListener(v -> goMap(v, topic));

        if (active) {
            holder.btnDownload.setColorFilter(Color.parseColor("#23a24d"), PorterDuff.Mode.SRC_IN);
            toogleSaveAdress = R.string.zip_remove; // for menu
        } else {
            holder.btnDownload.setColorFilter(Color.parseColor("#8B8B8B"), PorterDuff.Mode.SRC_IN);
            toogleSaveAdress = R.string.zip_save; // for menu
        }

        /////////////////////////////////////
        holder.btnCtxMenu.setOnClickListener(v -> holder.itemView.showContextMenu());

        holder.itemView.setOnLongClickListener(v -> {

            copy(zipZipped, city, uf, logradouro, bairro);

            return true;
        });

        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

            menu.setHeaderTitle(zipZipped);

            menu.add(R.string.see_map).setOnMenuItemClickListener(item -> {

                goMap(v, topic);
                return true;

            });

            menu.add(R.string.zip_tosahre).setOnMenuItemClickListener(item -> {

                share(v, zipZipped, city, uf, logradouro, bairro);
                return true;

            });

            menu.add(R.string.copy).setOnMenuItemClickListener(item -> {

                copy(zipZipped, city, uf, logradouro, bairro);
                return true;

            });

            menu.add(toogleSaveAdress).setOnMenuItemClickListener(item -> {

                saveAdress(v, position, cep, uf, city, bairro, logradouro, exist, active);
                return true;

            });

        });
        //////////////////////////////////////////////
    }

    private void saveAdress(final View v, int pos, String cep, String uf, String cidade, String bairro, String logradouro, boolean exist, boolean active) {

        if (!exist) {

            TopicDAO dao = new TopicDAO(v.getContext());
            if (dao.save(cep, uf, cidade, bairro, logradouro)) {

                toast(R.string.zip_added);

                Topic topic2 = dao.returnTo(cep);
                updateRow(topic2, pos);

                interfaceUpdates.updates(true, topic2);

            }

        } else {

            if (active) {
                TopicDAO dao = new TopicDAO(v.getContext());
                if (dao.inactive(cep)) {

                    toast(R.string.zip_removed);

                    Topic topic2 = dao.returnTo(cep);
                    updateRow(topic2, pos);

                    interfaceUpdates.updates(false, topic2);

                }
            } else {
                TopicDAO dao = new TopicDAO(v.getContext());
                if (dao.reactivate(cep)) {

                    toast(R.string.zip_added);

                    Topic topic2 = dao.returnTo(cep);
                    updateRow(topic2, pos);

                    interfaceUpdates.updates(true, topic2);

                }
            }
        }

    }

    private void toast(int message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        //Snackbar.make(v.getRootView(), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }

    private void copy(String cep, String cidade, String estado, String logradouro, String bairro) {

        String text = logradouro + ", " + bairro + "\n" + cidade + "/" + estado + "\n" + cep;

        ClipboardManager clipeboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData copy = ClipData.newPlainText("", text);
        assert clipeboard != null;
        clipeboard.setPrimaryClip(copy);

        toast(R.string.copied);

    }

    private void share(View v, String cep, String cidade, String estado, String logradouro, String bairro) {

        String body = logradouro + ", " + bairro + "\n" + cidade + "/" + estado + "\n" + cep;

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, (body));
        v.getContext().startActivity(Intent.createChooser(sharingIntent, v.getContext().getString(R.string.share_using)));

    }

    private void goMap(View v, Topic topic) {

        String adress = String.format("%s, %s, %s, %s", topic.getLogradouro(), topic.getBairro(), topic.getCidade(), topic.getUf());

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(adress));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        //mapIntent.setPackage("com.google.android.apps.maps");
        //mapIntent.setPackage("com.ubercab");
        //mapIntent.setPackage("com.waze");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {

            v.getContext().startActivity(mapIntent);

        } else {

            nullMapWarning();

        }

    }

    private void nullMapWarning() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_map_warning_title)
                .setMessage(R.string.app_map_warning)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();

    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    public void addTopic(Topic topic) {

        topics.add(getItemCount(), topic);
        notifyItemInserted(getItemCount());
        //notifyItemRangeChanged(0, getItemCount());

    }

    private void updateRow(Topic topic, int position) {

        try {

            topics.set(position, topic);
            topicsCache.set(position, topic);

            notifyItemChanged(position);
            notifyItemChanged(position);

        } catch (Exception ignored) {
            //Log.i("xcepx", "Erro ao tentar atualizar");
        }
    }

    public void removeRow(Topic topic) {

        try {

            int position = topics.indexOf(topic);
            topics.remove(position);
            notifyItemRemoved(position);

        } catch (Exception ignored) {
            //Log.i("xcepx", "Erro ao tentar remover");
        }
    }

    @SuppressWarnings("unchecked")
    public void filter(CharSequence constraint) {

        FilterResults results = new FilterResults();

        if (constraint == null || constraint.length() == 0) {

            results.values = topicsCache;
            results.count = topicsCache.size();

        } else {
            List<Topic> fRecords = new ArrayList<>();

            for (Topic top : topicsCache) {
                if ((top.getCidade().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) ||
                        (top.getCep().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) ||
                        (top.getLogradouro().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) ||
                        (top.getBairro().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim()))) {
                    fRecords.add(top);
                }
            }
            results.values = fRecords;
            results.count = fRecords.size();
        }

        topics = (ArrayList<Topic>) results.values;
        notifyDataSetChanged();

    }

    private static class FilterResults {
        FilterResults() {
            // nothing to see here
        }

        Object values;

        int count;
    }

}
