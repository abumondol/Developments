package nursingactivity;

/**
 *
 * @author mm5gg
 */
public class ExtractedVals {

    public static int[] nurseIds = {13, 14, 27, 30, 31, 41, 42, 43, 45, 46, 50, 61, 64, 66, 67, 68, 98, 99, 100, 103, 105, 107, 201, 202, 203, 205};
    public static int[] nurseIdsAll = {13, 14, 27, 30, 31, 41, 42, 43, 45, 46, 50, 52, 61, 64, 66, 67, 68, 80, 97, 98, 99, 100, 103, 105, 106, 107, 201, 202, 203, 205};
    public static String[] activitiesJapanese = {
        "01,アナムネ", "04,身体測定", "05,体重測定", "08,血圧測定", "10,採血",
        "12,点滴静注開始", "13,点滴静注終了", "15,点滴ライン交換", "18,医師処置介助", "19,動脈触知",
        "20,浮腫診察", "22,褥瘡確認", "23,心電図検査", "24,心電図モニター装着", "25,心電モニター除去",
        "27,バストバンド装着", "28,ポータブルX線", "29,創部付け替え", "31,体位交換", "32,全身清拭",
        "36,車いす介助", "37,歩行介助", "38,ベッド運搬", "39,手洗い", "41,看護記録",
        "42,時刻同期"}; // 42 is not in list, it means 'Time synchronization'

    public static String[] activitiesNullJapanese = {
        "x01,与薬", "x02,看護師休憩", "x03,カンファ", "x04,入浴介助",
        "x05,安楽", "x06,配薬準備", "x07,身の周りの世話", "x08,その他",
        "x09,Satモニター", "x10,排泄ベッドサイド", "x11,食事ケア", "x12,環境操作",
        "x13,おむつ交換", "x14,排泄ベッド上", "x15,スケジュール調整", "x16,尿道留置カテ抜去",
        "x17,ギャッジUP", "x18,体温測定", "x19,報告連絡相談調整"};

    public static String[] activitiesNullEnglish = {
        "x01,Medicinal drug", "x02,Nurse break", "x03,Camphor", "x04,Bath assistance",
        "x05,Comfort", "x06,Dispensing preparation", "x07,Caring around the body", "x08,Other",
        "x09,Sat_monitor", "x10,Excretion bedside", "x11,Food care", "x12,Environment operation",
        "x13,Change diaper", "x14,On excretion bed", "x15,Scheduling adjustment", "x16,Removal of catheter",
        "x17,A gadge_UP", "x18,Body temperature measurement", "x19,Report contact liaison coordination"};
}
