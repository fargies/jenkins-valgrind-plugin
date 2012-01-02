package hudson.plugins.valgrind.parser;

/**
 * FIXME: Document type LeakType.
 *
 * @author Sylvain Fargier
 */
public enum LeakType {
    InvalidFree,
    MismatchedFree,
    InvalidRead,
    InvalidWrite,
    InvalidJump,
    Overlap,
    InvalidMemPool,
    UninitCondition,
    SyscallParam,
    ClientCheck,
    Leak_DefinitelyLost,
    Leak_IndirectlyLost,
    Leak_PossiblyLost,
    Leak_StillReachable;

    public static LeakType fromString(final String type) {
        return LeakType.valueOf(type);
    }
}
