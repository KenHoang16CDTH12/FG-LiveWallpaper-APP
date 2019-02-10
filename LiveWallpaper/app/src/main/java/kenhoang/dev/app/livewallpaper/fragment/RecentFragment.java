package kenhoang.dev.app.livewallpaper.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kenhoang.dev.app.livewallpaper.R;
import kenhoang.dev.app.livewallpaper.adapter.RecentAdapter;
import kenhoang.dev.app.livewallpaper.database.Recents;
import kenhoang.dev.app.livewallpaper.database.local.LocalDatabase;
import kenhoang.dev.app.livewallpaper.database.local.RecentsDataSource;
import kenhoang.dev.app.livewallpaper.database.source.RecentsRepository;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentFragment extends Fragment {

    private static RecentFragment INSTANCE = null;

    List<Recents> listRecents;
    RecentAdapter adapter;

    RecyclerView recyclerView;

    // RoomDatabase
    CompositeDisposable compositeDisposable;
    RecentsRepository recentsRepository;

    Context context;

    public static RecentFragment getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new RecentFragment(context);
        return INSTANCE;
    }

    public RecentFragment(Context context) {
        this.context = context;
        // Required empty public constructor
        // Init RoomDatabase
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(context);
        recentsRepository = RecentsRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        recyclerView = view.findViewById(R.id.recycler_recent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        listRecents = new ArrayList<>();
        adapter = new RecentAdapter(getContext(), listRecents);
        recyclerView.setAdapter(adapter);

        fetchRecents();

        return view;
    }

    private void fetchRecents() {
        Disposable disposable = recentsRepository.getAllRecents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Recents>>() {
                    @Override
                    public void accept(List<Recents> recents) throws Exception {
                        onGetAllRecentsSuccess(recents);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("ERROR", "accept: " + throwable.getMessage());
                    }
                });

        compositeDisposable.add(disposable);
    }

    private void onGetAllRecentsSuccess(List<Recents> recents) {
        listRecents.clear();
        listRecents.addAll(recents);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
