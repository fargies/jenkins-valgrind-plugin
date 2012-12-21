package hudson.plugins.valgrind.parser;

import hudson.plugins.analysis.util.model.Priority;

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
    UninitValue,
    SyscallParam,
    ClientCheck,
    Leak_DefinitelyLost,
    Leak_IndirectlyLost,
    Leak_PossiblyLost,
    Leak_StillReachable;

    public static LeakType fromString(final String type) {
        return LeakType.valueOf(type);
    }

    public Priority getPriority() {
        switch (this) {
            case Overlap:
            case InvalidMemPool:
            case Leak_StillReachable:
            case Leak_PossiblyLost:
            case Leak_IndirectlyLost:
                return Priority.NORMAL;

            case UninitValue:
            case Leak_DefinitelyLost:
            case ClientCheck:
            case SyscallParam:
            case InvalidFree:
            case InvalidWrite:
            case InvalidJump:
            case MismatchedFree:
            case InvalidRead:
            case UninitCondition:
                return Priority.HIGH;
        }
        return Priority.NORMAL;
    }
}
