/**
 * @PackgeName: com.pangu.selenium
 * @ClassName: ImportClearEnterprise
 * @Author: guoqiang
 * Date: 2021/3/5 10:58
 * @Version:
 * @Description:
 */
package com.pangu.unicom;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.GridLayout;
import java.io.File;


public class ImportClearEnterprise  extends JFrame {
    public JLabel excelpathLabel = new JLabel("exel路径");
    public JLabel fileLabel = new JLabel("文件路径");
    public JTextArea filepathtext = new JTextArea("");
    public JFileChooser excelJFileChooser = new JFileChooser(new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath()));
    public TitledBorder titledborder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), "");
    GridLayout layout = new GridLayout(5, 1);
    JPanel panel;
    JComboBoxV areaComb;
    JLabel custom;
    JComboBoxV customChild;
    JComboBoxV customparent;
    JLabel area;
    GridLayout layout1;
    JPanel panel1;
    JPanel panel2;
    JPanel panel4;
    JFileChooser path;
    JPanel panel3;
    JButton submit;

    public static void main(String[] args) {
        new ImportClearEnterprise();
    }



    public  ImportClearEnterprise() {
        this.panel = new JPanel(this.layout);
        this.areaComb = new JComboBoxV();
        this.custom = new JLabel("客户");
        this.customChild = new JComboBoxV();
        this.customparent = new JComboBoxV();
        this.area = new JLabel("纳税人地区");
        this.layout1 = new GridLayout(1, 3);
        this.panel1 = new JPanel(new FlowLayout(0));
        this.panel2 = new JPanel(new FlowLayout(0));
        this.panel4 = new JPanel(new FlowLayout(0));
        this.path = new JFileChooser((new File(FileSystemView.getFileSystemView().getHomeDirectory().getPath())).getAbsolutePath());
        this.panel3 = new JPanel(new FlowLayout(0));
        this.submit = new JButton("提交");
        this.initPane();
        JFrame frame = new JFrame();
        frame.add(this.panel);
        frame.setTitle("导入联通信息采集应用");
        frame.setBounds(300, 200, 800, 300);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(3);


    }
    public class JComboBoxV<T> extends JComboBox{
        String name = null,value = "";
        public void addItemV(String name,String value) {
            this.name = name;
            this.value = value;
            super.addItem(name);
        }
        public String getItemV() {
            return this.value;
        }
        void checkMutableComboBoxModel() {
            if ( !(dataModel instanceof MutableComboBoxModel) )
                throw new RuntimeException("Cannot use this method with a non-Mutable data model.");
        }
    }

    private void initPane(){
        this.titledborder.setTitleFont(new Font("Georgia", 1, 1));
        this.titledborder.setTitleJustification(1);
        this.titledborder.setTitlePosition(0);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("仅能打开 *.xlsx *.xls", new String[]{"xlsx", "xls"});
        this.area.setPreferredSize(new Dimension(95, 35));
        this.area.setHorizontalAlignment(4);
        this.areaComb.addItemV("安徽", "anhui");
        this.areaComb.addItemV("北京", "beijing");
        this.areaComb.addItemV("重庆", "chongqing");
        this.areaComb.addItemV("大连", "dalian");
        this.areaComb.addItemV("福建", "fujian");
        this.areaComb.addItemV("甘肃", "gansu");
        this.areaComb.addItemV("广东", "guangdong");
        this.areaComb.addItemV("广西", "guangxi");
        this.areaComb.addItemV("贵州", "guizhou");
        this.areaComb.addItemV("海南", "hainan");
        this.areaComb.addItemV("河北", "hebei");
        this.areaComb.addItemV("黑龙江", "heilongjiang");
        this.areaComb.addItemV("河南", "henan");
        this.areaComb.addItemV("湖北", "hubei");
        this.areaComb.addItemV("湖南", "hunan");
        this.areaComb.addItemV("江苏", "jiangsu");
        this.areaComb.addItemV("江西", "jiangxi");
        this.areaComb.addItemV("吉林", "jilin");
        this.areaComb.addItemV("辽宁", "liaoning");
        this.areaComb.addItemV("内蒙古", "neimeng");
        this.areaComb.addItemV("宁波", "ningbo");
        this.areaComb.addItemV("宁夏", "ningxia");
        this.areaComb.addItemV("青岛", "qingdao");
        this.areaComb.addItemV("青海", "qinghai");
        this.areaComb.addItemV("陕西", "shaanxi");
        this.areaComb.addItemV("山东", "shandong");
        this.areaComb.addItemV("上海", "shanghai");
        this.areaComb.addItemV("山西", "shanxi");
        this.areaComb.addItemV("深圳", "shenzhen");
        this.areaComb.addItemV("四川", "sichuan");
        this.areaComb.addItemV("天津", "tianjin");
        this.areaComb.addItemV("厦门", "xiamen");
        this.areaComb.addItemV("新疆", "xinjiang");
        this.areaComb.addItemV("西藏", "xizang");
        this.areaComb.addItemV("云南", "yunnan");
        this.areaComb.addItemV("浙江", "zhejiang");
        this.customparent.addItemV("联通", "unicom2019");

        this.customChild.addItemV("联通北京", "联通北京");
        this.customChild.addItemV("联通安徽", "联通安徽");
        this.customChild.addItemV("联通重庆", "联通重庆");
        this.customChild.addItemV("联通大连", "联通大连");
        this.customChild.addItemV("联通福建", "联通福建");
        this.customChild.addItemV("联通甘肃", "联通甘肃");
        this.customChild.addItemV("联通广东", "联通广东");
        this.customChild.addItemV("联通广西", "联通广西");
        this.customChild.addItemV("联通贵州", "联通贵州");
        this.customChild.addItemV("联通海南", "联通海南");
        this.customChild.addItemV("联通河北", "联通河北");
        this.customChild.addItemV("联通黑龙江", "联通黑龙江");
        this.customChild.addItemV("联通河南", "联通河南");
        this.customChild.addItemV("联通湖北", "联通湖北");
        this.customChild.addItemV("联通湖南", "联通湖南");
        this.customChild.addItemV("联通江苏", "联通江苏");
        this.customChild.addItemV("联通江西", "联通江西");
        this.customChild.addItemV("联通吉林", "联通吉林");
        this.customChild.addItemV("联通辽宁", "联通辽宁");
        this.customChild.addItemV("联通内蒙古", "联通内蒙古");
        this.customChild.addItemV("联通宁波", "联通宁波");
        this.customChild.addItemV("联通宁夏", "联通宁夏");
        this.customChild.addItemV("联通青岛", "联通青岛");
        this.customChild.addItemV("联通青海", "联通青海");
        this.customChild.addItemV("联通陕西", "联通陕西");
        this.customChild.addItemV("联通山东", "联通山东");
        this.customChild.addItemV("联通上海", "联通上海");
        this.customChild.addItemV("联通山西", "联通山西");
        this.customChild.addItemV("联通深圳", "联通深圳");
        this.customChild.addItemV("联通四川", "联通四川");
        this.customChild.addItemV("联通天津", "联通天津");
        this.customChild.addItemV("联通厦门", "联通厦门");
        this.customChild.addItemV("联通新疆", "联通新疆");
        this.customChild.addItemV("联通西藏", "联通西藏");
        this.customChild.addItemV("联通云南", "联通云南");
        this.customChild.addItemV("联通浙江", "联通浙江");
        this.customChild.addItemV("联通", "联通");
        this.custom.setBounds(100, 100, 500, 400);
        this.excelpathLabel.setBounds(100, 100, 500, 400);
        this.excelJFileChooser.setFileSelectionMode(0);
        this.excelJFileChooser.setMultiSelectionEnabled(false);
        this.excelJFileChooser.getDragEnabled();
        this.excelJFileChooser.setDialogTitle("请选择文件");
        this.excelJFileChooser.setFileFilter(filter);
        this.excelJFileChooser.showOpenDialog(new JFrame("请选择导入的文件"));
        this.panel1.setBorder(this.titledborder);
        this.panel1.add(this.area);
        this.panel1.add(this.areaComb);
        this.panel.add(this.panel1, "card1");
        this.custom.setPreferredSize(new Dimension(95, 35));
        this.panel2.setBorder(this.titledborder);
        this.custom.setHorizontalAlignment(4);
        this.panel2.add(this.custom);
        this.panel2.add(this.customparent);
        this.panel2.add(this.customChild);
        this.panel.add(this.panel2, "card12");
        this.panel3.setBorder(this.titledborder);
        this.fileLabel.setPreferredSize(new Dimension(95, 35));
        this.fileLabel.setHorizontalAlignment(4);
        this.panel3.add(this.fileLabel);
        if (this.excelJFileChooser.getSelectedFile() == null) {
            JOptionPane.showMessageDialog((Component)null, "请重新打开选择文件", "提示", 2);
            return;
        } else {
            this.filepathtext.setText(this.excelJFileChooser.getSelectedFile().getAbsolutePath());
            this.filepathtext.setFont(new Font("黑体", 1, 14));
            this.filepathtext.setSize(800, 28);
            this.filepathtext.setForeground(Color.BLACK);
            this.filepathtext.setEnabled(false);
            this.panel3.add(this.filepathtext);
            this.panel.add(this.panel3, "card3");
            this.submit.addActionListener(new Button3ActionListener(this));
            this.panel4.setBorder(this.titledborder);
            this.panel4.setLayout(new FlowLayout(1));
            this.panel4.add(this.submit);
            this.panel.add(this.panel4, "card4");
            this.panel.setSize(600, 300);
            this.panel.setBorder(this.titledborder);
        }
    }


}