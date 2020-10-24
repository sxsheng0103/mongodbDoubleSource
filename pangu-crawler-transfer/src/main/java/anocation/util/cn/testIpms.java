package anocation.util.cn;


import anocation.test.cn.testInterface;

/**
 * 
 * @author zhangshaojie
 * @deprecated  /p>
 *
 */
public class testIpms  implements testInterface{

	
	public final static  String  name  ="QINSHHUANG";
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getIds(String name) {
		String []  string = {"02","10022","32193"};
		return string;
	}

	@Override
	public double getCount() {
		return 0;
	}

	@Override
	public boolean isMethod() {
		return false;
	}

}
