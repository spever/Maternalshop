package com.subzero.common.utils;
import java.util.Random;

import android.text.TextUtils;
/**随机种子
 * <br/>1... 产生随机字符串 {@link #getRandom(char[] sourceChar, int length)}
 * <br/>2... 产生随机字符串 {@link #getRandom(String source, int length)}
 * <br/>3... 产生随机字数串 {@link #getRandomNumbersAndLetters(int length)}
 * <br/>4... 产生随机纯数串 {@link #getRandomNumbers(int length)} 
 * <br/>5... 产生随机字母串 {@link #getRandomLetters(int length)}
 * <br/>6... 产生随机大写串 {@link #getRandomCapitalLetters(int length)}
 * <br/>7... 产生随机小写串 {@link #getRandomLowerCaseLetters(int length)} 
 * <br/>8... 产生随机范围数 {@link #getRandom(int min, int max)}
 * <br/>9... 产生随机最大数 {@link #getRandom(int max)}
 * <br/>10. 洗牌算法洗对象 {@link #shuffle(Object[] objArray, int shuffleCount)} 
 * <br/>11. 洗牌算法洗数组 {@link #shuffle(int[] intArray)}
 * <br/>12. 洗牌算法得小组 {@link #shuffle(int[] intArray, int shuffleCount)}*/
public class RandomUtil 
{
    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    /**获取一个String, 长度为length, 由[0-9,a-z,A-Z]随机组成, 数字可以打头
     * <br/>失败, 返回null
     */
    public static String getRandomNumbersAndLetters(int length) {
    	if(length<=0){
    		return null;
    	}
        return getRandom(NUMBERS_AND_LETTERS, length);
    }
    /**获取一个String, 长度为length, 由[0-9]随机组成, 纯数字
     * <br/>失败, 返回null
     */
    public static String getRandomNumbers(int length) {
    	if(length<=0){
    		return null;
    	}
        return getRandom(NUMBERS, length);
    }
    /**获取一个String, 长度为length, 由[a-z,A-Z]随机组成, 纯英文字母
     * <br/>失败, 返回null
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }
    /**获取一个String, 长度为length, 由[A-Z]随机组成, 纯大写英文字母
     * <br/>失败, 返回null
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }
    /**获取一个String, 长度为length, 由[a-z]随机组成, 纯小写英文字母
     * <br/>失败, 返回null
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**获得一个String, 长度为length, 由source元素随机组成
     * <br/>失败, 返回null
     */
    public static String getRandom(String source, int length) {
        return TextUtils.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    /**获得一个String, 长度为length, 由sourceChar[]元素随机组成
     * <br/>失败, 返回null*/
    public static String getRandom(char[] sourceChar, int length) 
    {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }
        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**获取随机数, [0, max]
     * <br/>失败, 返回0 ; 0 == max, 返回0
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }
    /**获取随机数, [min, max]
     * <br/>失败, 返回0 ; min == max, 返回min
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }

    /**
     * Shuffling algorithm, Randomly permutes the specified array using a default source of randomness
     * 
     * @param objArray
     * @return
     */
    public static boolean shuffle(Object[] objArray) {
        if (objArray == null) {
            return false;
        }
        return shuffle(objArray, getRandom(objArray.length));
    }
    /**洗牌算法, 将Object[] 元素 随机打乱
     * <br/> 失败, 返回false 
     */
    public static boolean shuffle(Object[] objArray, int shuffleCount) 
    {
        int length;
        if (objArray == null || shuffleCount < 0 || (length = objArray.length) < shuffleCount) {
            return false;
        }
        for (int i = 1; i <= shuffleCount; i++) 
        {
            int random = getRandom(length - i);
            Object temp = objArray[length - i];
            objArray[length - i] = objArray[random];
            objArray[random] = temp;
        }
        return true;
    }
    /**洗牌算法, 将int[] 元素 随机打乱
     * <br/> 失败, 返回null 
     */
    public static int[] shuffle(int[] intArray) {
        if (intArray == null) {
            return null;
        }
        return shuffle(intArray, getRandom(intArray.length));
    }
    /**洗牌算法, 获取一个 int[]数组, 长度为shuffleCount, 由intArray[]元素随机组成
     * <br/>shuffleCount 必须 ＜ intArray.length
     * <br/> 失败, 返回null 
     */
    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray == null || shuffleCount < 0 || (length = intArray.length) < shuffleCount) {
            return null;
        }
        int[] out = new int[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) 
        {
            int random = getRandom(length - i);
            out[i - 1] = intArray[random];
            int temp = intArray[length - i];
            intArray[length - i] = intArray[random];
            intArray[random] = temp;
        }
        return out;
    }
}
