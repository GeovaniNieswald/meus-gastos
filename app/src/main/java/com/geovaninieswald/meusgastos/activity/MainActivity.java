package com.geovaninieswald.meusgastos.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.Usuario;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imagem;
    private TextView nome, email;

    private Usuario usuario;
    private SharedFirebasePreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferencias = new SharedFirebasePreferences(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        imagem = headerView.findViewById(R.id.imagemID);
        nome = headerView.findViewById(R.id.nomeID);
        email = headerView.findViewById(R.id.emailID);

        usuario = preferencias.usuarioLogado();

        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());

        if (usuario.getImagem() != null) {
            Bitmap bm = BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/imagem.jpg");
            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), bm);
            rbd.setCircular(true);
            imagem.setBackground(rbd);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // ADICIONAR OPÇÃO SINCRONIZAR

        switch (item.getItemId()) {
            case R.id.nav_transacoes:
                break;
            case R.id.nav_categorias:
                break;
            case R.id.nav_rel_redimentos:
                break;
            case R.id.nav_rel_gastos:
                break;
            case R.id.nav_rel_calendario:
                break;
            case R.id.nav_compartilhar:
                break;
            case R.id.nav_avaliar:
                break;
            case R.id.nav_sobre:
                break;
            case R.id.nav_sair:
                // VERIFICAR SE ESTÁ SINCRONIZADO, CASO NÃO AVISAR QUE O USUÁRIO PERDERÁ DADOS, CONFIRMAÇÃO EM AMBOS OS CASOS

                preferencias.sair();
                ConexaoFirebase.sair();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
