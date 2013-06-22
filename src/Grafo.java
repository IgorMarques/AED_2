import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

class Aresta implements Comparable<Aresta> {
	public int origem;
	public int destino;
	public int peso;
	public Aresta(int origem, int destino, int peso) {
		this.origem = origem;
		this.destino = destino;
		this.peso = peso;
	}
	public int compareTo(Aresta a) {
		return this.peso - a.peso; 
	}
}

class ListaAresta {
	public LinkedList<Aresta> arestas;
	public ListaAresta() {
		arestas = new LinkedList<Aresta>(); 
	}
	public Aresta getAresta(int i) {
		return arestas.get(i);
	}
	public int getTamanho() {
		return arestas.size();
	}
}

public class Grafo {

	public static int MATRIZ = 0;
	public static int LISTA = 1;
	
	public int matriz[][];
	private ListaAresta listaAdjacencia[];
	private boolean orientado;
	
	public boolean isOrientado() {
		return orientado;
	}

	public boolean vis[];

	private int tipoRepresentacao; //MATRIZ ou LISTA
	private int n; //número de vértices
	private int e; //número de arestas

	int getNumeroVertices() {
		return n;
	}
	int getNumeroArestas() {
		return e;
	}
	
	public int getPeso(int origem, int destino) {
		if(tipoRepresentacao == MATRIZ)
			return matriz[origem][destino];
		else {
			ListaAresta lista = getLista(origem);
			for(int i = 0; i < lista.getTamanho(); i++) {
				Aresta a = lista.getAresta(i);
				if(a.destino == destino)
					return a.peso;
			}
			return 0;
		}
	}
	
	public ListaAresta getLista(int vertice) {
		return listaAdjacencia[vertice];
	}
	
	//retorna um vetor de arestas contendo todas as arestas do grafo
	public Aresta[] getVetorArestas() {
		Aresta a[] = new Aresta[getNumeroArestas()];
		int idx = 0;
		for(int i = 0; i < getNumeroVertices(); i++) {
			for(int j = 0; j < getLista(i).getTamanho(); j++) {
				if(!orientado) {
					if(getLista(i).getAresta(j).origem < getLista(i).getAresta(j).destino)
						a[idx++] = getLista(i).getAresta(j);
				} else
					a[idx++] = getLista(i).getAresta(j);
					//System.out.println(a[idx-1].origem);
			}
		}
		return a;
	}
	
	//adicionar aresta orientada
	public boolean adicionarAresta(int origem, int destino, int peso) {
		if(origem < 0 || destino < 0 || peso <= 0 || origem >= n || destino >= n) return false;
		if(tipoRepresentacao == MATRIZ) {
			matriz[origem][destino] = peso;
		} else {
			Aresta novaAresta = new Aresta(origem,destino,peso);
			listaAdjacencia[origem].arestas.add(novaAresta);
		}
		return true;
	}

	public boolean init() {
		if(n == -1) return false;
		vis = new boolean[n];
		if(tipoRepresentacao == MATRIZ) {
			matriz = new int[n][n];
		} else {
			listaAdjacencia = new ListaAresta[n];
			for(int i = 0; i < n; i++)
				listaAdjacencia[i] = new ListaAresta();
		}		
		return true;
	}
	
	public Grafo(int tipoRepresentacao) {
		this.tipoRepresentacao = tipoRepresentacao;
		n = -1;
	}

	public boolean lerGrafoDeArquivo(String nomeArquivo, boolean orientado, boolean ponderado) {
		Scanner input;
		this.orientado = orientado;
		try {
			input=new Scanner(new FileReader(nomeArquivo));
		} catch (FileNotFoundException e) {
			System.out.println("Erro ao abrir o arquivo");
			return false;
		}
		
		boolean entradaCorreta = true;;
		if(entradaCorreta &= input.hasNextInt()) {
			n = input.nextInt();
			if(!init())
				return false;
			if(entradaCorreta &= input.hasNextInt()) {
				e = input.nextInt();
				for(int i = 0; i < e; i++) {
					if(entradaCorreta &= input.hasNextInt()) {
						int origem = input.nextInt();
						if(entradaCorreta &= input.hasNextInt()) {
							int destino = input.nextInt();
							if(ponderado) {
								if(entradaCorreta &= input.hasNextInt()) {
									int peso = input.nextInt();
									adicionarAresta(origem, destino, peso);
									if(!orientado)
										adicionarAresta(destino, origem, peso);
								}
							} else {
								adicionarAresta(origem, destino, 1);
							}
						}
					}
				}
			}
		}
		if(!entradaCorreta) {
			System.out.println("Arquivo não consistente com a especificação de entrada");
			return false;
		}
		
		try {
			input.close();
		} catch (Exception e) {
			System.out.println("Não foi possível fechar arquivo");
			return false;
		}
		return true;
	}
	
	public boolean printMatriz() {
		if(tipoRepresentacao == LISTA) {
			System.out.println("Warning: impressão da matriz solicitada mas representação utilizada é lista de adjacência");
			return false;
		}
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++)
				System.out.print(matriz[i][j] + " ");
			System.out.println("");
		}
		return true;
	}

	public boolean printLista() {
		if(tipoRepresentacao == MATRIZ) {
			System.out.println("Warning: impressão das listas solicitada mas representação utilizada é matriz");
			return false;
		}
		for(int i = 0; i < n; i++) {
			System.out.print(i + " -> ");
			for(int j = 0; j < listaAdjacencia[i].arestas.size(); j++) {
				if(j != 0) System.out.print(", ");
				System.out.print(listaAdjacencia[i].arestas.get(j).destino);
				System.out.print("(" + listaAdjacencia[i].arestas.get(j).peso + ")");
			}
			System.out.println("");
		}
		return true;
	}
}
