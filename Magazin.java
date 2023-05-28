import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Magazin {
	static class Task {
		public static final String INPUT_FILE = "magazin.in";
		public static final String OUTPUT_FILE = "magazin.out";

		// n = numar de noduri, q = numarul de intrebari.
		static int n, q;

		// Matrice in care stochez intrebarile.
		int[][] questions;

		// Sir in care sunt stocate depozitele din care pleaca celelalte depozite.
		int[] deposits;

		// d = sirul in care sunt retinuti timpii de inceput ai prelucrarii fiecarui nod.
		// f = sirul in care sunt retinuti timpii de finalizare ai prelucrarii fiecarui nod.
		// color = sirul in care sunt retinute stadiile de prelucrare pentru fiecare nod la
		// un anumit moment (1 - inceputul prelucrarii, 0 - prelucrarea, -1 - sfarsitul
		// prelucrarii).
		static int[] d, f, color;

		// variabila in care e retinut de fiecare data timpul la care s-a inceput
		// prelucrarea unui nod, respectiv timpul in care aceasta a fost finalizata.
		static int time = 0;

		// Sir in care retin nodurile din "DFS".
		int[] stack;

		// Dimensiunea sirului de mai sus.
		static int size = 0;

		public void solve() {
			readInput();
			writeOutput(getResult());
		}

		private void readInput() {
			try {
				MyScanner sc = new MyScanner(new BufferedReader(new FileReader(INPUT_FILE)));
				n = sc.nextInt();
				q = sc.nextInt();

				stack = new int[n + 1];
				deposits = new int[n];

				for (int i = 0; i < n - 1; i++) {
					deposits[i] = sc.nextInt();
				}

				// Am mai adaugat un element ca altfel nu-mi mergea.
				deposits[n - 1] = 0;

				d = new int[n + 1];
				f = new int[n + 1];
				color = new int[n + 2];

				questions = new int[q][2];

				for (int i = 0; i < q; i++) {
					int x = sc.nextInt();
					int y = sc.nextInt();
					questions[i][0] = x;
					questions[i][1] = y;
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void writeOutput(int[] sol) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));

				for (int i = 0; i < sol.length; i++) {
					bw.write(sol[i] + "\n");
				}

				bw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/*
		 * Functie ce creeaza graful descris de problema cu ajutorul sirului
		 * "deposits". Un element aflat pe pozitia "i" in "deposits" simbolizeaza
		 * faptul ca din nodul de pe pozitia "i" pleaca nodul "i + 2".
		 */
		private static ArrayList<Integer>[] createGraph(int[] deposits) {
			@SuppressWarnings("unchecked")
			ArrayList<Integer>[] adj = new ArrayList[deposits.length + 1];

			for (int i = 0; i <= deposits.length; i++) {
				adj[i] = new ArrayList<Integer>();
			}

			for (int i = 0; i < deposits.length; i++) {
				int node = deposits[i];
				adj[node].add(i + 2);
			}

			return adj;
		}

		/*
		 * Functie ce realizeaza un "DFS" cu ajutorul timpilor de inceput si
		 * finalizare pentru fiecare nod.
		 */
		private static void dfs(ArrayList<Integer>[] adj, int[] stack) {

			// Initial toate nodurile sunt albe(nu au fost prelucrate deloc).
			for (int i = 1; i <= n; i++) {
				color[i] = 1;
			}

			// Timpul initial este 0, deoarece nu a inceput prelucrarea.
			time = 0;

			// Exploram fiecare nod din graf pentru a fi prelucrate si
			// astfel determinam "DFS"-ul grafului nostru.
			for (int i = 1; i <= n; i++) {
				if (color[i] == 1) {
					explore(i, adj, stack);
				}
			}
		}

		/*
		 * Functie auxiliara pentru prelucrarea fiecarui nod in parte.
		 */
		private static void explore(int u, ArrayList<Integer>[] adj, int[] stack) {

			// Timpul de start al prelucrarii nodului.
			d[u] = ++time;

			// Nodul a inceput sa fie prelucrat.
			color[u] = 0;

			// Adaugam nodul in rezultat si incrementam dimensiunea sirului.
			stack[size++] = u;

			// Pentru fiecare vecin al nodului aflat in prelucrare, daca prelucrarea
			// lui nu a inceput, apelam recursiv functia pentru acesta.
			for (int node : adj[u]) {
				if (color[node] == 1) {
					explore(node, adj, stack);
				}
			}

			// La final nodul a fost prelucrat si determinam timpul la care
			// finalizarea a luat sfarsit.
			color[u] = -1;
			f[u] = ++time;
		}

		/*
		 * Functie ce rezolva problema prin incercarea de a raspunde la fiecare
		 * intrebare din "questions".
		 */
		private int[] getResult() {

			// Sir in care retin rezultatul
			int[] sol = new int[q];

			// Sir in care retin pe ce pozitie se afla fiecare nod in "DFS".
			int[] map = new int[n + 1];

			// Sir in care retin unde se afla fiecare nod din intrebare in
			// "DFS".
			int[] indexes = new int[q];

			// Graful cu care lucram.
			ArrayList<Integer>[] adj = createGraph(deposits);

			// Parcurgem graful.
			dfs(adj, stack);

			// Retinem unde se afla fiecare nod in "DFS"/
			for (int i = 0; i < stack.length; i++) {
				int node = stack[i];
				map[node] = i;
			}

			// Retinem unde se afla fiecare nod din intrebari
			// in "DFS".
			for (int i = 0; i < q; i++) {
				int node = questions[i][0];
				indexes[i] = map[node];
			}

			// Pentru fiecare intrebare
			for (int i = 0; i < q; i++) {
				
				// iau nodul de start
				int start = questions[i][0];

				// iau numarul de pasi pe care vreau sa-i fac
				// in "DFS", pornind de la nodul anterior.
				int steps = questions[i][1];

				// iau index-ul depozitului aflat la
				// "steps" pasi fata de depozitul de
				// la care plec
				int foundDeposit = indexes[i] + steps;
				
				// daca se intampla ca index-ul anterior sa
				// nu depaseasca dimensiunea parcurgerii
				if (foundDeposit <= n - 1) {
					
					// inseamna ca in parcurgere la index-ul
					// determinat anterior se afla un nod.
					// Altfel in solutie am fi adaugat -1.
					int node = stack[foundDeposit];

					// Pentru acel nod verific daca exista cale de
					// la nodul de la care s-a inceput cautarea, pana
					// la el, caz in care in solutie adaug acest nod.
					// In caz contrar, nu exista cale si in solutie adaug -1.
					if (f[start] > f[node] && d[start] < d[node]) {
						sol[i] = node;
					} else {
						sol[i] = -1;
					}
				} else {
					sol[i] = -1;
				}
			}
		
			// Intorc rezultatul astfel determinat.
			return sol;
		}
	}

	public static void main(String[] args) {
		new Task().solve();
	}

	/*
	 * Clasa folosita pentru citire mai eficienta, extrasa din scheletul primei teme.
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
