package com.yaar.shortvideoapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;

import java.util.ArrayList;

public class LanguageActivity extends BaseActivity {

    RecyclerView recyclerView;
    ArrayList<LanguageModel> languageModelArrayList = new ArrayList<>();
    boolean isSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        isSettings = getIntent().getBooleanExtra("isSettings", false);
        languageModelArrayList.add(new LanguageModel("हिंदी", "Hindi", "hi", R.drawable.l1));
        languageModelArrayList.add(new LanguageModel("English", "English", "en", R.drawable.l2));
        languageModelArrayList.add(new LanguageModel("தமிழ்", "Tamil", "ta", R.drawable.l3));
        languageModelArrayList.add(new LanguageModel("বাংলা", "Bengali", "bn", R.drawable.l4));
        languageModelArrayList.add(new LanguageModel("తెలుగు", "Telugu", "te", R.drawable.l5));
        languageModelArrayList.add(new LanguageModel("ಕನ್ನಡ", "Kannada", "kn", R.drawable.l6));
        languageModelArrayList.add(new LanguageModel("മലയാളം", "Malayalam", "ml", R.drawable.l7));
        languageModelArrayList.add(new LanguageModel("मराठी", "Marathi", "mr", R.drawable.l8));
        languageModelArrayList.add(new LanguageModel("ਪੰਜਾਬੀ", "Punjabi", "pa", R.drawable.l9));
        languageModelArrayList.add(new LanguageModel("ગુજરાતી", "Gujarati", "gu", R.drawable.l10));
        languageModelArrayList.add(new LanguageModel("ગુજરાતી", "Bhojpuri", "bho", R.drawable.l10));
        languageModelArrayList.add(new LanguageModel("ગુજરાતી", "Rajasthani", "bho", R.drawable.l10));
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new Adapter());
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

            holder.txt_language1.setText(languageModelArrayList.get(position).language);
            holder.txt_language2.setText(languageModelArrayList.get(position).languageInEnglish);
            holder.img_icon.setImageDrawable(getDrawable(languageModelArrayList.get(position).icon));

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LanguageUtils.selectLanguage(LanguageActivity.this.getApplicationContext(), languageModelArrayList.get(position).languageInEnglish, languageModelArrayList.get(position).languageCode);

                    if (!isSettings) {
                        Intent intent = new Intent(LanguageActivity.this, MainMenuActivity.class);

                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                            setIntent(null);
                        }

                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Splash_A.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return languageModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_icon;
            TextView txt_language1;
            TextView txt_language2;
            View card;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                card = itemView.findViewById(R.id.card);
                img_icon = itemView.findViewById(R.id.img_icon);
                txt_language1 = itemView.findViewById(R.id.txt_language1);
                txt_language2 = itemView.findViewById(R.id.txt_language2);
            }
        }
    }
}