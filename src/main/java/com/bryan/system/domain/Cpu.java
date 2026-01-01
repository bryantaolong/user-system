package com.bryan.system.domain;

import com.bryan.system.util.math.Arithmetic;
import lombok.Getter;
import lombok.Setter;

/**
 * Cpu CPU相关信息
 *
 * @author Bryan Long
 */
@Setter
public class Cpu {
    /**
     * 核心数
     */
    @Getter private int processorNumber;

    /**
     * CPU 总的使用率
     */
    private double totalUsage;

    /**
     * CPU 系统使用率
     */
    private double systemUsage;

    /**
     * CPU 用户使用率
     */
    private double userUsage;

    /**
     * CPU 当前等待率
     */
    private double waitRate;

    /**
     * CPU 当前空闲率
     */
    private double idleRate;

    public double getTotalUsage() {
        return Arithmetic.round(Arithmetic.mul(totalUsage, 100), 2);
    }

    public double getSystemUsage() {
        return Arithmetic.round(Arithmetic.mul(systemUsage / totalUsage, 100), 2);
    }

    public double getUserUsage() {
        return Arithmetic.round(Arithmetic.mul(userUsage / totalUsage, 100), 2);
    }

    public double getWaitRate() {
        return Arithmetic.round(Arithmetic.mul(waitRate / totalUsage, 100), 2);
    }

    public double getIdleRate() {
        return Arithmetic.round(Arithmetic.mul(idleRate / totalUsage, 100), 2);
    }

}

