package mod.chloeprime.gunsmithlib.fcompat;

/** Minimal stand-in for maven-artifact's version parser (not available at runtime). */
public final class DefaultArtifactVersion implements ArtifactVersion {
    private final int major, minor, incremental;
    private final String qualifier;

    public DefaultArtifactVersion(String version) {
        var main = version;
        var qual = "";
        var dash = version.indexOf('-');
        if (dash >= 0) {
            main = version.substring(0, dash);
            qual = version.substring(dash + 1);
        }
        var plus = main.indexOf('+');
        if (plus >= 0) main = main.substring(0, plus);
        var parts = main.split("\\.");
        this.major = parseInt(parts, 0);
        this.minor = parseInt(parts, 1);
        this.incremental = parseInt(parts, 2);
        this.qualifier = qual;
    }

    private static int parseInt(String[] parts, int i) {
        if (i >= parts.length) return 0;
        try {
            return Integer.parseInt(parts[i].replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int getMajorVersion() {
        return major;
    }

    @Override
    public int getMinorVersion() {
        return minor;
    }

    @Override
    public int getIncrementalVersion() {
        return incremental;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public int compareTo(ArtifactVersion other) {
        int c = Integer.compare(major, other.getMajorVersion());
        if (c != 0) return c;
        c = Integer.compare(minor, other.getMinorVersion());
        if (c != 0) return c;
        return Integer.compare(incremental, other.getIncrementalVersion());
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + incremental + (qualifier.isEmpty() ? "" : "-" + qualifier);
    }
}
