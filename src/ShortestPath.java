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
		while (!nos.isEmpty()) {
			
			//nó atual
			int currentNode = nos.poll();
			
			//marca como visitado
			vis[currentNode]=true;
			
			//pega  as arestas
			ListaAresta currentArestas= g.getLista(currentNode);
			
			//joga numa fila de prioridade
			for (int i = 0; i < currentArestas.getTamanho(); i++) {
				Aresta a = currentArestas.getAresta(i);
				
				filaArestas.offer(a);
			}
			
			//pega a menor
			Aresta a = filaArestas.poll();
			
			//enquanto ela levar a um destino ja visitado e a fila nao estiver vazia
			while (vis[a.destino] && !filaArestas.isEmpty()) {
				//pega a seguinte
				a = filaArestas.poll();
			}
			
			//caso a final nao leve pra um destino visitado
			if(!vis[a.destino]){
				
				//adiciona na árvore
				tree.adicionarAresta(a.origem, a.destino, a.peso);
				mst_arestas.add(a);
				
				//para os casos de grafo nao orientado
				if (!g.isOrientado()){
					tree.adicionarAresta(a.destino, a.origem, a.peso);
					Aresta b = new Aresta(a.destino, a.origem, a.peso);
					mst_arestas.add(b);
				}
			
				//adiciona o destino a fila
				nos.offer(a.destino);
			}
			
						
		}
		
		//imprime a árvore
		tree.printLista();
		
		//retorna as arestas
		return mst_arestas;
	
	}
	
}

class Node implements Comparable<Node>{
	
	public int peso;
	public int valor;

	public Node(int v, int p){
		this.valor=v;
		this.peso= p;
	}
	
	@Override
	public int compareTo(Node o) {
		return this.peso - o.peso;
	}
	
}

//fiz um heap pois o do java nao possui decrease key
class Heap{
	
	public Node[] nodes= new Node[1000];
	public int cont=1;
	
	public void Heap(){
	}
		
	private void swap(int i, int j){
		Node aux = nodes[i];		
		nodes[i]=nodes[j];
		nodes[j]=aux;
	}
	
	private void climb(int index){
		while(index/2 > 0){
			
			if (nodes[index/2].peso > nodes[index].peso){
				swap(index/2, index);
			}
		}
	}
	
	public void offer(Node n){

		nodes[cont]= n;
		
		cont++;
		

	}
	
	private int getSmallestIndex(int index){
		
		if(nodes[2*index+1].peso < nodes[index*2].peso)
			return 2*index+1;
		return 2*index;
		
	}
	
	public void decreaseKey(int key, int newPeso){
		
		
		for (int i = 1; i < cont; i++) {
			
			if(key == nodes[i].valor){
				nodes[i].peso= newPeso;
				climb(i);
				//System.out.println(i);
				//System.out.println(nodes[i].valor);
				return;
			}
		}
		
	}
	
	public boolean has_left(int index){
		if (2*index <= cont)
			return true;
		return false;
	}
	
	public boolean has_right(int index){
		if (2*index+1 <= cont)
			return true;
		return false;
	}
	
	public Node poll(){
		
		Node root = nodes[1];				
		
		nodes[1]= nodes[cont-1];	
		
		cont--;
		
		//System.out.println(nodes[1].valor);
		
		int index = 1;
		
		if (has_left(index)&& has_right(index)){
		
			
			while( nodes[index].peso > nodes[index*2].peso || nodes[index].peso > nodes[index*2+1].peso)
			{			
				int smallestIndex = getSmallestIndex(index);
				
				
				swap(index, smallestIndex);
				
				
				index= smallestIndex;
				
				//System.out.println(index);
			}
		
		}
		else if (has_left(index)){
			
			swap(index, 2*index);
		}
		
		return root;
		
	}
	
	public boolean isEmpty(){
		if (cont==1)
			return true;
		return false;
	}
	
	public void print(){
		System.out.println(cont);
		for (int i = 1; i < cont; i++) {
			System.out.println(nodes[i].valor+" "+nodes[i].peso);
		}
	}
	
	
}


class Dijkstra{
	
	Grafo g;
	
	public static final int MAX = 99999;
	
	public Dijkstra(Grafo g) {
		this.g = g;
	}
	
	ArrayList<Integer> getShortestPath(int i, int j) {
		
		int destino = j;
		int origem = i;
	
		ArrayList<Integer> path = new ArrayList<Integer>();
		
		PriorityQueue<Aresta> arestas = new PriorityQueue<>();
		
		boolean vis[] = new boolean[g.getNumeroVertices()];
		
		int pred[]= new int[g.getNumeroVertices()];
		
		int dis[]=new int[g.getNumeroVertices()];
		
		Heap nos = new Heap();
		
		for (int k = 0; k < g.getNumeroVertices(); k++) {					

			Node no = new Node(k, MAX);
			
			if(k==i)
				no.peso=0;
			
			nos.offer(no);

			vis[k]= false;
			pred[k]=-1;
			dis[k]=MAX;
		}		
		
		while(!nos.isEmpty()){	
			
			nos.print();
		
			Node currentNode= nos.poll();				
			
			System.out.println("Current Node " + currentNode.valor);				
			
			ListaAresta currentArestas = g.getLista(currentNode.valor);
			
			for (int k = 0; k < currentArestas.getTamanho(); k++) {
				
				Aresta a = currentArestas.getAresta(k);	
				
				if (!vis[a.destino] && (a.peso + currentNode.peso < dis[a.destino])) {
					
					int newPeso = a.peso + currentNode.peso;
					
					nos.decreaseKey(a.destino, newPeso);
					
					dis[a.destino]= newPeso;
					
					pred[a.destino]= currentNode.valor;	
					
				}
				
			}
			
			vis[currentNode.valor]=true;
		}
		

		int cnode = destino;
		
		for (int k = 0; k < g.getNumeroVertices(); k++) {
			System.out.println(k + " " + pred[k]);
		}
		
		pred[origem]=-1;
		
		Stack<Integer> stack = new Stack<>();
		
		while (pred[cnode] !=-1) {
	
			stack.push(cnode);
			cnode=pred[cnode];		
		}
		
		while (!stack.isEmpty()) {
			path.add(stack.pop());
		}
		
		return path;
	}
	
}

class BellmanFord{
	
	public static final int MAX = 99999;
	
	public Grafo g;
	
	public BellmanFord(Grafo grafo){
		this.g=grafo;
	}
	
	ArrayList<Integer> getShortestPath(int origem, int destino) {
		ArrayList<Integer> path = new ArrayList<>();
		
		Aresta[] arestas = g.getVetorArestas();
		
		int[] dist = new int[g.getNumeroVertices()];
		int[] pred = new int[g.getNumeroVertices()];
	
		for (int k = 0; k < dist.length; k++) {
			dist[k]=MAX;
			pred[k]=-1;
		}
		
		dist[origem]= 0;
		
		
		
		for (int k = 0; k < g.getNumeroVertices(); k++) {
			
			System.out.println("\nIteracao num: "+ k);
			for(Aresta a: arestas){
	
				System.out.println("Origem:  "+ a.origem+  " : "+ dist[a.origem] + " : " + a.peso);
				System.out.println("Destino: "+ a.destino+  " : "+ dist[a.destino]+ " : " + a.peso);
				
				if(dist[a.origem]+a.peso < dist[a.destino]){
					System.out.println();
					
					dist[a.destino]=dist[a.origem]+a.peso;
					pred[a.destino]=a.origem;
				}	
				else if (!g.isOrientado() && dist[a.destino] + a.peso < dist[a.origem]) {
					dist[a.origem] = dist[a.destino] + a.peso;
					pred[a.origem] = a.destino;
				}
								
			}
		}
		
		int cnode = destino;
		
		for (int k = 0; k < g.getNumeroVertices(); k++) {
			System.out.println(k + " " + pred[k]);
		}
		
		Stack<Integer> stack = new Stack<>();
		
		while (pred[cnode] !=-1) {
	
			stack.push(cnode);
			cnode=pred[cnode];		
		}
		
		while (!stack.isEmpty()) {
			path.add(stack.pop());
		}		
		
		return path;
	}
	
	
}

public class ShortestPath {

	public static void main(String[] args) {
		Grafo g = new Grafo(Grafo.LISTA);
		g.lerGrafoDeArquivo("grafo1prova.in", false, true);
		g.printLista();
		
		//SolveShortestPath ssp = new SolveShortestPath(g);
		//Dijkstra ssp= new Dijkstra(g);
		BellmanFord ssp = new BellmanFord(g);
		
		//SHORTEST PATH
		
		ArrayList<Integer> path = ssp.getShortestPath(0, 1);
		
		if(path.size() == 0)
			System.out.println("No path");
		else {
			int custo=0;
			int oldNode=0;
			
			for (int p: path) {
				System.out.printf("%d ", p);
				
				custo+= g.getPeso(oldNode, p);
				
				oldNode=p;
			}
			
			System.out.println(custo);
			
			System.out.println();
		}
		
		//MST
		
//		Kruskal kruskal = new Kruskal(g);
//		//ArrayList<Aresta> tree = kruskal.solveMST();
//		
//		
//		Prim prim = new Prim(g);
//		ArrayList<Aresta> tree = prim.solveMST();
//		
//		for(Aresta a: tree){
//			System.out.println(a.origem);
//			System.out.println(a.destino);
//			System.out.println(a.peso);
//			System.out.println("--");
//		}
//		System.out.println("cabou");

	}
}

