package mod.chloeprime.gunsmithlib.fcompat;

public class FMLCommonSetupEvent extends Event {
    public void enqueueWork(Runnable work) {
        work.run();
    }
}
