package kenhoang.dev.app.livewallpaper.database.local;

import java.util.List;

import io.reactivex.Flowable;
import kenhoang.dev.app.livewallpaper.database.Recents;
import kenhoang.dev.app.livewallpaper.database.source.IRecentsDataSource;

public class RecentsDataSource implements IRecentsDataSource {

    private RecentsDAO recentsDAO;
    private static RecentsDataSource instance;

    public RecentsDataSource(RecentsDAO recentsDAO) {
        this.recentsDAO = recentsDAO;
    }

    public static RecentsDataSource getInstance(RecentsDAO recentsDAO) {
        if (instance == null)
            instance = new RecentsDataSource(recentsDAO);
        return instance;
    }

    @Override
    public Flowable<List<Recents>> getAllRecents() {
        return recentsDAO.getAllRecents();
    }

    @Override
    public void insertRecents(Recents... recents) {
        recentsDAO.insertRecents(recents);
    }

    @Override
    public void updateRecents(Recents... recents) {
        recentsDAO.updateRecents(recents);
    }

    @Override
    public void deleteRecents(Recents... recents) {
        recentsDAO.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        recentsDAO.deleteAllRecents();
    }
}
