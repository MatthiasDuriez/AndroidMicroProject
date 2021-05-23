package com.example.chat2021;

public class Message {
    String id;
    String contenu;
    String auteur;
    String couleur;

    public Message(String contenu, String auteur) {
        this.id = "-1";
        this.contenu = contenu;
        this.auteur = auteur;
        this.couleur = "bleu";
    }

    public String getContenu() {
        return contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                ", couleur='" + couleur + '\'' +
                '}';
    }
}
