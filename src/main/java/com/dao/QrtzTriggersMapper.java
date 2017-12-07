package com.dao;

import com.model.QrtzTriggers;
import com.vo.QrtzTriggersVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QrtzTriggersMapper {
    QrtzTriggers selectByPrimaryKey(QrtzTriggers key);

    List<QrtzTriggersVo> findAll();
}