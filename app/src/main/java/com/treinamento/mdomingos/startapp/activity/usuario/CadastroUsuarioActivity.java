package com.treinamento.mdomingos.startapp.activity.usuario;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ms_square.etsyblur.BlurSupport;
import com.treinamento.mdomingos.startapp.R;
import com.treinamento.mdomingos.startapp.activity.others.EnviandoEmailActivity;
import com.treinamento.mdomingos.startapp.activity.others.TermosActivity;
import com.treinamento.mdomingos.startapp.utils.Validator;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private RelativeLayout botaoCadastrar, botaoTwitter, botaoFacebook;
    private EditText nome, email, senha, confirmaSenha;
    private CheckBox checkBox;
    private TextView termosDeUso;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        //Instance
        botaoCadastrar = findViewById(R.id.botao_cadastrar_id);
        botaoTwitter = findViewById(R.id.botao_conecta_twitter_id);
        botaoFacebook = findViewById(R.id.botao__conecta_facebook_id);
        nome = findViewById(R.id.nome_cadastro_usuario_id);
        email = findViewById(R.id.email_cadastro_usuario_id);
        senha = findViewById(R.id.senha_cadastro_usuario_id);
        confirmaSenha = findViewById(R.id.comfirma_senha_cadastro_usuario_id);
        checkBox = findViewById(R.id.checkBox_termos_id);
        termosDeUso = findViewById(R.id.termos_text_id);
        progressDialog = new ProgressDialog(CadastroUsuarioActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        //functions
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nomeRecebido = nome.getText().toString();
                final String emailRecebido = email.getText().toString();
                final String senhaRecebida = senha.getText().toString();
                final String confirmaSenhaRecebido = confirmaSenha.getText().toString();

                if(FirebaseConection()){

                    //Criando Usuario

                    if (Validator.stringEmpty(nomeRecebido)) {
                        nome.setError("Insira seu nome");

                    } else if (Validator.stringEmpty(emailRecebido)) {
                        email.setError("Insira seu email");

                    } else if (Validator.validateEmailFormat(emailRecebido) == false) {
                        email.setError("Insira um email válido");

                    } else if (Validator.validaSenha(senhaRecebida) == false) {
                        senha.setError("Senha muito curta");

                    } else if (Validator.stringEmpty(confirmaSenhaRecebido)) {
                        confirmaSenha.setError("Confirme sua senha");

                    } else if (checkBox.isChecked() == false) {

                        new AlertDialog.Builder(CadastroUsuarioActivity.this).setTitle("Aviso").
                                setMessage("Leia e aceite os Termos de uso.").setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}}).show();

                    } else if (confirmaSenhaRecebido.equals(senhaRecebida)) {

                        progressDialog.setMessage("Estamos criando sua conta...");
                        progressDialog.show();

                        firebaseAuth.createUserWithEmailAndPassword(emailRecebido, senhaRecebida).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user != null){
                                        Log.i("createUser", "Cadastrado com sucesso!!!");
                                        Intent intent = new Intent(CadastroUsuarioActivity.this, EnviandoEmailActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                } else {
                                    Log.i("createUser", "Falha ao cadastrar!!!");
                                    Toast.makeText(CadastroUsuarioActivity.this, "Falha ao cadastrar", Toast.LENGTH_SHORT).show();
                                }

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 500);
                            }
                        });

                    } else {
                        Toast.makeText(CadastroUsuarioActivity.this, "A senha não confere", Toast.LENGTH_SHORT).show();
                    }

                } else { // sem conexao
                    Log.i("sem internet", "sem conexao");
                    Toast.makeText(CadastroUsuarioActivity.this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        termosDeUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroUsuarioActivity.this, TermosActivity.class);
                startActivity(intent);
            }
        });

        botaoFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        botaoTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public boolean FirebaseConection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) // conectado a internet
            return true;
        return false; // nao conectado
    }


}
