package com.codenjoy.dojo.collapse.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.services.event.EventObject;

public class Event implements EventObject<Event.Type, Integer> {

    public enum Type {
        SUCCESS;
    }

    public Event(Type type, int count) {
        this.type = type;
        this.count = count;
    }

    private Type type;
    private int count;

    @Override
    public Event.Type type() {
        return type;
    }

    @Override
    public Integer value() {
        return count;
    }

    @Override
    public String toString() {
        return String.format("Event[%s:%s]", type, count);
    }
}