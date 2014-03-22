/**
 * Your application code goes here
 */

package userclasses;

import com.codename1.l10n.SimpleDateFormat;
import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.events.*;
import com.codename1.ui.util.Resources;
import com.codename1.util.StringUtil;
import jp.co.tanaka.cs.Schedule;
import static jp.co.tanaka.cs.Schedule.ArrStr;
import static jp.co.tanaka.cs.Schedule.addCard;
import static jp.co.tanaka.cs.Schedule.btnDelt;
import static jp.co.tanaka.cs.Schedule.calender;
import static jp.co.tanaka.cs.Schedule.lstModel;
import static jp.co.tanaka.cs.Schedule.updtCard;
import java.util.Date;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase {
    public StateMachine(String resFile) {
        super(resFile);
        // do not modify, write code in initVars and initialize class members there,
        // the constructor might be invoked too late due to race conditions that might occur
    }
    
    /**
     * this method should be used to initialize variables instead of
     * the constructor/class scope to avoid race conditions
     */
    protected void initVars(Resources res) {
        
    }


    @Override
    protected void onMain_Button1Action(Component c, ActionEvent event) {
        //更新
        System.out.println("onMain_Button1Action:");
        if (lstModel.getSelectedIndex() <= 0) {
            //新規
            addCard();
             Schedule.readCSV();
        } else {
            //更新
            int tmpIdx = lstModel.getSelectedIndex();
            updtCard((Integer) lstModel.getSelectedIndex());
             Schedule.readCSV();
            lstModel.setSelectedIndex(tmpIdx);
        }
    }


    //@Override
    protected void onMain_ListAction(Component c, ActionEvent event) {
        System.err.println("onMain_ListAction");
        int idx = lstModel.getSelectedIndex();
        Schedule.selected(idx);
        btnDelt.setEnabled(false);
        String[] strP = {"","","","",""};
        Schedule.setText(strP);
        if (idx <= 0) {
            //上で初期化しておく
        } else {
            btnDelt.setEnabled(true);
            String[] str = {ArrStr[0].get(idx-1),ArrStr[1].get(idx-1),ArrStr[2].get(idx-1),ArrStr[3].get(idx-1)};
            Schedule.setText(str);
            
            //カレンダーに日付セット
            String strDt = ArrStr[0].get(idx-1);
            try {
                java.util.List<String> lst = StringUtil.tokenize(strDt, "/ ");//  replaceAll(strDt, "\\(.*\\)", "")).trim();      //正規表現は使えないらしい
                System.out.println("Parse:" + lst.get(0) + lst.get(1) + lst.get(2));
                //Date date = DateFormat.getDateInstance().parse(lst.get(0));     //実装されていない
                long date = getDateLong(Integer.parseInt(lst.get(0)), Integer.parseInt(lst.get(1)), Integer.parseInt(lst.get(2)));
                System.err.println("hdmDate:" + date);
                
                Date dt = new Date(date);
                System.err.println("Date:" + dt.getTime());
                //dt.setTime(date);
                calender.setDate(dt);   
                calender.repaint();
            } catch (Exception e) {
                //何もしない
                e.printStackTrace();
            }
        }
    }
    
    //無理やり実装
    public long getDateLong(int yyyy, int mm, int dd){
        Long ret = new Long(0);
        long md[] = {31, 999, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        //long y = yyyy - 1900;
        //y
        for (int i = 1970; i < yyyy; i++) {        //2000年は自動的にクリア。400年に一度。
            if ((i % 4) != 0) {
                ret = ret + 365L * 24L * 60L * 60L * 1000L;
                md[1] = 28;
            } else {
                ret = ret + 366L * 24L * 60L * 60L * 1000L;
                md[1] = 29;
            }
        }
        //m
        for (int i = 1; i < mm; i++) {
            ret = ret + md[i-1] * 24L * 60L * 60L * 1000L;
        }
        //d
        ret = ret + (dd - 1L) * 24L * 60L * 60L * 1000L;
        
        return ret;
    }
    
    @Override
    protected void onMain_ButtonAction(Component c, ActionEvent event) {
        //削除
        int tmpIdx = lstModel.getSelectedIndex();
        if (tmpIdx <= 0) {
            //削除対象なし
        } else {
            //選択行を削除
            lstModel.removeItem(tmpIdx);
            for (int i = 0; i < 4; i++) {
                ArrStr[i].remove(tmpIdx - 1);
            }
            //１行目を選択
            lstModel.setSelectedIndex(0);
            Schedule.selected(0);
            //削除ボタン無効化
            btnDelt.setEnabled(false);
            Schedule.writeCSV(true);
            Schedule.readCSV();
        }
    }

    @Override
    protected void onMain_CalendarAction(Component c, ActionEvent event) {
        //カレンダー処理
        Date date = ((Calendar)c).getDate();
        System.err.println("Calendar:" + date.getTime());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd (E)");
        String str = sdf1.format(date);
        Schedule.txt1.setText(str);
        
        
        //同じ日付を探す
        int i = 0;
        for (i = 0; i < lstModel.getSize(); i++) {
            //System.err.println("lstModel:" + (String)lstModel.getItemAt(i));
            //System.err.println("str:" + str);
            
            if ((((String)lstModel.getItemAt(i)).trim()).equals(str)) {
                lstModel.setSelectedIndex(i);
                onMain_ListAction(c, event);
                break;
            }
        }
        if (i == lstModel.getSize()) {
            lstModel.setSelectedIndex(0);
            //モードのみ表示
            //onMain_ListAction(c, event);
            Schedule.selected(0);
        }
    }

    protected void onMain_Button2Action(Component c, ActionEvent event) {
        //新規
        addCard();
        Schedule.readCSV();
    }

    protected void onMain_Button3Action(Component c, ActionEvent event) {
        //起動直後には、カレンダーの日付を今日にする
        Date dt = new Date();
        System.err.println("Date:" + dt.getTime());
        calender.setDate(dt);
        onMain_CalendarAction(calender, null);
    }

    @Override
    protected void onMain_TextAreaAction(Component c, ActionEvent event) {
        System.err.println("onMain_TextAreaAction:" + event.getCommand());
        Schedule.edited(true);
    }

    @Override
    protected void onMain_TextFieldAction(Component c, ActionEvent event) {
        System.err.println("onMain_TextFieldAction:" + event.getCommand());
        Schedule.edited(true);
    }

    @Override
    protected void onMain_TextField1Action(Component c, ActionEvent event) {
        System.err.println("onMain_TextField1Action:" + event.getCommand());
        Schedule.edited(true);
    }

    @Override
    protected void onMain_TextField2Action(Component c, ActionEvent event) {
        System.err.println("onMain_TextField2Action:" + event.getCommand());
        Schedule.edited(true);
    }

}
