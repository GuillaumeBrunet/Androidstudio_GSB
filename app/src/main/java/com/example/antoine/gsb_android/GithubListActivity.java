package com.example.antoine.gsb_android;

/**
 * Created by julien on 21/04/2017.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public final class GithubListActivity extends ListActivity {

    private static final String GITHUB_JSON_URL = "https://api.github.com/orgs/LSWSIO/members";
    private RequestQueue mRequestQueue;
    private com.example.antoine.gsb_android.GithubListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On récupère notre RequestQueue et notre ImageLoader depuis notre objet XebiaApplication
        com.example.antoine.gsb_android.XebiaApplication app = (com.example.antoine.gsb_android.XebiaApplication) getApplication();
        mRequestQueue = app.getVolleyRequestQueue();
        ImageLoader imageLoader = app.getVolleyImageLoader();

        mAdapter = new com.example.antoine.gsb_android.GithubListAdapter(app, imageLoader);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // On va créer une Request pour Volley.
        // JsonArrayRequest hérite de Request et transforme automatiquement les données reçues en un JSONArray
        JsonArrayRequest request = new JsonArrayRequest(GITHUB_JSON_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        // Ce code est appelé quand la requête réussi. Étant ici dans le thread principal, on va pouvoir mettre à jour notre Adapter
                        mAdapter.updateMembers(jsonArray);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Le code suivant est appelé lorsque Volley n'a pas réussi à récupérer le résultat de la requête
                Toast.makeText(GithubListActivity.this, "Error while getting JSON: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(this);

        // On ajoute la Request au RequestQueue pour la lancer
        mRequestQueue.add(request);
    }

    @Override
    protected void onStop() {
        mRequestQueue.cancelAll(this);
        super.onStop();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Lorsque l'on clique sur un élément de la liste, cela lancera l'URL du compte GitHub de l'utilisateur sélectionné.
        JSONObject item = mAdapter.getItem(position);
        String url = item.optString("html_url");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}