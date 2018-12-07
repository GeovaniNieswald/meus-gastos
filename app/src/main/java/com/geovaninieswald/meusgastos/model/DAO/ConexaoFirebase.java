package com.geovaninieswald.meusgastos.model.DAO;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConexaoFirebase {

    private static DatabaseReference referenciaDB;
    private static StorageReference referenciaST;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getDBReference(String path) {
        if (referenciaDB == null)
            referenciaDB = FirebaseDatabase.getInstance().getReference(path);

        return referenciaDB;
    }

    public static StorageReference getSTReference(String path) {
        if (referenciaST == null)
            referenciaST = FirebaseStorage.getInstance().getReference(path);

        return referenciaST;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (autenticacao == null)
            autenticacao = FirebaseAuth.getInstance();

        return autenticacao;
    }

    public static void sair() {
        try {
            autenticacao.signOut();
        } catch (Exception e) {
        }
    }
}
