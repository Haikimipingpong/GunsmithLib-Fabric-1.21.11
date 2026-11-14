package cn.chloeprime.commons.rpc;

@FunctionalInterface
public interface Fun2<T1, T2> {
    void invoke(T1 a, T2 b);
}
