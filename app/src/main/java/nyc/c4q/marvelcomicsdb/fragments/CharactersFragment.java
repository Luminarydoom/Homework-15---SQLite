package nyc.c4q.marvelcomicsdb.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import nyc.c4q.marvelcomicsdb.API.MarvelDBService;
import nyc.c4q.marvelcomicsdb.R;
import nyc.c4q.marvelcomicsdb.Utils.PrivateAPI;
import nyc.c4q.marvelcomicsdb.model.character.Character;
import nyc.c4q.marvelcomicsdb.model.character.CharacterDataWrapper;
import nyc.c4q.marvelcomicsdb.service.MarvelDatabaseServiceGenerator;
import nyc.c4q.marvelcomicsdb.controller.CharacterAdapter;
import nyc.c4q.marvelcomicsdb.service.RealmServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharactersFragment extends Fragment {

  public static final MarvelDBService marvelCallback = MarvelDatabaseServiceGenerator
      .createService();
  private static final String API_KEY = "b50c206319ac5359d379de4d56395a7a";
  private static final Long TIMESTAMP = new Date().getTime();
  private static String privateAPI = PrivateAPI.getPrivateApiKey();

  private String name;
  private String nameStartsWith;
  private Date modifiedSince;
  private int comics;
  private int series;
  private int events;
  private int stories;
  private String orderBy;
  private int limit;
  private int offset;

  private TextView textView;
  private View rootView;
  private RecyclerView recyclerView;
  private List<Character> characterList = new ArrayList<>();
  CharacterAdapter characterAdapter;

  public CharactersFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_characters, container, false);
    textView = rootView.findViewById(R.id.attributionText_character);
    recyclerView = rootView.findViewById(R.id.character_rv);

    if (getActivity().getApplication().getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT) {
      recyclerView
          .setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
    } else {
      recyclerView
          .setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3));
    }

    characterAdapter = new CharacterAdapter(characterList);

    try {
      getCharacterData();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return rootView;
  }

  public void getCharacterData() throws NoSuchAlgorithmException {
    String hash = md5(TIMESTAMP.toString() + privateAPI + API_KEY);

    Call<CharacterDataWrapper> call = marvelCallback
        .getCharactersDiscover(TIMESTAMP.toString(), API_KEY, hash);
    call.enqueue(new Callback<CharacterDataWrapper>() {
      @Override
      public void onResponse(Call<CharacterDataWrapper> call,
          Response<CharacterDataWrapper> response) {
        List<Character> responseList = response.body().getData().getResults();
        recyclerView.setAdapter(new CharacterAdapter(responseList));
        Log.d("CHARACTER CALLBACK", "onSuccess: " + response.body().getEtag());
      }

      @Override
      public void onFailure(Call<CharacterDataWrapper> call, Throwable t) {
        Log.d("CHARACTER CALLBACK", "onFailure: " + t.toString());
      }
    });
  }

  public String md5(String s) {
    try {
      final MessageDigest digest = MessageDigest.getInstance("md5");
      digest.update(s.getBytes());
      final byte[] bytes = digest.digest();
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(String.format("%02X", bytes[i]));
      }
      return sb.toString().toLowerCase();
    } catch (Exception exc) {
      return "";
    }
  }
}
