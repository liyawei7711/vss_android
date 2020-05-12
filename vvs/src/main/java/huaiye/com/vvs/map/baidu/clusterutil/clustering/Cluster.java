/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package huaiye.com.vvs.map.baidu.clusterutil.clustering;


import com.baidu.mapapi.model.LatLng;

import java.util.Collection;

/**
 * A collection of ClusterItems that are nearby each other.
 */
public interface Cluster<T extends huaiye.com.vvs.map.baidu.clusterutil.clustering.ClusterItem> {
    public LatLng getPosition();

    Collection<T> getItems();
    T getItem();

    int getSize();

    void setNewItem(T item);
}