package ReadDocument;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * Created by xiening1 on 2019/2/1.
 */
public class PlaceOrder {

    public static   JPanel mainSpan = new JPanel();
    public static   JPanel botSpan = new JPanel();
    public  static  JPanel row1 = new JPanel();
    public static   JPanel row2 = new JPanel();
    public static   JPanel row3 = new JPanel();
    public static   JPanel row4 = new JPanel();
    public static   JSplitPane mainScropan = new JSplitPane();
    public static BorderLayout borderLayout = new BorderLayout();
    public static   JLabel filetype = new JLabel("文件格式");
    static String[] selections = {"doc/docx","txt","pdf","xls"};
    public  static  JComboBox filecom = new JComboBox(selections);
    public static   JLabel ripidLable = new JLabel("语速");
    public static   JLabel volumeLable = new JLabel("音量");
    static Integer[] selections1 = {12, 18,24,36,42};
    static Integer[] selections2 = {-3, -2,-1,0,1,2,3};
    static Integer[] selections3 = {1,2,3,4,10,30, 50,70,90,100};
    public  static  JComboBox ripidcom = new JComboBox(selections2);
    public  static  JComboBox volumecom = new JComboBox(selections3);
    public static   JLabel fontsizeLable = new JLabel("字体大小");
    public static   JComboBox fontsizeCom = new JComboBox(selections1);
    public static   JLabel checkTagLable = new JLabel("请选择文件路径：");
    public  static   JFileChooser addChooser=new JFileChooser(".");
    public static  DataView dv = new DataView(new JFrame("文档阅读"));
    public static   JLabel trainsLable = new JLabel("火车车次");
    public static   JTextArea contentZone = new JTextArea();
    public static   JButton enterButton = new JButton("请选择文件");
    public  static  JButton Exit = new JButton("退出");
    public  static  JButton skip = new JButton("跳过当前行");
    public  static  ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
    public static Thread readThread =   null;
    public static Queue queue = new PriorityBlockingQueue();
    
  
    
    
    
    public static void displayTickits()throws IOException{
        mainSpan.setLayout(new BorderLayout());
        mainSpan.setSize(new Dimension(680, 450));
        botSpan.setLayout(new GridLayout(2, 1, 3, 3));
        ripidLable.setHorizontalAlignment(JLabel.LEFT);
        ripidcom.setAlignmentX(0);
        row1.add(filetype);
        row1.add(filecom);
        row1.add(ripidLable);
        row1.add(ripidcom);
        row1.add(fontsizeLable);
        row1.add(fontsizeCom);
        row1.add(volumeLable);
        row1.add(volumecom);
        addChooser.setMultiSelectionEnabled(true);
        addChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        row3.add(addChooser);
        row3.add(enterButton);
        row3.add(skip);
        row3.add(Exit);
        botSpan.add(row1);
//        botSpan.add(row2);
        botSpan.add(row3);
        
        contentZone.setBackground(Color.CYAN);
        contentZone.setFont(new Font("", Font.TYPE1_FONT, 20));
        contentZone.setAlignmentX(0.5f);
        contentZone.setAlignmentY(0.5f);
        mainSpan.add(contentZone);
        mainScropan.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainScropan.setOneTouchExpandable(true);
        mainScropan.setDividerLocation(418);
        mainScropan.add(mainSpan, JSplitPane.TOP);
        mainScropan.add(botSpan,JSplitPane.BOTTOM);
        Exit.addMouseListener(mlistener1);
        enterButton.addMouseListener(mlistener);
        skip.addMouseListener(mlistener3);
        fontsizeCom.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange()==ItemEvent.SELECTED) {
					contentZone.setFont(new Font("", Font.TYPE1_FONT, (Integer)e.getItem()));
					JOptionPane.showMessageDialog(new Frame(), "设置成功!", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
        ripidcom.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange()==ItemEvent.SELECTED) {
				sap.setProperty("Rate", new Variant(e.getItem()));
				JOptionPane.showMessageDialog(new Frame(), "设置成功!", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
		        
			}
		});
        volumecom.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange()==ItemEvent.SELECTED) {
				sap.setProperty("Volume", new Variant((Integer)e.getItem()));
				JOptionPane.showMessageDialog(new Frame(), "设置成功!", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
		        
			}
		});
//        mainSpan.setLayout(new GridLayout(4, 2, 5, 5));
       
//        mainSpan.add(row3);
//        mainSpan.add(row4);
        contentZone.setLineWrap(true);
        dv.getContentPane().setLayout(new BorderLayout());
        dv.add(mainScropan);
        dv.setSize(968, 568);
        dv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dv.setVisible(true);
        dv.show();

    }
    
    public static  MouseListener mlistener1 = new MouseListener() {
        

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource() == Exit&&e.getClickCount()==1){
				System.exit(-1);
            }
			
		}
    };
public static  MouseListener mlistener3 = new MouseListener() {
        

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource() == skip&&e.getClickCount()==1){
//					readThread.wait(0,10);
//					readThread.notifyAll();
					if(readThread!=null) {
						service.submit(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									readThread.suspend();
//									service.shutdown();
//									service.shutdownNow();
//									readThread.sleep(100000000,10);
//									readThread.notify();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						
					}
            }
			
		}
    };
    private static ExecutorService service = Executors.newCachedThreadPool(new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			// TODO Auto-generated method stub
			readThread = new Thread(r,"output");
			return readThread;
		}
	});
    
    public static  MouseListener mlistener = new MouseListener() {
        

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource() == enterButton&&e.getClickCount()==1){
				int returnval=addChooser.showOpenDialog(new Frame());  
//				PlaceOrder.addChooser.get
//                ReadWord.readWord(null);
				
				if(returnval==JFileChooser.APPROVE_OPTION)
                {
					service.submit(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							speachContents();
						}
					});
                }
            }
			
		}
    };
    
    
    public static void speachContents() {
//    	Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//			public void eventDispatched(AWTEvent event) {
//			}
//		}, AWTEvent.TEXT_EVENT_MASK);
    	 String addWords = "";
		 String centents ="";
	    // Dispatch是做什么的？
	    Dispatch sapo = sap.getObject();
	    try {
	        // 音量 0-100
	        sap.setProperty("Volume", new Variant(100));
	        // 语音朗读速度 -10 到 +10
	        sap.setProperty("Rate", new Variant(5));
	        Variant defalutVoice = sap.getProperty("Voice");
	        Dispatch dispdefaultVoice = defalutVoice.toDispatch();
	        Variant allVoices = Dispatch.call(sapo, "GetVoices");
	        Dispatch dispVoices = allVoices.toDispatch();

	        Dispatch setvoice = Dispatch.call(dispVoices, "Item", new Variant(1)).toDispatch();
	        ActiveXComponent voiceActivex = new ActiveXComponent(dispdefaultVoice);
	        ActiveXComponent setvoiceActivex = new ActiveXComponent(setvoice);

	        Variant item = Dispatch.call(setvoiceActivex, "GetDescription");
	        // 执行朗读
//	        Dispatch.call(sapo, "Speak", new Variant("张绍杰小朋友，早上好"));
	        File[] files=addChooser.getSelectedFiles();
	        StringBuffer str=new StringBuffer("");
	        String filepath="";
	        String[] listRow = null;
	        for (File file : files) {
	        	filepath=file.getPath();
	            str.append(filepath+"\n");
//	            ReadWord.readWord(filepath);
	            centents=ReadWord.readWord(filepath);
	            listRow = centents.split("\\n");
	            speech :for(int i =0;i<listRow.length;i++) {
	            	System.out.println();
	            	final String row=listRow[i];
//	            	System.out.println(countPath(listRow[i])>3);
	            	if(countPath(row)>3) {continue speech;}else {            		
//	            		ridersText.paintImmediately(ridersText.getBounds());
//	            		mainSpan.validate();
//	            		mainSpan.repaint();	            		
	            		contentZone.setText("\r\n"+row+"\r\n");
//	            		service.submit(new Runnable() {
//							@Override
//							public void run() {
//								
//								
//							}
//						});
	            		
	            		contentZone.setCaretPosition(contentZone.getText().length());
//	            		ridersText.paintImmediately(ridersText.getX(), ridersText.getY(), ridersText.getWidth(), ridersText.getHeight());
	            		Dispatch.call(sapo, "Speak", new Variant(listRow[i]));//centents
	            	}
	            }
	        }
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    } finally {
	        sapo.safeRelease();
	        sap.safeRelease();
	    }
        
    
    }
    
    public static int countPath(String row) {
//    	int i = 0;
//    	while(row.index("\\")>0) {
//    		i++;
//    		row = row.replace("\\", "");
//    		
//    	}
    	int i=0,index = -1,count=0,j=row.length();
    	String temp = row;
    	while((temp.lastIndexOf("\\"))!=-1){
    		temp=temp.substring(0, temp.lastIndexOf("\\"));
    		count++;
    		if(count>3)break;
    	}
    			
    	return count;
    	
    }

  

  
    public static void main(String[] args)throws IOException{
    	
    	displayTickits();
    }

 
}
class DataView extends JFrame{
    DataView(JFrame j){}

}