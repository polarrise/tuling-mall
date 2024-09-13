package com.tuling.tulingmall;

import cn.hutool.json.JSONUtil;
import com.hankcs.hanlp.dictionary.py.PinyinDictionary;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMulityWordSort {

    @Test
    public void getUser(){
        List<OrgDTO> orgList = new ArrayList<OrgDTO>(){
            {
                add(new OrgDTO(1,"北京"));
                add(new OrgDTO(2,"上海"));
                add(new OrgDTO(3,"重庆"));
                add(new OrgDTO(4,"厦门"));
                add(new OrgDTO(5,"湖南"));
                add(new OrgDTO(6,"安徽"));
            }
        };
        orgList.stream()
               .sorted(Comparator.comparing(OrgDTO::getOrgName,getComparator()))
               .forEach(orgDTO -> log.info(JSONUtil.toJsonStr(orgDTO)));
    }

    /**
     * 比较器,获取多音字首拼后 进行a-z排序
     * @return
     */
    private Comparator<String> getComparator(){
        Collator collator = Collator.getInstance(Locale.CHINA);
        return (o1,o2) -> collator.compare(getPinYinFirstWord(o1), getPinYinFirstWord(o2));
    }

    /**
     * 获取首拼
     * @param chineseStr
     * @return
     */
    public static String getPinYinFirstWord(String chineseStr){
        // 获取第一个字符：
        char firstChar = chineseStr.charAt(0);

        // 第一个字符属于小写a-z,大小A-Z ? 首拼就为这个  :  去多音字的第一个字母(比如说重庆：取出来是c，而不是z, 厦门:取出来是x,而不是s)
        return getAtoZStr().contains(firstChar) ? String.valueOf(firstChar) :
                ((Function<Character, String>) String::valueOf).apply(PinyinDictionary.convertToPinyin(chineseStr).get(0).getPinyinWithoutTone().charAt(0));

    }

    /**
     * 获取所有小写a-z和大小A-Z的字符
     * @return
     */
    private static List<Character> getAtoZStr(){
        List<Character> characterList = new ArrayList<>();
        for (char i ='a' ; i<= 'z' ; i++){
            characterList.add(i);
        }
        for (char i ='A' ; i<= 'Z' ; i++){
            characterList.add(i);
        }
        return characterList;
    }

}

@NoArgsConstructor
@AllArgsConstructor
@Data
class OrgDTO{

    @ApiModelProperty(value = "架构id")
    private Integer orgId;

    @ApiModelProperty(value = "架构名称")
    private String orgName;
}
