package cn.chloeprime.commons.rpc;

@FunctionalInterface
public interface Fun4<T1, T2, T3, T4> {
    void invoke(T1 a, T2 b, T3 c, T4 d);
}
