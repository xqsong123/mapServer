package com.map.utils;

/**
 * @author xqsong
 * @create 2019/3/14
 * @since 1.0.0
 **/
public interface Constants {

    /**
     * 数据来源
     */
    interface SJLY {
        //来自建筑物
        String JZW = "1";
        //来自单元房屋表
        String DYFW = "2";
    }

    /**
     * 地址元素类型
     */
    interface DZYSLX {
        //61单元（门）
        String DY = "61";
        //房屋（号、室）
        String FW = "62";
    }

    interface YesOrNo{
        String YES = "1";
        String NO = "0";
    }


    /**
     * 实有人口管理类别代码
     */
    enum SYRKGLLBDM{
        //常住人口
        CZRK(11, "2"),
        //流动人口
        LDRK(12, "3"),
        //境外人口
        JWRK(20, "5");

        //原系统编码
        private int originCode;
        //现在的编码
        private String newCode;

        SYRKGLLBDM(int originCode, String newCode){
            this.originCode = originCode;
            this.newCode = newCode;
        }

        public static int getOriginCode(String newCode){
            for (SYRKGLLBDM syrkgllbdm : SYRKGLLBDM.values()){
                if (syrkgllbdm.getNewCode().equals(newCode)){
                    return syrkgllbdm.getOriginCode();
                }
            }
            return 0;
        }

        public int getOriginCode() {
            return originCode;
        }

        public void setOriginCode(int originCode) {
            this.originCode = originCode;
        }

        public String getNewCode() {
            return newCode;
        }

        public void setNewCode(String newCode) {
            this.newCode = newCode;
        }
    }

    /**
     * 实有房屋使用形式
     */
    enum SYXS {
        //自用
        ZY("1", "2"),
        //出租
        CZ("2", "3"),
        //闲置
        XZ("3", "4");

        //原系统编码
        private String originCode;
        //现在的编码
        private String newCode;

        SYXS(String originCode, String newCode) {
            this.originCode = originCode;
            this.newCode = newCode;
        }

        public static String getOriginCode(String newCode){
            for (SYXS xysx : SYXS.values()){
                if (xysx.getNewCode().equals(newCode)){
                    return xysx.getOriginCode();
                }
            }
            return null;
        }

        public String getOriginCode() {
            return originCode;
        }

        public void setOriginCode(String originCode) {
            this.originCode = originCode;
        }

        public String getNewCode() {
            return newCode;
        }

        public void setNewCode(String newCode) {
            this.newCode = newCode;
        }
    }

    String[] ZDRYLB = {
            "304000000000", "405000000000", "203000000000","102000000000", "501000000000", "001000000000",
             "601000000000", "701000000000", "801000000000", "901000000000", "120800000000", "051101050200",
             "051502020205", "050102050100", "051601040103", "040100000000", "030000000000", "040200000000",
            "051601040109", "050103040105"
    };

    /**
     * 特种单位
     */
    int[] TZDW = {
            240, 216, 219, 217, 220, 211, 212, 213, 215, 214, 218, 221, 280, 291, 292, 230
    };

    /**
     * 保护单位
     */
    int[] BHDW = {
            251, 259, 269, 268, 252, 271, 253, 254, 255, 256, 257
    };
}
