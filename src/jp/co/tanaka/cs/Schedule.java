package jp.co.tanaka.cs;

import com.codename1.io.Storage;
import static com.codename1.testing.TestUtils.findByName;
import com.codename1.ui.Button;
import com.codename1.ui.Calendar;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.List;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.util.StringUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import userclasses.StateMachine;

public class Schedule {
   
    private static final String fileName = "cardDB.csv";
    private Form current;
    //private static Label dbName;
    public static TextField txt1;
    private static TextField txt2;
    private static TextField txt3;
    private static TextArea txtHonbun;
    private static List list;
    public static ListModel lstModel;
    public static Button btnAdd;
    public static Button btnUpdt;
    public static Button btnDelt;
    public static Calendar calender;
    private static Container cntList;
    private static Label FormTitle;
    private static boolean editedWk;
    private static int selectedIdxWk;
    
    public static ArrayList<String> ArrStr[];   //CSV
    
    //Disp List
    private static DefaultListModel ModelL;
    private static DefaultListModel ModelIdx;
    
    public void init(Object context) {
        // Pro users - uncomment this code to get crash reports sent to you automatically
        /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                Log.p("OS " + Display.getInstance().getPlatformName());
                Log.p("Error " + evt.getSource());
                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
                Log.e((Throwable)evt.getSource());
                Log.sendLog();
            }
        });*/
    }

    public void start() {
        if(current != null){
            current.show();
            return;
        }
        new StateMachine("/theme");   
        
        //Init
        ArrStr = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            ArrStr[i] = new ArrayList();
            //ArrStr[i].removeAll(null);
        }
        //dbName = (Label)findByName("Label");
        txt1 = (TextField)findByName("TextField");
        txt2 = (TextField)findByName("TextField1");
        txt3 = (TextField)findByName("TextField2");
        txtHonbun = (TextArea)findByName("TextArea");
        btnDelt = (Button)findByName("Button");
        btnDelt.setEnabled(false);
        btnUpdt = (Button)findByName("Button1");
        FormTitle = (Label)findByName("Label");
        
        calender = (Calendar)findByName("Calendar");
        
        int h = txt1.getHeight() * 4;
        list = (List)findByName("List");
        cntList = (Container)findByName("Container5");
        list.setHeight(h);
        cntList.setHeight(h + FormTitle.getPreferredH());
        list.setPreferredH(h);
        cntList.setPreferredH(h + FormTitle.getPreferredH());
        
        //dbName.setText("Memo");
        txt1.setText("");
        txt2.setText("");
        txt3.setText("");
        txtHonbun.setText("");
        
        lstModel = list.getModel();
        ModelL = new DefaultListModel();
        ModelIdx = new DefaultListModel();
        initList();
        readCSV();
        
        System.out.println("初期化完了");
    }
    private static void initList(){
        System.out.println("initList:");
        for (int i = lstModel.getSize(); i >= 0 ; i--) {
            lstModel.removeItem(i);
        }
        lstModel.addItem("Add...");
        ModelL.removeAll();
        ModelIdx.removeAll();
    }
    public static void edited(boolean edited) {
        editedWk = edited;
        setMode();
    }
    public static void selected(int idx) {
        selectedIdxWk = idx;
        setMode();
    }
    private static void setMode() {
        String str = "";
        if (editedWk) {
            str = "*m*";
        }
        FormTitle.setText(str + lstModel.getItemAt(selectedIdxWk));
    }
    public static void addCard() {
        //if (JOptionPane.showConfirmDialog(null, "Write?") != JOptionPane.YES_OPTION) {
        //    return;
        //}
        System.out.println("");
        //CSV?????
        String[] str = {"","","","","","","","","",""};
        getText(str);
        
        for (int j = 0; j < 4; j++) {
            ArrStr[j].add(str[j]);
        }
        writeCSV(false);
        readCSV();
        //最後の行を選択した状態にする
        lstModel.setSelectedIndex(lstModel.getSize() - 1);
        btnDelt.setEnabled(true);
    }
    public static void updtCard(int idx) {
        //if (JOptionPane.showConfirmDialog(null, "Update?") != JOptionPane.YES_OPTION) {
        //    return;
        //}
        //????????
        String[] str = {"","","","","","","","","",""};
        getText(str);
        for (int i = 0; i < 4; i++) {
            //ArrStr[i].remove(idx);
            ArrStr[i].set(idx - 1, str[i]);
        }
        writeCSV(false);
        readCSV();
    }
    public static void readCSV() {
        System.out.println("readCSV:" + fileName);
        
        //File csv = new File(fileName); // CSV file
        InputStream inp = null;
        try {
            inp = Storage.getInstance().createInputStream(fileName);
            if (!Storage.getInstance().exists(fileName)) {
                System.out.println("NoFile:" + fileName);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        //CSV
        try {
            String src = "";
            byte[] b = null;
            int size = inp.available();
            if (size > 0) {
                b = new byte[size];
                inp.read(b);
                src = new String(b);
            } else {
                src ="";
            }
            //改行毎
            java.util.List<String> lstAll = StringUtil.tokenize(src, "\n");
            System.out.println("Src:" + src);
            
            String[] str = {"","","","","","","","","",""};
            //List Clear
            initList();
            //Init Arr
            ArrStr = new ArrayList[4];
            for (int i = 0; i < 4; i++) {
                ArrStr[i] = new ArrayList();
                //ArrStr[i].removeAll(null);
            }
            // 改行毎
            //String line = "";
            for (int i = 0; i < lstAll.size(); i++) {
                //先に配列を確保
                for (int j = 0; j < 4; j++) {
                    ArrStr[j].add("");
                }
                //列毎
                java.util.List<String> cols = StringUtil.tokenize(lstAll.get(i), "\t");
                String wk;
                int col = 0;
                for (int j = 0; j < cols.size(); j++) {
                    System.err.println("ColSize:" + cols.size());
                    try {
                        wk = cols.get(j);
                    }catch (Exception e) {
                        wk = "";
                    }
                    if (j <= 3) {
                        col = j;
                    } else {
                        col = 3;
                    }
                    //4:残り全部
                    if (j >= 3) {
                        for (int k = 4; k < cols.size(); k++) {
                            wk = wk + "\t" + cols.get(k);
                            j = k;
                        }
                    }
                    ArrStr[col].set(i, StringUtil.replaceAll(wk, "\t", "\n"));
                    System.out.println(wk);
                }
            }
            inp.close();
            
            //画面左のリストに設定
            setList(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Error");
        }
    }
    public static void setList(boolean flg) {
        //List Clear
        initList();
        //Set List
        System.out.println("setList:");
        for (int i = 0; i < ArrStr[0].size(); i++) {
            //if (ArrStr[0].get(i) == null) {
            //    break;
            //}
            String wL = ArrStr[0].get(i);
            ModelL.addItem(wL);
            ModelIdx.addItem(new Integer(i));
            lstModel.addItem(wL); //表示用
        }
        edited(false);
    }
    
    private static void getText(String[] str) {
        str[0] = txt1.getText();
        str[1] = txt2.getText();
        str[2] = txt3.getText();
        str[3] = txtHonbun.getText();
    }
    public static void setText(String[] str) {
        txt1.setText(str[0]);
        txt2.setText(str[1]);
        txt3.setText(str[2]);
        txtHonbun.setText(str[3]);
        edited(false);
    }
    public static void writeCSV(boolean flg) {
        System.out.println("writeCSV");
        //if (flg) {
        //    if (JOptionPane.showConfirmDialog(null, "Write?") != JOptionPane.YES_OPTION) {
        //        return;
        //    }
        //}
        
        //Write CSV
        try {
            // New File
            OutputStream out = Storage.getInstance().createOutputStream(fileName);
            
            //bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv),"UTF-8")));
            
            //Data
            for (int i = 0; i < ArrStr[0].size(); i++) {
                out.write((ArrStr[0].get(i).trim() + " \t").getBytes("UTF-8"));
                out.write((ArrStr[1].get(i).trim() + " \t").getBytes("UTF-8"));
                out.write((ArrStr[2].get(i).trim() + " \t").getBytes("UTF-8"));
                out.write((StringUtil.replaceAll(ArrStr[3].get(i).trim(), "\n", "\t")).getBytes("UTF-8"));
                out.write(" \n".getBytes());
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Error");
            return;
        }
        //JOptionPane.showMessageDialog(null, "Updated.");
    }
    
    
    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
}
