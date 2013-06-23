import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

class SolveShortestPath {
	
	private static final int INFINITY = 99999;
	
	Grafo g;
	public SolveShortestPath(Grafo g) {
		this.g = g;
	}
	
	//implemente getShortestPath com uma das seguintes operacoess
	//1) Floyd-warshall O(n^3) (2.5 pts)
	//2) Dijkstra com fila de prioridade (2.5 pts) ou com busca linear (1.8 pts)
	//3) Bellman-ford (2.5 pts)
	
    //mestodos de uma ArrayList: boolean isEmpty(), void add(int), void remove()
	ArrayList<Integer> getShortestPath(int i, int j) {
		
		//Algoritmo de Bellman-Ford, assumindo que naoo ha arestas de peso negativo
		ArrayList<Integer> path = new ArrayList<Integer>();
		int[] custos = new int[g.getNumeroVertices()];
		int[] preds = new int[g.getNumeroVertices()];

		for (int k = 0; k < g.getNumeroVertices(); k++) {
			custos[k] = INFINITY;
			preds[k] = -1;
		}
		
		custos[i] = 0;
		
		for (int k = 0; k < g.getNumeroVertices(); k++) {
			for (Aresta aresta : g.getVetorArestas()) {
				if (custos[aresta.origem] + aresta.peso < custos[aresta.destino]) {
					custos[aresta.destino] = custos[aresta.origem] + aresta.peso;
					preds[aresta.destino] = aresta.origem;
				}
				else if (!g.isOrientado() && custos[aresta.destino] + aresta.peso < custos[aresta.origem]) {
					custos[aresta.origem] = custos[aresta.destino] + aresta.peso;
					preds[aresta.origem] = aresta.destino;
				}
			}
		}
		
		if (custos[j] == INFINITY)
			return path;
		
		int tamanhoPilha = 0;
		int[] pilha = new int[g.getNumeroVertices()];
		int pred = j;
		
		while (pred != -1) {
			pilha[tamanhoPilha++] = pred;
			pred = preds[pred];
		}
		
		tamanhoPilha--;
		
		for (; tamanhoPilha >= 0; tamanhoPilha--) {
			path.add(pilha[tamanhoPilha]);
		}
		
		path.add(custos[j]);
		
		return path;
	}
}

class DisjointSet{
	private DisjointSet representative;
	public int  value;
	private int order;
	
	public DisjointSet(int i){
		this.value = i;
		this.representative = this;
		this.order=0;
	}
	
	public DisjointSet findset(){
		if (this.representative== this){
			return this;
		}
		else return this.representative.findset();
	}

	public int getOrder(){
		return this.findset().order;
	}
	
	public void setOrder(int o){
		this.findset().order= o;
	}
	
	public void setRepresentative(DisjointSet d){
		this.findset().representative= d;
	}
	
	public void union(DisjointSet other){
		
		if (this.getOrder() == other.getOrder()){
			other.setRepresentative(this.findset());
			this.setOrder(this.order++);
		}
		else if (this.getOrder() < other.getOrder()){
			this.setRepresentative(other.findset());
		}
		else{
			other.setRepresentative(this.findset());
		}
			
	}
}

class Kruskal{
	Grafo g;
	
	public Kruskal(Grafo g) {
		this.g = g;
	}
	
	public ArrayList<Aresta> solveMST(){
		
		//lista de arestas da mst
		ArrayList<Aresta> mst_arestas = new ArrayList<>();
		
		//grafo da mst
		Grafo tree = new Grafo(Grafo.LISTA);
		
		tree.setNumeroVertices(g.getNumeroVertices());
		
		tree.init();
		
		//fila de prioridade de arestas
		PriorityQueue<Aresta> arestas = new PriorityQueue<Aresta>();
		
		//conjuntos de conjuntos disjuntos dos nós
		DisjointSet[] nos = new DisjointSet[g.getNumeroVertices()];
		
		
		//ordenando as arestas
		for (Aresta aresta : g.getVetorArestas()) {
			arestas.offer(aresta);
		}
		
		//criando os conjuntos para cada nó
		for (int i=0; i <g.getNumeroVertices(); i++) {							
			nos[i]= new DisjointSet(i);
		}
		
		
		//executando o algoritmo propriamente dito
		for (int i = 0; i < g.getNumeroVertices()-1; i++) {
			
			//menor aresta
			Aresta a= arestas.poll();
			
			DisjointSet origem = nos[a.origem];
			DisjointSet destino = nos[a.destino];
		
			//se ela conecta duas árvores distintas
			if(origem.findset() != destino.findset()){	
				
				//adiciona na lista de arestas e no grafo
				mst_arestas.add(a);
			
				tree.adicionarAresta(a.origem, a.destino, a.peso);
				
				//caso especial do mesmo nao ser orientado
				if (!g.isOrientado()){
					tree.adicionarAresta(a.destino, a.origem, a.peso);
					Aresta b = new Aresta(a.destino, a.origem, a.peso);
					mst_arestas.add(b);
				}
				
				origem.union(destino);
			}
		}		
		
		//imprime a árvore
		tree.printLista();
		
		//retorna a lista de arestas
		return mst_arestas;
	}
}

class Prim{
	Grafo g;
	
	public Prim (Grafo g){
		this.g = g;
	}
	
	public ArrayList<Aresta> solveMST(){
		
		//lista de arestas da mst
		ArrayList<Aresta> mst_arestas = new ArrayList<>();
		
		//grafo que representa a mst
		Grafo tree = new Grafo(Grafo.LISTA);
		
		//inicializando
		tree.setNumeroVertices(g.getNumeroVertices());
		
		tree.init();
		
		//fila de prioridade de arestas
		PriorityQueue<Aresta> filaArestas = new PriorityQueue<Aresta>();
		
		//fila de nós
		LinkedList<Integer> nos = new LinkedList<Integer>();
		
		//nó inicial
		nos.add(0);		
		
		//lista de visitados
		boolean vis[] = new boolean[g.getNumeroVertices()];
		
		//enquanto houverem nós a serem adicionados
		while (!nos.isEmpty() && tree.getNumeroVertices() < g.getNumeroVertices()) {
			
			//nó atual (primeiro da fila)
			int currentNode = nos.poll();
			
			//marca o mesmo como visitado
			vis[currentNode]=true;
			
			System.out.println(currentNode);
			
			//pega a lista de arestas do nó atual
			ListaAresta currentArestas = g.getLista(currentNode);
			
			//adiciona na fila de prioridades
			for (int i = 0; i < currentArestas.getTamanho(); i++) {
				filaArestas.offer(currentArestas.getAresta(i));
			}
			
			//pega a menor
			Aresta a = filaArestas.poll();
			
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			while(vis[a.destino]== true && !filaArestas.isEmpty()){
				a = filaArestas.poll();
			}
			
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			//DAISIAJISAPROBLEMA TA AQUI
			
			//adiciona destino a fila
			nos.add(a.destino);	
			System.out.println("Add");
			System.out.println(a.destino);
			
			//adiciona aresta a árvore
			tree.adicionarAresta(a.origem, a.destino, a.peso);
			
			mst_arestas.add(a);
			
			
			
			if (!g.isOrientado()){
				tree.adicionarAresta(a.destino, a.origem, a.peso);
				Aresta b = new Aresta(a.destino, a.origem, a.peso);
				mst_arestas.add(b);
				
			}
			
			System.out.println("hue");
			
						
		}
		
		tree.printLista();
		System.out.println("terminou prim");
		return mst_arestas;
	
	}
	
	
}

public class ShortestPath {

	public static void main(String[] args) {
		Grafo g = new Grafo(Grafo.LISTA);
		g.lerGrafoDeArquivo("grafo1.in", false, true);
		g.printLista();
		
		SolveShortestPath ssp = new SolveShortestPath(g);
		
		//SHORTEST PATH
//		ArrayList<Integer> path = ssp.getShortestPath(0, 1);
//		if(path.size() == 0)
//			System.out.println("No path");
//		else {
//			for (int v: path) {
//				System.out.printf("%d ", v);
//			}
//			
//			System.out.println();
//		}
		
		//MST
		
		Kruskal kruskal = new Kruskal(g);
		//ArrayList<Aresta> tree = kruskal.solveMST();
		
		
		Prim prim = new Prim(g);
		ArrayList<Aresta> tree = prim.solveMST();
		
		for(Aresta a: tree){
			System.out.println(a.origem);
			System.out.println(a.destino);
			System.out.println(a.peso);
			System.out.println("--");
		}
		System.out.println("cabou");

	}
}

