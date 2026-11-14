package cn.chloeprime.commons.rpc;

@FunctionalInterface
public interface Fun5<T1, T2, T3, T4, T5> {
    void invoke(T1 a, T2 b, T3 c, T4 d, T5 e);
}
