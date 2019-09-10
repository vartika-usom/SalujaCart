package com.usomandroidproject.salujaecommerce;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;

public class PolicyActivity extends AppCompatActivity {
CardView policy1, policy2, policy3, policy4, policy5, policy6, policy7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("POLICY");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        policy1 = (CardView)findViewById(R.id.policy1);
        policy2 = (CardView)findViewById(R.id.policy2);
        policy3 = (CardView)findViewById(R.id.policy3);
        policy4 = (CardView)findViewById(R.id.policy4);
        policy5 = (CardView)findViewById(R.id.policy5);
        policy6 = (CardView)findViewById(R.id.policy6);
        policy7 = (CardView)findViewById(R.id.policy7);

        policy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/term-and-condition.html"));
                startActivity(browseIntent);
            }
        });

        policy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/Disclaimer.html"));
                startActivity(browseIntent);
            }
        });
        policy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/privacy.html"));
                startActivity(browseIntent);
            }
        });
        policy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/payment.html"));
                startActivity(browseIntent);
            }
        });
        policy5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/replacement.html"));
                startActivity(browseIntent);
            }
        });
        policy6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/disputes.html"));
                startActivity(browseIntent);
            }
        });
        policy7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://salujacart.usom.co.in/privacy-policy/buyer.html"));
                startActivity(browseIntent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }
}
