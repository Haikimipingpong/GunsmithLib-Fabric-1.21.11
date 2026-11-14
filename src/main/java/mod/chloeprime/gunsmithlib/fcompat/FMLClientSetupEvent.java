package mod.chloeprime.gunsmithlib.fcompat;

public class FMLClientSetupEvent extends Event {
    public void enqueueWork(Runnable work) {
        work.run();
    }
}
