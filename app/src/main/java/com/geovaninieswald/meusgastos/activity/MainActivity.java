package com.geovaninieswald.meusgastos.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.Usuario;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imagem;
    private TextView nome, email;
    private FloatingActionMenu famMenu;
    private com.github.clans.fab.FloatingActionButton fabReceita, fabGasto;

    private Usuario usuario;
    private SharedFirebasePreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferencias = new SharedFirebasePreferences(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);

        famMenu = findViewById(R.id.famMenuID);
        fabReceita = findViewById(R.id.fabRendimentoID);
        fabGasto = findViewById(R.id.fabGastoID);

        fabGasto.setOnClickListener(clFamMenu);
        fabReceita.setOnClickListener(clFamMenu);

        DrawerLayout drawer = findViewById(R.id.drawerLayoutID);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navViewID);
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

    private View.OnClickListener clFamMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            famMenu.close(true);

            switch (v.getId()) {
                case R.id.fabGastoID:
                    startActivity(new Intent(MainActivity.this, AddTransacaoActivity.class).putExtra("gasto", true));
                    break;
                case R.id.fabRendimentoID:
                    startActivity(new Intent(MainActivity.this, AddTransacaoActivity.class).putExtra("gasto", false));
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayoutID);
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
            case R.id.nav_home:
                break;
            case R.id.nav_transacoes:
                startActivity(new Intent(MainActivity.this, TransacaoActivity.class));
                break;
            case R.id.nav_categorias:
                startActivity(new Intent(MainActivity.this, CategoriaActivity.class));
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
                // VERIFICAR SE ESTÁ SINCRONIZADO, CASO NÃO, AVISAR QUE O USUÁRIO PERDERÁ DADOS, CONFIRMAÇÃO EM AMBOS OS CASOS

                preferencias.sair();
                ConexaoFirebase.sair();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayoutID);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
