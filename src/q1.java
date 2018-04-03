
public class q1 {
	public static int[][] map = new int[30][30];
	
	
	
	public static void main(String[] args) {
		
		
		
		for (int[] is : map) {
			System.out.print("|");
			for (int i : is) {
				if (i == 0) {
					System.out.print(" ");//nothing
				} else if (i == 1) {
					System.out.print(".");//obstacle
				} else if (i == 2) {
					System.out.print("x");//goal
				} else if (i == 3) {			
					System.out.print("@");//character
				} else {
					System.out.print("e");//error
				}
			}
			System.out.println("|");
		}
	}
}
