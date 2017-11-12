package com.mgovindappa.qod.model;

/**
 * Created by Manjesh on 11/11/2017.
 */

public class QOD {

    private String title;
    private String quote;
    private String author;

    public QOD(String title, String quote, String author) {
        this.title = title;
        this.quote = quote;
        this.author = author;
    }

    public String getAuthor() {

        return author;
    }

    public String getQuote() {

        return quote;
    }

    public String getTitle() {

        return title;
    }
}
