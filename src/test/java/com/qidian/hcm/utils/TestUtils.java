package com.qidian.hcm.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import static com.qidian.hcm.utils.TokenType.*;

@SuppressWarnings("PMD")
public class TestUtils {

    public static final String DATE_FORMAT1 = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT2 = "yyyy-MM-dd";

    public static final String DATE_FORMAT3 = "yyyyMMddHHmmss";

    public static final String DATE_FORMAT4 = "MMddHHmmss";


    private static String[] telFirst = ("134,135,136,137,138,139,150,151,152" +
            ",157,158,159,130,131,132,155,156,133,153,176").split(",");

    private static String firstName = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施" +
            "张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑" +
            "薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧" +
            "计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡" +
            "凌霍虞万支柯咎管卢莫经房裘缪干解应宗宣丁贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄" +
            "魏加封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘姜" +
            "詹束龙叶幸司韶郜黎蓟薄印宿白怀蒲台从鄂索咸籍赖卓蔺屠蒙池乔阴郁胥能苍双闻莘党翟谭贡劳逄姬申扶堵冉" +
            "宰郦雍却璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庚终暨居衡步都耿" +
            "满弘匡国文寇广禄阙东殴殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空曾毋沙乜养鞠须丰巢关蒯相查后" +
            "江红游竺权逯盖益桓公万俟司马上官欧阳夏侯诸葛闻人东方赫连皇甫尉迟公羊澹台公冶宗政濮阳淳于" +
            "仲孙太叔申屠公孙乐正轩辕令狐钟离闾丘长孙慕容鲜于宇文司徒司空亓官司寇仉督子车颛孙端木巫" +
            "马公西漆雕乐正壤驷公良拓拔夹谷宰父谷粱晋楚阎法汝鄢涂钦段干百里东郭南门呼延归海羊舌微生岳帅" +
            "缑亢况后有琴梁丘左丘东门西门商牟佘佴伯赏南宫墨哈谯笪年爱阳佟第五言福百家姓续";

    private static String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春" +
            "菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜" +
            "秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园" +
            "艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽 ";

    private static String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全" +
            "国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦" +
            "承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建" +
            "家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";

    private static String[] road = ("重庆大厦,黑龙江路,十梅庵街,遵义路,湘潭街,瑞金广场,仙山街" +
            ",仙山东路,仙山西大厦,白沙河路,赵红广场,机场路,民航街,长城南路,流亭立交桥,虹桥广场," +
            "长城大厦,礼阳路,风岗街,中川路,白塔广场,兴阳路,文阳街,绣城路,河城大厦,锦城广场,崇阳街," +
            "华城路,康城街,正阳路,和阳广场,中城路,江城大厦,顺城路,安城街,山城广场,春城街,国城路,泰城街," +
            "德阳路,明阳大厦,春阳路,艳阳街,秋阳路,硕阳街,青威高速,瑞阳街,丰海路,双元大厦,惜福镇街道,夏庄街道," +
            "古庙工业园,中山街,太平路,广西街,潍县广场,博山大厦,湖南路,济宁街,芝罘路,易州广场,荷泽四路," +
            "荷泽二街,荷泽一路,荷泽三大厦,观海二广场,广西支街,观海一路,济宁支街,莒县路,平度广场,明水路," +
            "蒙阴大厦,青岛路,湖北街,江宁广场,郯城街,天津路,保定街,安徽路,河北大厦,黄岛路,北京街,莘县路," +
            "济南街,宁阳广场,日照街,德县路,新泰大厦,荷泽路,山西广场,沂水路,肥城街,兰山路,四方街,平原广场," +
            "泗水大厦,浙江路,曲阜街,寿康路,河南广场,泰安路,大沽街,红山峡支路,西陵峡一大厦,台西纬一广场," +
            "台西纬四街,台西纬二路,西陵峡二街,西陵峡三路,台西纬三广场,台西纬五路,明月峡大厦,青铜峡路," +
            "台西二街,观音峡广场,瞿塘峡街,团岛二路,团岛一街,台西三路,台西一大厦,郓城南路,团岛三街," +
            "刘家峡路,西藏二街,西藏一广场,台西四街,三门峡路,城武支大厦,红山峡路,郓城北广场,龙羊峡路," +
            "西陵峡街,台西五路,团岛四街,石村广场,巫峡大厦,四川路,寿张街,嘉祥路,南村广场,范县路,西康街," +
            "云南路,巨野大厦,西江广场,鱼台街,单县路,定陶街,滕县路,钜野广场,观城路,汶上大厦,朝城路,滋阳街," +
            "邹县广场,濮县街,磁山路,汶水街,西藏路,城武大厦,团岛路,南阳街,广州路,东平街,枣庄广场,贵州街," +
            "费县路,南海大厦,登州路,文登广场,信号山支路,延安一街,信号山路,兴安支街,福山支广场,红岛支大厦," +
            "莱芜二路,吴县一街,金口三路,金口一广场,伏龙山路,鱼山支街,观象二路,吴县二大厦,莱芜一广场," +
            "金口二街,海阳路,龙口街,恒山路,鱼山广场,掖县路,福山大厦,红岛路,常州街,大学广场,龙华街," +
            "齐河路,莱阳街,黄县路,张店大厦,祚山路,苏州街,华山路,伏龙街,江苏广场,龙江街,王村路,琴屿大厦," +
            "齐东路,京山广场,龙山路,牟平街,延安三路,延吉街,南京广场,东海东大厦,银川西路,海口街,山东路," +
            "绍兴广场,芝泉路,东海中街,宁夏路,香港西大厦,隆德广场,扬州街,郧阳路,太平角一街,宁国二支路," +
            "太平角二广场,天台东一路,太平角三大厦,漳州路一路,漳州街二街,宁国一支广场,太平角六街," +
            "太平角四路,天台东二街,太平角五路,宁国三大厦,澳门三路,江西支街,澳门二路,宁国四街,大尧一广场," +
            "咸阳支街,洪泽湖路,吴兴二大厦,澄海三路,天台一广场,新湛二路,三明北街,新湛支路,湛山五街,泰州三广场," +
            "湛山四大厦,闽江三路,澳门四街,南海支路,吴兴三广场,三明南路,湛山二街,二轻新村镇," +
            "江南大厦,吴兴一广场,珠海二街,嘉峪关路,高邮湖街,湛山三路,澳门六广场,泰州二路,东海一大厦," +
            "天台二路,微山湖街,洞庭湖广场,珠海支街,福州南路,澄海二街,泰州四路,香港中大厦,澳门五路,新湛三街," +
            "澳门一路,正阳关街,宁武关广场,闽江四街,新湛一路,宁国一大厦,王家麦岛,澳门七广场,泰州一路,泰州六街," +
            "大尧二路,青大一街,闽江二广场,闽江一大厦,屏东支路,湛山一街,东海西路,徐家麦岛函谷关广场,大尧三路," +
            "晓望支街,秀湛二路,逍遥三大厦,澳门九广场,泰州五街,澄海一路,澳门八街,福州北路,珠海一广场,宁国二路," +
            "临淮关大厦,燕儿岛路,紫荆关街,武胜关广场,逍遥一街,秀湛四路,居庸关街,山海关路,鄱阳湖大厦,新湛路," +
            "漳州街,仙游路,花莲街,乐清广场,巢湖街,台南路,吴兴大厦,新田路,福清广场,澄海路,莆田街,海游路,镇江街," +
            "石岛广场,宜兴大厦,三明路,仰口街,沛县路,漳浦广场,大麦岛,台湾街,天台路,金湖大厦,高雄广场,海江街," +
            "岳阳路,善化街,荣成路,澳门广场,武昌路,闽江大厦,台北路,龙岩街,咸阳广场,宁德街,龙泉路,丽水街," +
            "海川路,彰化大厦,金田路,泰州街,太湖路,江西街,泰兴广场,青大街,金门路,南通大厦,旌德路,汇泉广场," +
            "宁国路,泉州街,如东路,奉化街,鹊山广场,莲岛大厦,华严路,嘉义街,古田路,南平广场,秀湛路,长汀街," +
            "湛山路,徐州大厦,丰县广场,汕头街,新竹路,黄海街,安庆路,基隆广场,韶关路,云霄大厦,新安路,仙居街," +
            "屏东广场,晓望街,海门路,珠海街,上杭路,永嘉大厦,漳平路,盐城街,新浦路,新昌街,高田广场,市场三街," +
            "金乡东路,市场二大厦,上海支路,李村支广场,惠民南路,市场纬街,长安南路,陵县支街,冠县支广场," +
            "高密路,阳谷广场,平阴路,夏津大厦,邱县路,渤海街,恩县广场,旅顺街,堂邑路,李村街,即墨路,港华大厦,港环路,馆陶街,普集路,朝阳街,甘肃广场,港夏街,").split(",");

    private static final String[] email_suffix = ("@gmail.com,@yahoo.com,@msn.com,@hotmail.com," +
            "@aol.com,@ask.com,@live.com,@qq.com,@0355.net," +
            "@163.com,@163.net,@263.net,@3721.net,@yeah.net," +
            "@googlemail.com,@126.com,@sina.com,@sohu.com,@yahoo.com.cn").split(",");

    public static final String base = "abcdefghijklmnopqrstuvwxyz0123456789";


    private static final SerializerFeature[] CONFIG = new SerializerFeature[]{
            SerializerFeature.WriteNullBooleanAsFalse,//boolean为null时输出false
            SerializerFeature.WriteMapNullValue, //输出空置的字段
            SerializerFeature.WriteNonStringKeyAsString,//如果key不为String 则转换为String 比如Map的key为Integer
            SerializerFeature.WriteNullListAsEmpty,//list为null时输出[]
            SerializerFeature.WriteNullNumberAsZero,//number为null时输出0
            SerializerFeature.WriteNullStringAsEmpty//String为null时输出""
    };

    /**
     * 获取指定格式的日期
     * @param format
     * @return
     */
    public static String getDate(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 当前日期几天后日期（负数为几天前）
     * @param day
     * @return
     */
    public static Date getDate(int day) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    public static String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            in.read(bytes);
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }



    /**
     * 含枚举类的Javabean转换jsonstr
     * @param javaObject
     * @return
     */
    public static String toJSON(Object javaObject) {
        SerializeWriter out = new SerializeWriter();
        String jsonStr;
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            for (com.alibaba.fastjson.serializer.SerializerFeature feature : CONFIG) {
                serializer.config(feature, true);
            }
            serializer.config(SerializerFeature.WriteEnumUsingName, false);
            serializer.write(javaObject);
            jsonStr = out.toString();
        } finally {
            out.close();
        }
        return jsonStr;
    }

    /**
     * 返回指定范围随机数
     * @param start
     * @param end
     * @return
     */
    public static int getNum(int start,int end) {
        return (int)(Math.random() * (end - start + 1) + start);
    }

    /**
     * 返回手机号码
     */
    public static String getPhone() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + third;
    }

    /**
     * 返回Email
     * @param min 最小长度
     * @param max 最大长度
     * @return
     */
    public static String getEmail(int min, int max) {
        int length = getNum(min, max);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = (int) (Math.random() * base.length());
            sb.append(base.charAt(number));
        }
        sb.append(email_suffix[(int)(Math.random() * email_suffix.length)]);
        return sb.toString();
    }

    /**
     * 返回中文姓名
     */
    public static String getChineseName() {
        int index = getNum(0, firstName.length() - 1);
        String first = firstName.substring(index, index + 1);
        int sex = getNum(0, 1);
        String str = boy;
        int length = boy.length();
        String nameSex = "";
        if (sex == 0) {
            str = girl;
            length = girl.length();
            nameSex = "女";
        } else {
            nameSex = "男";
        }
        index = getNum(0, length - 1);
        String second = str.substring(index, index + 1);
        int hasThird = getNum(0, 1);
        String third = "";
        if (hasThird == 1) {
            index = getNum(0, length - 1);
            third = str.substring(index, index + 1);
        }
        return first + second + third;
    }

    /**
     * 返回地址
     * @return
     */
    public static String getRoad() {
        int index = getNum(0,road.length - 1);
        String first = road[index];
        String second = String.valueOf(getNum(11, 150)) + "号";
        String third = "-" + getNum(1,20) + "-" + getNum(1, 10);
        return first + second + third;
    }

    /**
     * 保存管理员token
     * @param token
     * @param name 文件名称 manager general
     * @return
     */
    public static boolean saveToken(String token, TokenType name) {
        boolean status = false;
        try {
            File file = new File("src/test/resources/" + name + ".token");
            if (file.exists()) {
                status = file.delete();
            }
            status = file.createNewFile();
            FileUtils.writeStringToFile(file, token, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            status = false;
            System.out.println("保存token失败:" + name);
        }
        return status;
    }

    /**
     * 读取指定token
     * @param name
     * @return
     */
    public static String readToken(TokenType name) {
        String token = "";
        try {
            token = FileUtils.readFileToString(
                    new File("src/test/resources/" + name + ".token"), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            System.out.println(name + "token文件不存在");
        }
        return token;
    }

    public static void deleteToken() {
        FileUtils.deleteQuietly(new File("src/test/resources/" + MANAGER + ".token"));
        FileUtils.deleteQuietly(new File("src/test/resources/" + HAVE_PERMISSION + ".token"));
        FileUtils.deleteQuietly(new File("src/test/resources/" + NO_PERMISSION + ".token"));

    }

    public static void switchToTenant(ClientUtils client) {
        client.createRequest(MethodType.get, "/api/employees/me")
                .sendRequset().getResponse();
    }

    public static void switchToCenter(ClientUtils client) {
        client.createRequest(MethodType.post, "/api/user/phoneCode/13100000000")
                .sendRequset().getResponse();
    }
}
