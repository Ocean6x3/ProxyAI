package ee.carlrobert.codegpt.cjhx;


import java.util.Date;
import java.util.List;
import java.util.Map;
public class DemoPojo {
    /**
     * 用户姓名
     */
    private String name;

    // 用户年龄
    private int age;

    /** 是否有效 */
    private boolean valid;

    /* 用户性别 */
    private String gender;

    @Deprecated
    // 过期时间
    private Date expired;

    // 第一行注释
// 第二行注释
    private String multiLineComment;
}