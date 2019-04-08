package com.treinamento.mdomingos.startapp.activity.investidor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.treinamento.mdomingos.startapp.R;
import com.treinamento.mdomingos.startapp.activity.home.BaseFragmentInvestidor;
import com.treinamento.mdomingos.startapp.model.CEP;
import com.treinamento.mdomingos.startapp.model.Investidor;
import com.treinamento.mdomingos.startapp.model.InvestidorResponse;
import com.treinamento.mdomingos.startapp.utils.FirebaseConfig;
import com.treinamento.mdomingos.startapp.utils.HttpService;
import com.treinamento.mdomingos.startapp.utils.MaskFormatter;
import com.treinamento.mdomingos.startapp.utils.Validator;

import java.util.concurrent.ExecutionException;

public class EditarPerfilInvestidorActivity extends AppCompatActivity {

    private EditText nome, email, empresa, telefone, cep, cpf, cnpj, dataNasc, rua, cidade, bairro, estado;
    private RelativeLayout botaoConcluir;
    private RadioGroup radioGroup;
    private FirebaseAuth firebaseAuth;
    private TextInputLayout inputPf, inputPj;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private ImageView foto, icone;
    private StorageReference mStorage;


    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Usuarios").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InvestidorResponse investidor = dataSnapshot.getValue(InvestidorResponse.class);
                nome.setText(investidor.getDetalhe_investidor().getNome());
                cidade.setText(investidor.getDetalhe_investidor().getCidade());
                empresa.setText(investidor.getDetalhe_investidor().getEmpresa());
                email.setText(investidor.getDetalhe_investidor().getEmail());
                dataNasc.setText(investidor.getDetalhe_investidor().getData());
                rua.setText(investidor.getDetalhe_investidor().getRua());
                bairro.setText(investidor.getDetalhe_investidor().getBairro());
                estado.setText(investidor.getDetalhe_investidor().getEstado());
                telefone.setText(investidor.getDetalhe_investidor().getTelefone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_investidor);

        icone = findViewById(R.id.icone_editar_investidor_id);
        foto = findViewById(R.id.foto_editar_investidor_id);
        nome = findViewById(R.id.nome_editar_investidor_id);
        email = findViewById(R.id.email_editar_investidor_id);
        telefone = findViewById(R.id.telefone_editar_investidor_id);
        empresa = findViewById(R.id.empresa_editar_investidor_id);
        dataNasc = findViewById(R.id.data_nascimento_editar_investidor_id);
        cep = findViewById(R.id.cep_editar_investidor_id);
        cpf = findViewById(R.id.cpf_editar_investidor_id);
        cnpj = findViewById(R.id.cnpj_editar_investidor_id);
        rua = findViewById(R.id.rua_editar_investidor_id);
        cidade = findViewById(R.id.cidade_editar_investidor_id);
        bairro = findViewById(R.id.bairro_editar_investidor_id);
        estado = findViewById(R.id.estado_editar_investidor_id);
        inputPf = findViewById(R.id.textInputPF_editar);
        inputPj = findViewById(R.id.textInputPJ_editar);
        botaoConcluir = findViewById(R.id.botao_concluir_edicao_investidor_id);
        radioGroup = findViewById(R.id.radioGroup_editar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        //Mascaras
        MaskFormatter.simpleFormatterCell(telefone);
        MaskFormatter.simpleFormatterData(dataNasc);
        MaskFormatter.simpleFormatterCpf(cpf);
        MaskFormatter.simpleFormatterCnpj(cnpj);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.pessoaFisica_editar) {

                    cnpj.setVisibility(View.GONE);
                    inputPj.setVisibility(View.GONE);
                    inputPf.setVisibility(View.VISIBLE);
                    cpf.setVisibility(View.VISIBLE);

                } else if (checkedId == R.id.pessoaJuridica_editar) {

                    cnpj.setVisibility(View.VISIBLE);
                    inputPj.setVisibility(View.VISIBLE);
                    inputPf.setVisibility(View.GONE);
                    cpf.setVisibility(View.GONE);

                }
            }
        });



        cep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    CEP retorno = new HttpService(s.toString()).execute().get();
                    if (retorno == null) {
                        cep.setError("Cep inválido");
                    } else {
                        if (retorno.getCidade() != null) {
                            cidade.setText(retorno.getCidade());
                            estado.setText(retorno.getEstado());
                            rua.setText(retorno.getLogradouro());
                            bairro.setText(retorno.getBairro());
                        } else {
                            cep.setError("Cep inválido");
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        botaoConcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseConfig.firebaseConection()) {

                    final String nomeRecebido = nome.getText().toString();
                    final String emailRecebido = email.getText().toString();
                    final String telefoneRecebido = telefone.getText().toString();
                    final String empresaRecebida = empresa.getText().toString();
                    final String dataRecebida = dataNasc.getText().toString();
                    final String cepRecebido = cep.getText().toString();
                    final String ruaRecebida = rua.getText().toString();
                    final String bairroRecebido = bairro.getText().toString();
                    final String cidadeRecebido = cidade.getText().toString();
                    final String estadoRecebido = estado.getText().toString();
                    final String cpfRecebido = cpf.getText().toString();
                    final String cnpjRecebido = cnpj.getText().toString();
                    progressDialog = new ProgressDialog(EditarPerfilInvestidorActivity.this);

                    String cpfSemFormatacao = cpfRecebido.replace(".", "");
                    cpfSemFormatacao = cpfSemFormatacao.replace("-", "");

                    String cnpjSemFormatacao = cnpjRecebido.replace(".", "");
                    cnpjSemFormatacao = cnpjSemFormatacao.replace("/", "");
                    cnpjSemFormatacao = cnpjSemFormatacao.replace("-", "");

                    if (Validator.stringEmpty(nomeRecebido)) {
                        nome.setError("Insira seu nome");

                    } else if (Validator.stringEmpty(emailRecebido)) {
                        email.setError("Insira seu email");

                    } else if (Validator.validateEmailFormat(emailRecebido) ==  false) {
                        email.setError("Insira um email válido");

                    } else if (Validator.stringEmpty(telefoneRecebido)) {
                        telefone.setError("Insira seu numero");

                    } else if (Validator.stringEmpty(empresaRecebida)) {
                        empresa.setError("Insira sua compania");

                    } else if (Validator.stringEmpty(dataRecebida)) {
                        dataNasc.setError("Insira sua data de nascimento");

                    } else if (Validator.stringEmpty(cepRecebido)) {
                        cep.setError("Insira seu cep");

                    } else if (Validator.stringEmpty(ruaRecebida)) {
                        rua.setError("Insira seu logradouro");

                    } else if (Validator.stringEmpty(bairroRecebido)) {
                        bairro.setError("Insira seu bairro");

                    } else if (Validator.stringEmpty(cidadeRecebido)) {
                        cidade.setError("Insira sua cidade");

                    } else if (Validator.stringEmpty(estadoRecebido)) {
                        estado.setError("Insira seu estado");

                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.pessoaFisica_editar) {
                            if (Validator.isCPF(cpfSemFormatacao) == false) {
                                cpf.setError("Insira um CPF válido");
                                return;

                            } else {

                                Investidor investidor = new Investidor(nomeRecebido, emailRecebido, telefoneRecebido, empresaRecebida, dataRecebida, cepRecebido, ruaRecebida, bairroRecebido, cidadeRecebido, estadoRecebido, null, cpfRecebido);
                                investidor.salvarInvestidor(firebaseUser.getUid());
                                progressDialog.setMessage("Salvando dados...");
                                progressDialog.show();
                                Toast.makeText(EditarPerfilInvestidorActivity.this, "Alterado com sucesso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditarPerfilInvestidorActivity.this, BaseFragmentInvestidor.class));
                                finish();

                            }
                        } else if (radioGroup.getCheckedRadioButtonId() == R.id.pessoaJuridica_editar) {
                            if (Validator.isCNPJ(cnpjSemFormatacao) == false) {
                                cnpj.setError("Insira um CNPJ válido");
                            } else {
                                Investidor investidor = new Investidor(nomeRecebido, emailRecebido, telefoneRecebido, empresaRecebida, dataRecebida, cepRecebido, ruaRecebida, bairroRecebido, cidadeRecebido, estadoRecebido, cnpjRecebido, null);
                                investidor.salvarInvestidor(firebaseUser.getUid());
                                progressDialog.setMessage("Salvando dados...");
                                progressDialog.show();
                                Toast.makeText(EditarPerfilInvestidorActivity.this, "Alterado com sucesso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditarPerfilInvestidorActivity.this, BaseFragmentInvestidor.class));
                                finish();
                            }
                        }
                    }

                } else {
                    Log.i("sem internet", "sem conexao");
                    Toast.makeText(EditarPerfilInvestidorActivity.this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
