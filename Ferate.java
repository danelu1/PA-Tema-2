import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

public class Ferate {
	static class Task {
		static final String INPUT_FILE = "ferate.in";
		static final String OUTPUT_FILE = "ferate.out";

		// numarul maxim de noduri
		static final int NMAX = (int) 1e5 + 5; // 10^5 + 5 = 100.005

		// n = numar de noduri, m = numar de muchii.
		int n, m, s;

		// Graful reprezentat prin liste de adiacenta.
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adj = new ArrayList[NMAX];

		// Graful transpus.
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] adjTrans = new ArrayList[NMAX];

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				MyScanner sc = new MyScanner(new BufferedReader(new FileReader(INPUT_FILE)));
				n = sc.nextInt();
				m = sc.nextInt();
				s = sc.nextInt();

				for (int i = 1; i <= n; i++) {
					adj[i] = new ArrayList<>();
					adjTrans[i] = new ArrayList<>();
				}

				for (int i = 1; i <= m; i++) {
					int x, y;
					x = sc.nextInt();
					y = sc.nextInt();
					adj[x].add(y);
					adjTrans[y].add(x);
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
		 * Primul "DFS" din algoritmul lui Kosaraju.
		 */
		private void dfs1(int node, boolean[] visited, ArrayList<Integer> finalSort) {
			visited[node] = true;
			
			for (int v : adj[node]) {
				if (!visited[v]) {
					dfs1(v, visited, finalSort);
				}
			}

			finalSort.add(node);
		}

		/*
		 * Al doilea "DFS" din algoritmul lui Kosaraju.
		 */
		private void dfs2(int node, boolean[] visited, ArrayList<Integer> currentCtc) {
			visited[node] = true;
			currentCtc.add(node);

			for (int v : adjTrans[node]) {
				if (!visited[v]) {
					dfs2(v, visited, currentCtc);
				}
			}
		}

		/*
		 * Functie pentru determinarea componentelor tari conexe folosing algoritmul lui Kosaraju.
		 */
		private ArrayList<ArrayList<Integer>> kosarajuScc() {
			ArrayList<ArrayList<Integer>> sccs = new ArrayList<>();
			boolean[] visited = new boolean[n + 1];
			ArrayList<Integer> finalSort = new ArrayList<>();

			for (int i = 1; i <= n; i++) {
				if (!visited[i]) {
					dfs1(i, visited, finalSort);
				}
			}

			Collections.reverse(finalSort);
			visited = new boolean[n + 1];

			for (int u : finalSort) {
				if (!visited[u]) {
					ArrayList<Integer> scc = new ArrayList<>();
					dfs2(u, visited, scc);
					sccs.add(scc);
				}
			}

			return sccs;
		}

		/*
		 * Functie auxiliara ce verfica daca nu exista muchie de la orice nod din componenta conexa
		 * catre un nod exterior componentei.
		 */
		private boolean isNotConnected(ArrayList<Integer> scc, ArrayList<Integer>[] graph) {
			// Ma uit in lista de adiacenta a fiecarui nod al componentei tari conexe primite ca
			// parametru, iar in cazul in care gasesc nu vecin pentru nodul curent care nu se afla
			// in componenta, intorc "false".
			for (int node : scc) {
				for (int comp : graph[node]) {
					if (!scc.contains(comp)) {
						return false;
					}
				}
			}

			// Altfel intorc "true".
			return true;
		}

		/*
		 * Functie ce rezolva problema prin determinarea numarului de componente tari conexe izolate
		 * din graful transpus al problemei.
		 */
		private int getResult() {
			
			// Componentele tari conexe ale grafului determinate cu Kosaraju
			ArrayList<ArrayList<Integer>> sccs = kosarajuScc();

			// Variabila in care retin numarul de "cai ferate" ce trebuiesc adaugate(rezultatul).
			int count = 0;

			// Parcurg fiecare componenta tare conexa a grafului.
			for (ArrayList<Integer> scc : sccs) {

				// Daca aceasta are dimensiune 1 si nu mai are alti vecini, inseamna ca este izolata
				// si incrementez contorul.
				if (scc.size() == 1 && adjTrans[scc.get(0)].size() == 0) {
					count++;

					// In cazul in care componenta tare conexa curenta contine doar nodul sursa,
					// nu o numar si pe ea deoarece nu are rost sa trasez o muchie spre ea insasi.
					if (scc.get(0) == s) {
						count--;
					}

				// Daca aceasta are dimensiunea mai mare ca 1, verific daca e izolata,
				// caz in care incrementez rezultatul.
				} else if (scc.size() > 1) {
					if (isNotConnected(scc, adjTrans)) {
						count++;
					}

					// In cazul in care componenta tare conexa curenta contine nodul sursa
					// si este si izolata, nu are rost sa o numar si pe ea, deoarece de la
					// sursa exista muchie catre oricare alt nod din componenta.
					if (scc.contains(s) && isNotConnected(scc, adjTrans)) {
						count--;
					}
				}
			}

			// La final, intorc rezultatul.
			return count;
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}

	/*
	 * Clasa preluata din prima tema si folosita pentru o citire mai eficienta.
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
