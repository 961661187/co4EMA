package com.lasat.dsdco.node;

import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;

/**
 * The interface for message service
 * @author MactavishCui
 */
public interface MessageService {
    Space json2space(String json);
    Point json2point(String json);
    String space2json(Point point);
    String point2json(Space space);

    boolean isSpace(String json);

    /**
     * check if given message is duplicated
     * @param json the message to be checked
     * @return true if the message is duplicated
     */
    boolean isDuplicatedMsg(String json);

}
