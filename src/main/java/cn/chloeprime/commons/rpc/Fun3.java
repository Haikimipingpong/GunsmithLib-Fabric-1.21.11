package cn.chloeprime.commons.rpc;

@FunctionalInterface
public interface Fun3<T1, T2, T3> {
    void invoke(T1 a, T2 b, T3 c);
}
