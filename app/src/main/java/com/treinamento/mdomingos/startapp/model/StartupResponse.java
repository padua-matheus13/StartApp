package com.treinamento.mdomingos.startapp.model;

import java.io.Serializable;

/**
 * Created by Matheus de Padua on 05/04/2019.
 * mdomingos@luxfacta.com
 * For Luxfacta Soluções de TI
 * {@see more in https://www.luxfacta.com}
 */
public class StartupResponse implements Serializable{
    private String id;
    private int perfil;
    private String email;
    private Startup detalhe_startup;
    private Startup progresso_startup;
    private String foto_perfil;


    public StartupResponse() {
    }

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public void setFoto_perfil(String foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPerfil() {
        return perfil;
    }

    public void setPerfil(int perfil) {
        this.perfil = perfil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Startup getDetalhe_startup() {
        return detalhe_startup;
    }

    public void setDetalhe_startup(Startup detalhe_startup) {
        this.detalhe_startup = detalhe_startup;
    }

    public Startup getProgresso_startup() {
        return progresso_startup;
    }

    public void setProgresso_startup(Startup progresso_startup) {
        this.progresso_startup = progresso_startup;
    }
}
