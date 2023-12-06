package com.example.as_faunaspyv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ConnectivityReceiver connectivityReceiver;
    private boolean wasConnected = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation();

        // Inicializar el BroadcastReceiver
        connectivityReceiver = new ConnectivityReceiver();

        // Registrar el BroadcastReceiver
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Llamada a la función con tu mensaje y duración
        // Reemplaza "Tu mensaje aquí" y "Snackbar.LENGTH_SHORT" según tus necesidades
    }

    @Override
    protected void onDestroy() {
        // Desregistrar el BroadcastReceiver cuando la actividad se destruye
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
            connectivityReceiver = null;
        }

        super.onDestroy();
    }

    private void navigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment nhf = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, nhf.getNavController());
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private Snackbar showSnackbarWithInternetCheck() {
        View rootView = findViewById(android.R.id.content);
        String message = null;

        if (isNetworkConnected()) {
            if (!wasConnected) {
                wasConnected = true;
                message = "Se restauró la conexión a internet.";
            }
            // Puedes agregar más lógica aquí si es necesario
        } else {
            wasConnected = false;
            message = "En este momento no tienes conexión.";
        }

        if (message != null) {
            return Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        }

        // Si no hay mensaje, devolver una Snackbar vacía
        return null;
    }

    // BroadcastReceiver para detectar cambios en la conectividad
    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                // Verificar el estado de la conectividad al recibir la acción de cambio de conectividad
                Snackbar snackbar = showSnackbarWithInternetCheck();
                if (snackbar != null) {
                    snackbar.show();
                }
            }
        }
    }
}