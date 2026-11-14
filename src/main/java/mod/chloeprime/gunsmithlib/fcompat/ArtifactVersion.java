package mod.chloeprime.gunsmithlib.fcompat;

public interface ArtifactVersion extends Comparable<ArtifactVersion> {
    int getMajorVersion();

    int getMinorVersion();

    int getIncrementalVersion();

    String getQualifier();
}
