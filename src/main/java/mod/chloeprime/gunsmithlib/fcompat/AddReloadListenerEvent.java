package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.ArrayList;
import java.util.List;

public class AddReloadListenerEvent extends Event {
    private final List<PreparableReloadListener> listeners = new ArrayList<>();

    public void addListener(PreparableReloadListener listener) {
        listeners.add(listener);
    }

    public List<PreparableReloadListener> getListeners() {
        return listeners;
    }
}
