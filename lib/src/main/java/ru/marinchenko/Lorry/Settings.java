package ru.marinchenko.lorry;

import ru.marinchenko.lorry.util.SearchStatus;
import static ru.marinchenko.lorry.util.SearchStatus.*;


public class Settings {

    private int update = 15;

    private SearchStatus searchStatus = PERMANENT;
    private boolean autoConnect = false;
    private boolean allowNotes = false;

    public Settings(){}

    public void setSearch(SearchStatus status) { searchStatus = status; }

    public void setUpdate(int timer){ update = timer; }

    public void setAutoConnect(boolean connect){ autoConnect = connect; }

    public void setAllowNotes(boolean allow){ allowNotes = allow; }

    public SearchStatus getSearchStatus(){ return searchStatus; }

    public int getUpdate(){ return update; }

    public boolean isAutoConnect(){ return autoConnect; }

    public boolean isAllowNotes(){ return allowNotes; }
}
