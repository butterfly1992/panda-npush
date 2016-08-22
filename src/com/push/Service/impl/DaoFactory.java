package com.push.Service.impl;

public class DaoFactory {

    public static <T> T getInstance(String daoName, Class<T> interfaceType) {
        T obj = null;

        try {
            obj = interfaceType.cast(Class.forName(daoName).newInstance());
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
