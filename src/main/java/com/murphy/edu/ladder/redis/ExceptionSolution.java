package com.murphy.edu.ladder.redis;

import java.util.HashMap;

/**
 * @Author Li
 * @Date 2020-12-28 11:17:14
 * @Version 1.0.0
 * run mode
 */
public enum ExceptionSolution {

    /***异常处理方式***/
    THROW("throw", "抛出异常"),
    /***异常处理方式***/
    CATCH("catch", "捕获异常");


    private final String solution;
    private final String desc;

    public String getSolution() {
        return solution;
    }

    public String getDesc() {
        return desc;
    }

    ExceptionSolution(String solution, String desc) {
        this.solution = solution;
        this.desc = desc;
    }

    public static final HashMap<String, ExceptionSolution> SOLUTION_MAP = new HashMap<>();

    static {
        SOLUTION_MAP.put(THROW.solution, ExceptionSolution.THROW);
        SOLUTION_MAP.put(CATCH.solution, ExceptionSolution.CATCH);
    }
}
