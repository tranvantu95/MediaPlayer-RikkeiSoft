package com.rikkeisoft.musicplayer.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rikkeisoft.musicplayer.model.base.BaseItem;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    //
    public static <Item extends BaseItem> int findIndex(List<Item> items, int id) {
        for(int i = items.size() - 1; i >= 0; i--) {
            if(id == items.get(i).getId()) return i;
        }

        return -1;
    }

    @Nullable
    public static <Item extends BaseItem> Item findItem(List<Item> items, int id) {
        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(id == item.getId()) return item;
        }

        return null;
    }

    @NonNull
    public static <Item extends BaseItem, Item2 extends BaseItem> List<Item> findItems(
            List<Item> items, Item2 item2, FindItems<Item, Item2> findItems) {
        List<Item> _items = new ArrayList<>();

        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(item2.getId() == findItems.getId(item)) {
                findItems.linked(item, item2);
                _items.add(0, item);
            }
        }

        return _items;
    }

    public interface FindItems<Item, Item2> {
        int getId(Item item);
        void linked(Item item, Item2 item2);
    }

    //
    public static <T extends BaseItem> boolean equalsList(List<T> oldList, List<T> newList) {
        if(oldList.size() != newList.size()) return false;

        int size = oldList.size();
        for(int i = 0; i < size; i++)
            if(oldList.get(i).getId() != newList.get(i).getId()) return false;

        return true;
    }

    //
    @NonNull
    public static  <T extends BaseItem> List<T> searchList(List<T> rootList, List<T> oldList) {
        List<T> newList = new ArrayList<>();

        //
//        int rootSize = rootList.size();
//        for(int i = 0; i < rootSize; i++) {
//            T item = rootList.get(i);
//            int oldSize = oldList.size();
//            if(oldSize == 0) break;
//            for(int j = 0; j < oldSize; j++) {
//                if (oldList.get(j).getId() == item.getId()) {
//                    newList.add(item);
//                    oldList.remove(j);
//                    break;
//                }
//            }
//        }

        //
        int oldSize = oldList.size();
        for(int i = 0; i < oldSize; i++) {
            T item = oldList.get(i);
            int rootSize = rootList.size();
            for(int j = 0; j < rootSize; j++) {
                if (rootList.get(j).getId() == item.getId()) {
                    newList.add(rootList.get(j));
                    break;
                }
            }
        }

        return newList;
    }

}
