package com.github.kevincnzuk.buslog.stats;

import android.content.Context;

import com.github.kevincnzuk.buslog.vo.StatsVO;

import java.util.List;

public interface IStatsList {
    public List<StatsVO> getStats();
}
