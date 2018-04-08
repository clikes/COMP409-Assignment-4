import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;



class Character extends Thread{
	public Integer[] Position;
	public Integer[] Goal;
	private LinkedList<Integer[]> path;
	public boolean isGoal = false;
	
	private int[][] mark = new int[30][30];
	
	public Character(Integer[] position) {
		this.Position = position;
		path = new LinkedList<Integer[]>();
		Goal = null;
	}
	
	public void addGoal() {
		int x;
		int y;
		do {
			x = (int)(0.5+Math.random()*29);
			y = (int)(0.5+Math.random()*29);
		} while (!q1.AtomicMap[x].compareAndSet(y, 0, 2));
		Goal = new Integer[2];
		Goal[0] = x;
		Goal[1] = y;
	}
	
	public void changeGoal() {
		if (Goal != null) {
			//q1.AtomicMap[Goal[0]].compareAndSet(Goal[1], 2, 0);
		}
		int x, y, dx,dy;
		do {
			do {
				x = (int)(0.5+Math.random()*7);
				y = (int)(0.5+Math.random()*7);
				dx = x + Position[0] - 3;
				dy = y + Position[1] - 3;
			} while (!(dx <= 29 && dy <= 29 && dx >=0 && dy >= 0 ));
		} while (q1.AtomicMap[dx].compareAndSet(dy, 1, 1));//if goal is obstacle
		Integer[] newgoal = {dx,dy};
		Goal = newgoal;
	}
	
	public void FindPath() {
		FindPathDFS(Position,Goal);
	}
	
	public boolean FindPathDFS(Integer[] position, Integer[] goal) {
		int x = position[0];
		int y = position[1];
		int gx = goal[0];
		int gy = goal[1];
		//System.out.println(gx);
		q1.AtomicMap[x].compareAndSet(y, 0, 4);
		mark[x][y] = 1;
		if (x == gx && y == gy) {
			path.add(position);
			return true;
		}
		Integer[] newPosition = new Integer[2];
		int[] dx = {1,1,1,0,0,-1,-1,-1};
		int[] dy = {1,0,-1,1,-1,1,0,-1};
		for (int i = 0; i < 8; i++) {
			int newx = x+dx[i];
			int newy = x+dy[i];
			if (!(newx<=29&&newx>=0&&newy>=0&&newy<=29)) {
				continue;
			}
			if (q1.AtomicMap[newx].get(newy) != 1 && mark[newx][newy] == 0) {//no obstacle and no marked
				newPosition[0] = newx;
				newPosition[1] = newy;
				if (FindPathDFS(newPosition, goal)) {
					path.add(position);
					q1.AtomicMap[newx].set(newy, 4);
					System.out.println(1);
					return true;
				};
			}
			
		}
		return false;
	}
	
	
	public boolean MoveToGoal() {
		int dx;
		int dy;
		dx = Goal[0]-Position[0];
		if (dx > 0) {
			dx = 1;
		} else if (dx < 0) {
			dx = -1;
		} else {
			dx = 0;
		}
		
		dy = Goal[1]-Position[1];
		if (dy > 0) {
			dy = 1;
		} else if (dy < 0) {
			dy = -1;
		} else {
			dy = 0;
		}
		if (dx == 0 && dy == 0) {
			q1.AtomicMap[Position[0]].compareAndSet(Position[1], 3, 2);
			isGoal = true;
			return false;//break loop
		}
		if (q1.AtomicMap[Position[0]+dx].compareAndSet(Position[1]+dy, 0, 3)) {
			q1.AtomicMap[Position[0]].compareAndSet(Position[1], 3, 0);
			Position[0] += dx;
			Position[1] += dy;
			return true;
		}
		changeGoal();//change goal
		return true;//success or fail all need continue wait one move
	}
	
	
	
	@Override
	public void run() {
		try {
			while (MoveToGoal()) {
				q1.printMap(Goal);
				sleep(q1.k*(int)(Math.random()*4));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}


public class q1 {
	public static int[][] map = new int[30][30];
	public static AtomicIntegerArray[] AtomicMap = new AtomicIntegerArray[30];
	public static int k;
	
	public synchronized static void printMap(Integer[] Goal) {
		for (int i = 0; i < 32; i++) {
			System.out.print("_");
		}
		System.out.println();
		
		for (int j = 0 ; j < 30; j ++) {//print map
			AtomicIntegerArray line = q1.AtomicMap[j];
			System.out.print("|");
			for (int i = 0; i < 30; i++) {
				int value = line.get(i);
				if (j == Goal[0]&&i==Goal[1]) {
					System.out.print("X");
					continue;
				}
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
		for (int i = 0; i < 32; i++) {
			System.out.print("-");
		}
		System.out.println();
	}
	
	public synchronized static void printMap() {
		for (int i = 0; i < 32; i++) {
			System.out.print("_");
		}
		System.out.println();
		for (AtomicIntegerArray line : AtomicMap) {//print map
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
		for (int i = 0; i < 32; i++) {
			System.out.print("-");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		int n,p,r;
		
		for (int i = 0; i < 30; i++) {				//init map
			AtomicMap[i] = new AtomicIntegerArray(30);
		}
		LinkedList<Character> characters = new LinkedList<Character>();
		
		r = 100;
		for (int i = 0; i < r; i++) {
			int x = (int)(1+Math.random()*28);
			int y = (int)(1+Math.random()*28);
			if (!AtomicMap[x].compareAndSet(y, 0, 1)) {
				i--;
			}
		}
		
		n = 20;
		ArrayBlockingQueue<Runnable> allCharacters = new ArrayBlockingQueue<Runnable>(n);
		for (int i = 0; i < n; i++) {
			int x = (int)(0.5+Math.random()*30);
			int y = (int)(0.5+Math.random()*30);
			if (Math.random()>0.5) {
				if (Math.random() > 0.5 ) {
					x = 0;
				} else {
					x = 29;
				}
			} else {
				if (Math.random() > 0.5 ) {
					y = 0;
				} else {
					y = 29;
				}
			}
			
			if (!AtomicMap[x].compareAndSet(y, 0, 3)) {
				i--;
			} else {
				Integer[] position = {x,y};
				characters.add(new Character(position));
				characters.getLast().changeGoal();
			}
		}
		
		p = 2;
		ThreadPoolExecutor test = new ThreadPoolExecutor(0, p, 200, TimeUnit.MILLISECONDS, allCharacters);
		
		
		k = 1000;
		for (Character character : characters) {
			character.start();
		}
		try {
			
			for (Character character : characters) {
				character.join();
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
		}
		
		//characters.get(0).FindPath();
		printMap() ;
		
		
		
		
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
