package com.mgovindappa.qod.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mgovindappa.qod.AsyncCallbacks;
import com.mgovindappa.qod.AsyncDownloader;
import com.mgovindappa.qod.R;
import com.mgovindappa.qod.constant.Extras;
import com.mgovindappa.qod.model.QOD;

import java.util.HashMap;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragEmailQuote extends Fragment implements AsyncCallbacks {
    private final String CATEGORIES_URL = "http://quotes.rest/qod/categories.json";
    private final String QOD_URL = "http://quotes.rest/qod.json?category=";

    // Holds categories
    private HashMap<String, String> categories = new HashMap<>();

    // Handle for button control
    private Button sendQuote;

    // show progress dialog so that the user is aware that we are running an async task
    private ProgressDialog progressDialog;

    public FragEmailQuote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.content_frag_email_quote, container, false);

        sendQuote = (Button) layout.findViewById(R.id.buttonSend);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait while we select a Quote...");

        sendQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableButton(sendQuote, false);
                progressDialog.show();
                fetchJSONFeed();
            }
        });

        return layout;
    }

    private void fetchJSONFeed() {

        AsyncDownloader<HashMap<String, String>> asyncDownloader = new AsyncDownloader<HashMap<String, String>>(getActivity(), this);

        asyncDownloader.execute(CATEGORIES_URL, Extras.CATEGORIES);

    }

    @Override
    public void onPostExecuteCategories(HashMap<String, String> result) {
        categories = result;

        if (categories != null && categories.size() > 0) {

            // check if the server returned an error message due to exceeding number of allowed retrievals
            if (categories.get("error")!= null && !categories.get("error").isEmpty()) {
                progressDialog.cancel();
                enableButton(sendQuote, true);

                // show a alert dialog
                showErrorDialog(categories.get("error"));

            } else {
                // we got a valid response now email the quote after fetching QOD
                emailQuote(randomNum(categories.size()));
            }
        } else {
            progressDialog.cancel();
            enableButton(sendQuote, true);
        }
    }

    @Override
    public void onPostExecuteQOD(QOD qod) {
        QOD quoteOfDay = qod;
        enableButton(sendQuote, true);
        progressDialog.cancel();

        // we should have something to email hence validate
        if (!quoteOfDay.getQuote().isEmpty() || !quoteOfDay.getTitle().isEmpty() || !quoteOfDay.getAuthor().isEmpty()) {
            enableButton(sendQuote, true);
            sendAnEmail(quoteOfDay);
        }
    }

    // Fetch QOD message, title and author
    private void emailQuote(int index) {
        String urlQOD = QOD_URL + (categories.keySet().toArray())[index];

        AsyncDownloader<QOD> asyncDownloader = new AsyncDownloader<QOD>(getActivity(), this);
        asyncDownloader.execute(urlQOD, Extras.QOD);

        enableButton(sendQuote, true);
    }

    // returns a random number
    private int randomNum(int max) {
        Random rand = new Random();
        return rand.nextInt(max);
    }

    // launch the email app to send the QOD
    private void sendAnEmail(QOD quote) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, quote.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT   , quote.getQuote() + "\n" + "-" + quote.getAuthor());

        try {
            startActivity(Intent.createChooser(intent, "email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Enable / Disable button
    private void enableButton(final Button button, final boolean enable) {
        button.setEnabled(enable);
        button.setClickable(enable);
    }

    // Show error dialog
    private void showErrorDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog
                .setTitle("Error")
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}



