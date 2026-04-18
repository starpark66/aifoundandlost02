package org.example.aifoundandlost.util;

public class UserContext {
    private static final ThreadLocal<Long> UID = new ThreadLocal<>();

    public static void set(Long uid) { UID.set(uid); }
    public static Long get() { return UID.get(); }
    public static void clear() { UID.remove(); }
}
