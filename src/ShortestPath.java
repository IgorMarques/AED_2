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
	
	public Grafo solveMST(){
		Grafo tree = new Grafo(Grafo.LISTA);
		
		//System.out.println("criando filas e vetores");
		PriorityQueue<Aresta> arestas = new PriorityQueue<Aresta>();
		DisjointSet[] nos = new DisjointSet[g.getNumeroVertices()];
		
		//System.out.println("populando fila");
		for (Aresta aresta : g.getVetorArestas()) {
			arestas.offer(aresta);
		}
		
		//System.out.println("populando vetor");
		for (int i=0; i <g.getNumeroVertices(); i++) {							
			nos[i]= new DisjointSet(i);
		}
		
		//System.out.println("iterando");
		for (int i = 0; i < g.getNumeroVertices()-1; i++) {
			Aresta a= arestas.poll();
			
			System.out.println("Aresta:");
			
			DisjointSet origem = nos[a.origem];
			DisjointSet destino = nos[a.destino];
			
			System.out.println(a.origem);
			System.out.println(a.destino);
			
			if(origem.findset() != destino.findset()){
				System.out.println("adicionando aresta");
			
				tree.adicionarAresta(a.origem, a.destino, a.peso);
				
				System.out.println("unindo conjuntos");
				
				origem.union(destino);
			}
		}		
		
		System.out.println("retornando mst");
		
		System.out.println(tree.getNumeroVertices());
		System.out.println(tree.getNumeroArestas());
		System.out.println(g.getNumeroVertices());
		return tree;
	}
}

class Prim{
	Grafo g;
	
	public Prim (Grafo g){
		this.g = g;
	}
	
	public Grafo solveMST(){
		Grafo tree = new Grafo(Grafo.LISTA);
		
		PriorityQueue<Aresta> arestas = new PriorityQueue<Aresta>();
		
		Queue nos = new LinkedList<Integer>();
		
		while (tree.getNumeroArestas() < g.getNumeroArestas() ){
			
		}
		
		
		return tree;
	
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
		Grafo tree = kruskal.solveMST();
		
		tree.printLista();
		System.out.println("cabou");

	}
}

