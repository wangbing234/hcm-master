package com.qidian.hcm.common.utils;

import com.qidian.hcm.common.config.HCMConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 全局唯一ID生成器
 */
@Component
public class IDGeneratorUtil {

    @Autowired
    private HCMConfig hcmConfig;
    /**
     * 起始的时间戳
     */
    private static final long START_TIME_STAMP = 1534694400000L;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上一次时间戳
     */
    private long lastTimeStamp = -1L;

    /**
     * 每一部分占用的位数
     */
    private static final long SEQUENCE_BIT = 12L; //序列号占用的位数
    private static final long MACHINE_BIT = 5L;  //机器标识占用的位数
    private static final long DATACENTER_BIT = 5L;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private static final long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long getNextId() {
        if (hcmConfig.getSnowFlakeDatacenterId() > MAX_DATACENTER_NUM ||
                hcmConfig.getSnowFlakeDatacenterId() < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (hcmConfig.getSnowFlakeMachineId() > MAX_MACHINE_NUM ||
                hcmConfig.getSnowFlakeMachineId() < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }

        long currStmp = getNewTimpStamp();

        if (currStmp < lastTimeStamp) {
            throw new IllegalArgumentException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastTimeStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimeStamp = currStmp;

        return (currStmp - START_TIME_STAMP) << TIMESTMP_LEFT //时间戳部分
                | hcmConfig.getSnowFlakeDatacenterId() << DATACENTER_LEFT      //数据中心部分
                | hcmConfig.getSnowFlakeMachineId() << MACHINE_LEFT            //机器标识部分
                | sequence;                            //序列号部分
    }

    private long getNextMill() {
        long mill = getNewTimpStamp();
        while (mill <= lastTimeStamp) {
            mill = getNewTimpStamp();
        }
        return mill;
    }

    private long getNewTimpStamp() {
        return System.currentTimeMillis();
    }

}
