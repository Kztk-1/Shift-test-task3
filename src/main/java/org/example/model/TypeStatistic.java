package org.example.model;

import lombok.Data;

@Data
public abstract class TypeStatistic<T> {

    protected int typeCnt;

    protected abstract void updateStat(T word);

}
