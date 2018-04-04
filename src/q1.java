import java.util.concurrent.atomic.AtomicIntegerArray;

public class q1 {
	public static int[][] map = new int[30][30];
	public static AtomicIntegerArray[] AtomicMap = new AtomicIntegerArray[30];
	
	
	public static void main(String[] args) {
		
		int n,p,r,k;
		
		for (int i = 0; i < 30; i++) {
			AtomicMap[i] = new AtomicIntegerArray(30);
		}
		r = 100;
		for (int i = 0; i < r; i++) {
			int x = (int)(1+Math.random()*28);
			int y = (int)(1+Math.random()*28);
			if (!AtomicMap[x].compareAndSet(y, 0, 1)) {
				i--;
			}
		}
		
		for (AtomicIntegerArray line : AtomicMap) {
			System.out.print("|");
			for (int i = 0; i < 30; i++) {
				int value = line.get(i);
				if (value == 0) {
					System.out.print(" ");//nothing
				} else if (value == 1) {
					System.out.print(".");//obstacle
				} else if (value == 2) {
					System.out.print("x");//goal
				} else if (value == 3) {			
					System.out.print("@");//character
				} else {
					System.out.print("e");//error
				}
			}
			System.out.println("|");
		}
		
//		for (int[] is : map) {
//			System.out.print("|");
//			for (int i : is) {
//				if (i == 0) {
//					System.out.print(" ");//nothing
//				} else if (i == 1) {
//					System.out.print(".");//obstacle
//				} else if (i == 2) {
//					System.out.print("x");//goal
//				} else if (i == 3) {			
//					System.out.print("@");//character
//				} else {
//					System.out.print("e");//error
//				}
//			}
//			System.out.println("|");
//		}
	}
}
