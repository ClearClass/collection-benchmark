package lib.clearclass;

import java.io.PrintStream;
import java.util.*;
import com.mathworks.engine.*;

class Benchmark {
	// максимальное количество элементов в коллекции
	private static int itr;
	
	// шаг измерений (по количеству элементов)
	private static int step;
	
	// общее количество точек измерения
	private static int num;
	
	// массив точек измерения
	private static int[] x;
	
	private static String matlabInstance;
	
	private static Process gnuplot;
	private static PrintStream std; // стандартный поток gnuplot
	private static int n = 0; // счетчик графиков gnuplot
	
	static void init(int itr, int step){
		Benchmark.itr = itr;
		Benchmark.step = step;
		num = itr/step;
		x = new int[num];
		for(int k = 0; k < num; k++)
			x[k]=(k+1)*step;
	}
	
	static {
		init(100000, 100);
		
		try {
			String[] matlabInstances = MatlabEngine.findMatlab();
			matlabInstance = matlabInstances[0];
		} catch (Throwable e) {
			System.err.println("Shared Matlab instance not found: " + e.getMessage());
		} 
		
		try {
			String[] cmd = {"gnuplot.exe", "-persist"};
			gnuplot = Runtime.getRuntime().exec(cmd);
			std = new PrintStream(gnuplot.getOutputStream());
		} catch (Exception e) {
			System.err.println("Failed to start gnuplot process: " + e.getMessage());
		} 
	}

	static void mplot(double[] y1, String name1, double[] y2, String name2, String title) {
		int LineWidth = 1;
		double[] color1 = {0, 0.4, 0.8};
		double[] color2 = {0.8, 0, 0.2};
		
		try {
			MatlabEngine eng = MatlabEngine.connectMatlab(matlabInstance);
			try {
				eng.eval("clear;");
				eng.putVariable("LineWidth", LineWidth);
				eng.putVariable("color1", color1);
				eng.putVariable("color2", color2);
				eng.putVariable("x", x);
				eng.putVariable("y1", y1);
				eng.putVariable("y2", y2);
				eng.putVariable("name1", name1);
				eng.putVariable("name2", name2);
				eng.eval("figure(); plot(x, y1, 'Color', color1, 'LineWidth', LineWidth);");
				eng.eval("hold on; grid on;");
				eng.eval("plot(x, y2, 'Color', color2, 'LineWidth', LineWidth);");
				String legend = "'Location','northwest','FontWeight','bold','FontAngle','italic','FontSize',12,'FontName','Courier New');";
				eng.eval("legend({'" + name1 + "','" + name2 + "'}," + legend);
				eng.eval("xlabel('Размер коллекции','FontName','Arial','FontAngle','italic');");
				eng.eval("ylabel('Время выполнения, мкс','FontName','Arial','FontAngle','italic');");
				eng.eval("title('" + title + "','FontWeight','bold','FontName','Arial');");
			} finally {eng.close();}
		} catch (Exception e) {
			System.err.println("plot error: " + title);
		}
	}
	
	private enum FontSize {
		Little(14, 12, 12), Big(22, 18, 16);
		final int t; // title
		final int l; // xlabel, ylabel
		final int k; // key
		private FontSize(int t, int l, int k) {
			this.t = t;
			this.l = l;
			this.k = k;
		}
	}
	
	static void gplot(double[] y1, String name1, double[] y2, String name2, String title) {
		double linewidth = 2;
		
		String color1 = "yellow4";
		String color2 = "#c04070"; // узнать все цвета: show colornames
		boolean defaultColor = true;
		
		std.println("set terminal wxt " + n++);
		std.print("plot '-' using 1:2 with lines ");
		if(!defaultColor) std.print("linecolor rgb \"" + color1 + "\" ");
		std.print("linewidth " + linewidth + " ");
		std.print("title \"" + name1 + "\", ");
		std.print("'-' using 1:2 with lines ");
		if(!defaultColor) std.print("linecolor rgb \"" + color2 + "\" ");
		std.print("linewidth " + linewidth + " ");
		std.print("title \"" + name2 + "\"\n");
		for(int k = 0; k < num; k++)
			std.println(x[k] + " " + y1[k]);
		std.println("e");
		for(int k = 0; k < num; k++)
			std.println(x[k] + " " + y2[k]);
		std.println("e");
		std.print("set grid;");
		FontSize fz = FontSize.Little;
		std.print("set title \"" + title + "\" font \"Times New Roman, " + fz.t + "\";");
		std.print("set xlabel \"Размер коллекции\" font \"Times New Roman, " + fz.l + " \";");
		std.print("set ylabel \"Время выполнения, мкс\" font \"Times New Roman, " + fz.l + "\";");
		std.print("set key top left font \"Courier New, " + fz.k + "\";");
		std.println("replot;");
		std.flush();
	}
	
	static void gplot(double[] y1, String name1, double[] y2, String name2, String title, double ymin, double ymax) {
		gplot(y1, name1, y2, name2, title);
		std.print("set yrange ["+ ymin +":"+ ymax +"];");
		std.println("replot;");
		std.flush();
	}
	
	static void gplot(double[] y1, String name1, double[] y2, String name2, String title, double xmin, double xmax, double ymin, double ymax) {
		gplot(y1, name1, y2, name2, title, ymin, ymax);
		std.print("set xrange ["+ xmin +":"+ xmax +"];");
		std.println("replot;");
		std.flush();
	}
	
	// закрыть ресурсы gnuplot
	static void gclose(){
		std.close();
	}
	
	static void gclose(int pause){
		std.close();
		try {
			Thread.sleep(pause);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gnuplot.destroy();
	}
	
	// ====================================================== СПИСКИ (List)
	// ------------------------------------------------------ Операции чтения
		
	// тест "определение размера позиционного списка"
	static double[] getSize(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				long t1 = System.nanoTime();
				list.size();
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// тест "чтение нулевого элемента"
	static double[] readZero(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		final int index = 0;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				long t1 = System.nanoTime();
				list.get(index);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// тест "чтение центрального элемента"
	static double[] readCenter(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		int index;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				index = i/2;
				long t1 = System.nanoTime();
				list.get(index);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}

	// тест "чтение последнего элемента"
	static double[] readEnd(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		int index;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				index = i-1;
				long t1 = System.nanoTime();
				list.get(index);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// ------------------------------------------------------ Операции вставки
	
	// тест "вставка в начало"
	static double[] add2zero(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		final int index = 0;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				long t1 = System.nanoTime();
				list.add(index,1);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
				list.remove(index);
			}
		}
		return y;
	}
	
	// тест "вставка в середину"
	static double[] add2center(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		int index;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				index = i/2;
				long t1 = System.nanoTime();
				list.add(index,1);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
				list.remove(index);
			}
		}
		return y;
	}
	
	// тест "вставка в конец"
	static double[] add2end(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		int index;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				index = i; // вставка в свободную позицию
				long t1 = System.nanoTime();
				list.add(index,1);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
				list.remove(index);
			}
		}
		return y;
	}
	
	// ------------------------------------------------------ Поиск
	
	// тест "линейный поиск"
	static double[] find(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		Random r = new Random();
		int range = itr*100;
		for (int i = 1; i<=itr; i++) {
			list.add(r.nextInt(range));
			if (i%step==0) {
				int q = r.nextInt(range);
				long t1 = System.nanoTime();
//				list.contains(q);
				list.indexOf(q);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// тест "выборка всех элементов"
	static double[] readAll(List<Integer> list){
		list.clear();
		double[] y = new double[num];
		int k = 0;
		for (int i = 1; i<=itr; i++) {
			list.add(1);
			if (i%step==0){
				Iterator<Integer> it = list.iterator();
				long t1 = System.nanoTime();
				for (int index = 0; index<i; index++)
					it.next();
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// ====================================================== МНОЖЕСТВА (Set)
	
	// тест "вставка"
	static double[] add(Set<Integer> set){
		set.clear();
		double[] y = new double[num];
		int k = 0; // счетчик записи в y[]
		Random r = new Random();
		for (int i = 1; i<=itr; i++) {
			set.add(i);
			if (i%step==0){
				int q = r.nextInt(i)+1; // диапазон [1..i] включительно
				set.remove(q);
				long t1 = System.nanoTime();
				set.add(q);
				long t2 = System.nanoTime();
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// вставка с усреднением
	static double[] add_(Set<Integer> set) {
		int meanNum = 1000;
		double[] y = new double[num];
		for (int i = 0; i < meanNum; i++) {
			double[] z = add(set);
			for (int j = 0; j < num; j++)
				y[j]+=z[j];
		}
		for (int j = 0; j < num; j++) 
			y[j]/=meanNum;
		return y;
	}
	
	// ----------------------------------------------------------------------
	
	// тест "поиск"
	static double[] find(Set<Integer> set){
		double[] y = new double[num];
		int k = num; // счетчик записи в y[]
		Random r = new Random();
		int range = itr*100; // диапазон значений случайной величины
		for (int i = itr; i>0; i-=step) {
			set.clear();
			while(set.size()<i)
				set.add(r.nextInt(range));
			int q = r.nextInt(range);
			int repeat = 2000;
			long t1 = System.nanoTime();
			for (int j = 0; j < repeat; j++)
				set.contains(q);
			long t2 = System.nanoTime();
			double dt=t2-t1;
			y[--k]=dt/1000/repeat; //время в мкс
		}
		return y;
	}
	
	// поиск с усреднением
	static double[] find_(Set<Integer> set){
		int meanNum = 1000;
		double[] y = new double[num];
		for (int i = 0; i < meanNum; i++) {
			double[] z = find(set);
			for (int j = 0; j < num; j++)
				y[j]+=z[j];
			if(i%100==0) System.out.println(meanNum-i);
		}
		for (int j = 0; j < num; j++) 
			y[j]/=meanNum;
		return y;
	}
	
	// ----------------------------------------------------------------------
	
	// тест "удаление"
	static double[] remove(Set<Integer> set){
		set.clear();
		double[] y = new double[num];
		int k = 0; // счетчик записи
		Random r = new Random();
		for (int i = 1; i<=itr; i++) {
			set.add(i);
			if (i%step==0) {
				int q = r.nextInt(i)+1; // диапазон [1..i] включительно
				long t1 = System.nanoTime();
				set.remove(q);
				long t2 = System.nanoTime();
				set.add(q);
				double dt=t2-t1;
				y[k++]=dt/1000; //время в мкс
			}
		}
		return y;
	}
	
	// удаление с усреднением
	static double[] remove_(Set<Integer> set){
		int meanNum = 1000;
		double[] y = new double[num];
		for (int i = 0; i < meanNum; i++) {
			double[] z = remove(set);
			for (int j = 0; j < num; j++)
				y[j]+=z[j];
			if (i%100==0) System.out.println(meanNum-i);
		}
		for (int j = 0; j < num; j++) 
			y[j]/=meanNum;
		return y;
	}
	
	// ====================================================== ОТОБРАЖЕНИЯ (Map)
	
	// ====================================================== Потокобезопасные коллекции
}

// пользовательский класс для хранения в коллекции
class A implements Comparable<A>{
	Integer a1;
	Integer a2;
	Integer a3;
	
	A(Integer a1, Integer a2, Integer a3) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
	}

	@Override
	public int compareTo(A ob) {
		if (a1>ob.a1)
			return 1;
		if (a1<ob.a1)
			return -1;
		if (a2>ob.a2)
			return 1;
		if (a2<ob.a2)
			return -1;
		if (a3>ob.a3)
			return 1;
		if (a3<ob.a3)
			return -1;
		return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a1;
		result = prime * result + a2;
		result = prime * result + a3;
		return result;
	}

	@Override
	public boolean equals(Object ob) {
		if (this == ob)
			return true;
		if (ob == null)
			return false;
		if (getClass() != ob.getClass())
			return false;
		A other = (A) ob;
		if (a1 != other.a1)
			return false;
		if (a2 != other.a2)
			return false;
		if (a3 != other.a3)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + a1 + " " + a2 + " " + a3 + "]";
	}
}

public class Main {
	public static void main(String[] args) {
		List<Integer> al = new ArrayList<>();
		List<Integer> ll = new LinkedList<>();
		
		double[] alm;
		double[] llm;
		
		alm = Benchmark.getSize(al);
		llm = Benchmark.getSize(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Определение размера списка");
		
		alm = Benchmark.readZero(al);
		llm = Benchmark.readZero(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Чтение нулевого элемента списка");
		
		alm = Benchmark.readCenter(al);
		llm = Benchmark.readCenter(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Чтение центрального элемента списка");
		
		alm = Benchmark.readEnd(al);
		llm = Benchmark.readEnd(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Чтение последнего элемента списка");
		
		alm = Benchmark.add2zero(al);
		llm = Benchmark.add2zero(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Вставка в начало");
		
		alm = Benchmark.add2center(al);
		llm = Benchmark.add2center(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Вставка в середину");
		
		alm = Benchmark.add2end(al);
		llm = Benchmark.add2end(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Вставка в конец");
		
		alm = Benchmark.find(al);
		llm = Benchmark.find(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Линейный поиск", 0, 350);

		alm = Benchmark.readAll(al);
		llm = Benchmark.readAll(ll);
		Benchmark.gplot(alm, "ArrayList", llm, "LinkedList", "Чтение всех элементов списка");
		
//		======================================================================================================
		Set<Integer> hs = new HashSet<>();
		Set<Integer> ts = new TreeSet<>();
//		Set<Integer> ls = new LinkedHashSet<>();
				
//		Set<A> hs = new HashSet<>();
//		Set<A> ts = new TreeSet<>();
//		Set<A> ls = new LinkedHashSet<>();
		
		Benchmark.init(600, 1); // для множеств устанавливаем itr = 600, step = 1 
		
		double[] hsm;
		double[] tsm; 
//		double[] lsm;
		
		hsm = Benchmark.add_(hs);
		tsm = Benchmark.add_(ts);
		Benchmark.gplot(hsm, "HashSet", tsm, "TreeSet", "Вставка в множество", 0.8, 1.05);
		
		hsm = Benchmark.find_(hs);
		tsm = Benchmark.find_(ts);
		Benchmark.gplot(hsm, "HashSet", tsm, "TreeSet", "Поиск в множестве");
		
		hsm = Benchmark.remove_(hs);
		tsm = Benchmark.remove_(ts);
		Benchmark.gplot(hsm, "HashSet", tsm, "TreeSet", "Удаление из множества");
		
		Benchmark.gclose();
	}
}
