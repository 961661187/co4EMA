package com.lasat.dsdco.node;

import com.lasat.dsdco.bean.Space;

public interface SpaceStorageService {
    void addSpace(Space space);
    Space getBestSpace();
    int getSpaceCount();
}
