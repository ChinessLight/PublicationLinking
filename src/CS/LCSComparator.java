package CS;


/**
 * //比较两个字符串的最长公共子序列，非连续
 */
public class LCSComparator {
	public static char[] x;
	public static char[] y;
	public static int[][] c=new int [200] [200];
	public static int[][] b=new int [200] [200];
	public static int length=0;
	
	public static void LCSLength(int m, int n)
	{
	    int i, j;
	    
	    for(i = 0; i <= m; i++)
	        c[i][0] = 0;
	    for(j = 1; j <= n; j++)
	        c[0][j] = 0;
	    for(i = 1; i<= m; i++)
	    {
	        for(j = 1; j <= n; j++)
	        {
	            if(x[i-1] == y[j-1])
	            {
	                c[i][j] = c[i-1][j-1] + 1;
	                b[i][j] = 0;
	            }
	            else if(c[i-1][j] >= c[i][j-1])
	            {
	                c[i][j] = c[i-1][j];
	                b[i][j] = 1;
	            }
	            else
	            {
	                c[i][j] = c[i][j-1];
	                b[i][j] = -1;
	            }
	        }
	    }
	}

	/*
	 * 计算出两个字符串的最长公共子序列长度
	 */
	public static void PrintLCS(int i, int j)
	{
	    if(i == 0 || j == 0)
	        return;
	    if(b[i][j] == 0)
	    {
	    	length++;
	        PrintLCS(i-1, j-1);
	    }
	    else if(b[i][j] == 1)
	        PrintLCS( i-1, j);
	    else
	        PrintLCS( i, j-1);
	}
	
	
	/*
	 * 比较两个String的LCS相似度
	 */
	public static double  compute(String String1,String String2){
		LCSComparator lcsCompare=new LCSComparator();
		lcsCompare.length=0;
		lcsCompare.x=String1.toCharArray();
		lcsCompare.y=String2.toCharArray();
		lcsCompare.LCSLength(lcsCompare.x.length, lcsCompare.y.length);
		lcsCompare.PrintLCS(lcsCompare.x.length, lcsCompare.y.length);
		int maxLength=lcsCompare.x.length>lcsCompare.y.length?lcsCompare.x.length:lcsCompare.y.length;
		return (double)lcsCompare.length/(double)maxLength;
	}
	
	
	 public static void main(String[] args){
		String xx="中国科学院等离子体物理研究所";
		String yy="食品科";
		x=xx.toCharArray();
		y=yy.toCharArray();
		LCSLength(x.length,y.length);
	    PrintLCS(x.length,y.length);
	    System.out.println((double)length/(double)y.length);
	}
}
