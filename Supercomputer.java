import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Supercomputer {
	static class Task {
		public static final String INPUT_FILE = "supercomputer.in";
		public static final String OUTPUT_FILE = "supercomputer.out";

		// numarul maxim de noduri
		public static final int NMAX = (int) 1e5 + 5; // 10^5 + 5 = 100.005

		// n = numar de noduri, m = numar de muchii.
		int n, m;
		// Sirul in care sunt stocate codurile pentru fiecare nod.
		int[] codes;

		// Graful reprezentat prin liste de adiacenta.
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adj = new ArrayList[NMAX];

		// Sirul in care sunt stocate gradele interne pentru fiecare nod.
		int[] inDegree;

		// Acelasi sir ca mai sus.
		int[] inDegree2;

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				MyScanner sc = new MyScanner(new BufferedReader(new FileReader(INPUT_FILE)));
				n = sc.nextInt();
				m = sc.nextInt();

				codes = new int[n + 1];

				for (int i = 1; i <= n; i++) {
					codes[i] = sc.nextInt();
				}

				for (int i = 1; i <= n; i++) {
					adj[i] = new ArrayList<>();
				}

				inDegree = new int[n + 1];
				inDegree2 = new int[n + 1];

				for (int i = 1; i <= m; i++) {
					int x, y;
					x = sc.nextInt();
					y = sc.nextInt();
					adj[x].add(y);
					++inDegree[y];
					++inDegree2[y];
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void writeOutput(int count) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
				bw.write(count + "\n");
				bw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/*
		 * Functie preluata din laboratorul de PA pentru sortare topologica si modificata
		 * pentru nevoile problemei. Functia intoarce sortarea topologica in care prima
		 * data sunt alese elemente cu codul 1.
		 */
		private ArrayList<Integer> solve1(int[] inDegree) {

			// Coada in care voi baga de fiecare data nodurile cu codul 1.
			Queue<Integer> queueForOne = new ArrayDeque<>();

			// Coada in care voi baga de fiecare data nodurile cu codul 2.
			Queue<Integer> queueForTwo = new ArrayDeque<>();
			
			// Lista in care stochez rezultatul.
			ArrayList<Integer> topSort = new ArrayList<>();

			// Initial bag fiecare nod cu grad intern 0 in coada corespunzatoare
			// codului pe care il are.
			for (int node = 1; node <= n; node++) {
				if (inDegree[node] == 0 && codes[node] == 1) {
					queueForOne.add(node);
				} else if (inDegree[node] == 0  && codes[node] == 2) {
					queueForTwo.add(node);
				}
			}
			
			// Cat timp coada cu nodurile ce au cod 1 nu este goala
			while (!queueForOne.isEmpty()) {

				// extrag primul nod din coada si il si elimin in acelasi timp
				int node = queueForOne.poll();

				// dupa care care il bag in rezultat.
				topSort.add(node);

				// Pentru fiecare vecin al nodului extras
				for (int neigh : adj[node]) {

					// decrementez gradul intern al acestuia
					--inDegree[neigh];

					// dupa care il bag in coada corespunzatoare codului sau.
					if (inDegree[neigh] == 0 && codes[neigh] == 1) {
						queueForOne.add(neigh);
					}  else if (inDegree[neigh] == 0 && codes[neigh] == 2) {
						queueForTwo.add(neigh);
					}
				}

				// Daca s-a golit coada ce continea doar nodurile cu codul 1
				if (queueForOne.isEmpty()) {

					// Repet toate operatiile anterioare pe coada ce contine
					// nodurile ce au codul 2.
					while (!queueForTwo.isEmpty()) {
						int node1 = queueForTwo.poll();
						topSort.add(node1);

						for (int neigh : adj[node1]) {
							--inDegree[neigh];

							if (inDegree[neigh] == 0 && codes[neigh] == 2) {
								queueForTwo.add(neigh);
							} else if (inDegree[neigh] == 0 && codes[neigh] == 1) {
								queueForOne.add(neigh);
							}
						}
					}
				}
			}

			// Intorc sortarea astfel determinata.
			return topSort;
		}

		/*
		 * Aceeasi functie ca cea anterioara, doar ca de data aceasta incepem cu nodurile
		 * ce au codul 2.
		 */
		private ArrayList<Integer> solve2(int[] inDegree) {
			Queue<Integer> queueForOne = new ArrayDeque<>();
			Queue<Integer> queueForTwo = new ArrayDeque<>();
			ArrayList<Integer> topSort = new ArrayList<>();

			for (int node = 1; node <= n; node++) {
				if (inDegree[node] == 0 && codes[node] == 1) {
					queueForOne.add(node);
				} else if (inDegree[node] == 0  && codes[node] == 2) {
					queueForTwo.add(node);
				}
			}
			
			while (!queueForTwo.isEmpty()) {
				int node = queueForTwo.poll();
				topSort.add(node);

				for (int neigh : adj[node]) {
					--inDegree[neigh];

					if (inDegree[neigh] == 0 && codes[neigh] == 2) {
						queueForTwo.add(neigh);
					}  else if (inDegree[neigh] == 0 && codes[neigh] == 1) {
						queueForOne.add(neigh);
					}
				}

				if (queueForTwo.isEmpty()) {
					while (!queueForOne.isEmpty()) {
						int node1 = queueForOne.poll();
						topSort.add(node1);

						for (int neigh : adj[node1]) {
							--inDegree[neigh];

							if (inDegree[neigh] == 0 && codes[neigh] == 1) {
								queueForOne.add(neigh);
							} else if (inDegree[neigh] == 0 && codes[neigh] == 2) {
								queueForTwo.add(neigh);
							}
						}
					}
				}
			}

			return topSort;
		}

		/*
		 * Functie ce rezolva problema intorcand numarul minim de context switch-uri cerute.
		 */
		private int getResult() {

			// Prima sortare topologica ce incepe cu nodurile ce au codul 2.
			ArrayList<Integer> first = solve1(inDegree);

			// A doua sortare topologica ce incepe cu nodurile ce au codul 1.
			ArrayList<Integer> second = solve2(inDegree2);

			// Vectori in care retin pentru fiecare din sortarile topologice
			// de mai sus noua aranjare a codurilor.
			int[] topsortCodes = new int[codes.length];
			int[] topsortCodes2 = new int[codes.length];

			for (int i = 0; i < first.size(); i++) {
				topsortCodes[i] = codes[first.get(i)];
			}

			for (int i = 0; i < second.size(); i++) {
				topsortCodes2[i] = codes[second.get(i)];
			}

			// Doua variabile in care retin numarul de context switch-uri
			// pentru fiecare dintre sortari.
			int count1 = 0;
			int count2 = 0;

			// Determin numarul de context switch-uri pentru fiecare sortare
			// topologica prin compararea elementului de pe pozitia curenta cu
			// cel de pe pozitia anterioara(pentru a contoriza de cate ori se
			// trece din 1 in 2 si invers).
			for (int i = 1; i < topsortCodes.length; i++) {
				if (topsortCodes[i - 1] != topsortCodes[i]) {
					count1++;
				}

				if (topsortCodes2[i - 1] != topsortCodes2[i]) {
					count2++;
				}
			}

			// La final intorc rezultatul dupa urmatoarele cazuri
			// 1. Se poate incepe atat cu noduri cu 1 cat si cu noduri cu 2 in sortare,
			//    motiv pentru care intoarcem minimul dintre ele.
			// 2. Se poate incepe doar cu noduri cu 1 in sortare, motiv pentru care intoarcem
			// 	  doar numarul obtinut din prima sortare.
			// 3. Se poate incepe doar cu noduri cu 2 in sortare, motiv pentru care intoarcem
			//    doar numarul obtinut din a doua sortare.
			// 4. Default, intoarcem 0.
			if (count2 > 1 && count1 > 1) {
				return Math.min(count1 - 1, count2 - 1);
			} else if (count2 <= 1 && count1 > 1) {
				return count1 - 1;
			} else if (count2 > 1 && count1 <= 1) {
				return count2 - 1;
			} else {
				return 0;
			}
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}

	/*
	 * Clasa preluata din scheletul temei 1. Folosita pentru o citire mai eficienta.
	 */
	private static class MyScanner {
		private BufferedReader br;
		private StringTokenizer st;

		public MyScanner(Reader reader) {
			br = new BufferedReader(reader);
		}

		public String next() {
			while (st == null || !st.hasMoreElements()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return st.nextToken();
		}

		public int nextInt() {
			return Integer.parseInt(next());
		}

		public long nextLong() {
			return Long.parseLong(next());
		}

		public double nextDouble() {
			return Double.parseDouble(next());
		}

		public String nextLine() {
			String str = "";
			try {
				str = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return str;
		}
	}
}
