// Header precompilato per VisualStudio
#include "stdafx.h"

#include <iostream>
#include <random>
#include "heuristic.h"

using namespace std;

Heuristic::Heuristic(string path) {
	this->hasSolution = false;
	string line;
	string word;

	ifstream iffN(path.c_str());

	if (!iffN.is_open()) {
		cout << "Impossible to open" << path << endl;
		cin.get();
		exit(1);
	}

	getline(iffN, line);
	std::replace(line.begin(), line.end(), ';', ' ');
	istringstream iss(line);
	iss >> word;
	this->nCells = atoi(word.c_str());
	iss >> word;
	this->nTimeSteps = atoi(word.c_str());
	iss >> word;
	this->nCustomerTypes = atoi(word.c_str());

	// Memory allocation
	solution = new int***[nCells];
	problem.costs = new double***[nCells];
	for (int i = 0; i < this->nCells; i++) {
		problem.costs[i] = new double**[nCells];
		solution[i] = new int**[nCells];
		for (int j = 0; j < this->nCells; j++) {
			problem.costs[i][j] = new double*[nCustomerTypes];
			solution[i][j] = new int*[nCustomerTypes];
			for (int m = 0; m < this->nCustomerTypes; m++) {
				problem.costs[i][j][m] = new double[nTimeSteps];
				solution[i][j][m] = new int[nTimeSteps];
			}
		}
	}
	problem.n = new int[nCustomerTypes];
	problem.activities = new int[nCells];
	problem.usersCell = new int**[nCells];
	for (int i = 0; i < this->nCells; i++) {
		problem.usersCell[i] = new int*[nCustomerTypes];
		for (int m = 0; m < this->nCustomerTypes; m++) {
			problem.usersCell[i][m] = new int[nTimeSteps];
		}
	}
	/*
	* finito di istanziare
	*/

	getline(iffN, line);
	getline(iffN, line);
	std::replace(line.begin(), line.end(), ';', ' ');
	istringstream issN(line);
	for (int m = 0; m < nCustomerTypes; m++) {
		issN >> word;
		problem.n[m] = atoi(word.c_str());
	}

	getline(iffN, line);
	for (int m = 0; m < nCustomerTypes; m++) {
		for (int t = 0; t < nTimeSteps; t++) {
			getline(iffN, line);// linea con m e t
			for (int i = 0; i < nCells; i++) {
				getline(iffN, line);// linea della matrice c_{ij} per t ed m fissati
				istringstream issC(line);
				for (int j = 0; j < nCells; j++) {
					issC >> word;
					problem.costs[i][j][m][t] = atoi(word.c_str());
				}
			}
		}
	}

	getline(iffN, line);
	getline(iffN, line);
	std::replace(line.begin(), line.end(), ';', ' ');
	istringstream issA(line);
	for (int i = 0; i < nCells; i++) {
		issA >> word;
		problem.activities[i] = atoi(word.c_str());
	}

	getline(iffN, line);
	for (int m = 0; m < nCustomerTypes; m++) {
		for (int t = 0; t < nTimeSteps; t++) {
			getline(iffN, line);
			getline(iffN, line);
			std::replace(line.begin(), line.end(), ';', ' ');
			istringstream issU(line);
			for (int i = 0; i < nCells; i++) {
				issU >> word;
				problem.usersCell[i][m][t] = atoi(word.c_str());
			}
		}
	}
}

/*
Rispetto all'algoritmo base si privilegiano gli utenti che offrono il minore rapporto costo / task.
La soluzione può essere utilizzata come soluzione iniziale fattibile per una meta-euristica.
Attualmente le soluzioni fornite si discostano di pochissimo rispetto a quelle ottime,
ma alcune sono leggermente superiori altre leggermente inferiori, quindi ci sono soluzioni super-ottime.

EB: valutare se conviene cambiare l'ordine di iterazione degli array. probabilmente meglio ciclare dall'esterno all'interno in ordine di lunghezza. il numero di iterazioni totali è uguale (n*m*i*j) ma se mettiamo dei blocchi intelligenti guadagnamo iterazioni risparmiate
*/
void Heuristic::solveFast(vector<double>& stat, int timeLimit) {
	double objFun = 0;
	clock_t tStart = clock();

	for (int i = 0; i < nCells; i++)
		for (int j = 0; j < nCells; j++)
			for (int m = 0; m < nCustomerTypes; m++)
				for (int t = 0; t < nTimeSteps; t++)
					solution[i][j][m][t] = 0;

	// Itera su ogni cella di destinazione / operazionale.
	for (int i = 0; i < nCells; i++) {
		int iOriginCell = -1;
		int iCustomerType = -1;
		int iTimeStep = -1;

		// Rimane nel ciclo finché tutti i task da eseguire nella cella corrente non sono stati assegnati.
		bool notSatisfied = true;
		while (notSatisfied) {

			// Determina la posizione, il tipo e l'intervallo temporale degli utenti che possono offrire il minore rapporto costo / task.
			double minimumRatio = numeric_limits<double>::max();

			// Itera su ogni cella di origine
			for (int j = 0; j < nCells; j++) {

				// Sposto qui il vincolo (i != j) perché mi sembra inutile verificarlo nel ciclo più interno.
				if (i == j) { continue; }

				// Itera su ogni tipo di utente.
				for (int m = 0; m < nCustomerTypes; m++) {

					// Gli utenti devono poter eseguire almeno un'operazione. Il controllo non deve essere così ripetuto in seguito.
					if (problem.n[m] <= 0) { continue; }

					// Itera su ogni istante temporale.
					for (int t = 0; t < nTimeSteps; t++) {

						// Deve essere presente almeno un utente. Il controllo non deve essere così ripetuto in seguito.
						if (problem.usersCell[j][m][t] <= 0) { continue; }

						double currentRatio = problem.costs[j][i][m][t] / problem.n[m];
						if (currentRatio < minimumRatio) {
							if (t != iTimeStep || iCustomerType != m || iOriginCell != j)
							{
								minimumRatio = currentRatio;
								iOriginCell = j;
								iCustomerType = m;
								iTimeStep = t;
							}
						}
					}
				}
			}

			if ((iOriginCell < 0) || (iCustomerType < 0) || (iTimeStep < 0)) {
				hasSolution = false;
				return;
			}

			// Sposta il numero necessario o possibile di utenti dalla cella di origine a quella di destinazione / operazionale.
			// Aggiorna la funzione obiettivo.
			int availableActivities = problem.usersCell[iOriginCell][iCustomerType][iTimeStep] * problem.n[iCustomerType];
			if (problem.activities[i] > availableActivities) {
				solution[iOriginCell][i][iCustomerType][iTimeStep] += problem.usersCell[iOriginCell][iCustomerType][iTimeStep];
			}
			else {
				solution[iOriginCell][i][iCustomerType][iTimeStep] += ceil((double)problem.activities[i] / problem.n[iCustomerType]);
				notSatisfied = false;
				if (!notSatisfied && problem.activities[i] > solution[iOriginCell][i][iCustomerType][iTimeStep] * problem.n[iCustomerType])
					cout << "Porco dio soddisfatto\n";
			}
			problem.usersCell[iOriginCell][iCustomerType][iTimeStep] -= solution[iOriginCell][i][iCustomerType][iTimeStep];
			if (problem.usersCell[iOriginCell][iCustomerType][iTimeStep] < 0)
				cout << "Porco dio\n";
			problem.activities[i] -= solution[iOriginCell][i][iCustomerType][iTimeStep] * problem.n[iCustomerType];
			objFun += solution[iOriginCell][i][iCustomerType][iTimeStep] * problem.costs[iOriginCell][i][iCustomerType][iTimeStep];
		}
	}

	stat.push_back(((double)clock() - tStart) / CLOCKS_PER_SEC);
	stat.push_back(objFun);

	hasSolution = true;

	// Stampa il risultato
	cout << "Start time = " << tStart << "End time = " << clock() << "TimeDiff = " << (((double)clock() - tStart) / CLOCKS_PER_SEC) << "\t" << "Objective Function = " << objFun << "\n";

	return;
}

/*
Sono presenti tre tipi di cella (stando al testo dell'assignment): di origine, di destinazione, operazionale.
La differenza consiste nel ruolo che assumono di volta in volta nei diversi passaggi dell'algoritmo, non sono insiemi separati.
Non capisco che differenza ci sia tra una di destinazione ed un'operazionale.
L'algoritmo è centrato sulle celle di destinazione / operazionali, sulle quali si itera esternamente cercando di soddisfarne le richeste:
per ognuna si itera internamente su tutte le celle di origine prendendone gli utenti necessari e portandoli a destinazione affinché eseguano dei task.
*/
//void Heuristic::solveFast(vector<double>& stat, int timeLimit) {
//	double objFun = 0;
//	clock_t tStart = clock();
//
//	for (int i = 0; i < nCells; i++)
//		for (int j = 0; j < nCells; j++)
//			for (int m = 0; m < nCustomerTypes; m++)
//				for (int t = 0; t < nTimeSteps; t++)
//					solution[i][j][m][t] = 0;
//
//	// Per ogni cella di destinazione / operazionale.
//	for (int i = 0; i < nCells; i++) {
//
//		// Numero di task che devono essere eseguiti nella cella i.
//		int demand = problem.activities[i];
//
//		bool notSatisfied = true;
//
//		// Per ogni cella di origine. Esce se tutti i task sono stati assegnati.
//		for (int j = 0; j < nCells && notSatisfied; j++) {
//
//			// Per ogni tipo di utente. Esce se tutti i task sono stati assegnati
//			for (int m = 0; m < nCustomerTypes && notSatisfied; m++) {
//
//				// Per ogni istante temporale. Esce se tutti i task sono stati assegnati.
//				for (int t = 0; t < nTimeSteps && notSatisfied; t++) {
//
//					// Non è possibile assegnare dei task di una cella a degli utenti già nella cella.
//					// Non capisco perché è necessario che si spostino.
//					if (i != j) {
//
//						// Se il numero di task da eseguire rimasti nella cella di destinazione / operazionale i
//						// è maggiore di quelli che possono essere eseguiti dagli utenti della cella di origine j,
//						// sposta tutti gli utenti dalla cella di origine a quella di destinazione / operazionale.
//						if (demand > problem.n[m] * problem.usersCell[j][m][t]) {
//
//							// Non capisco l'ordine degli indici i e j, non mi sembrano coerenti con quelli dei cicli,
//							// ma probabilmente è indifferente.
//							solution[j][i][m][t] = problem.usersCell[j][m][t];
//
//							// Non capisco perché la casella di userCell non possa essere direttamente azzerata.
//							problem.usersCell[j][m][t] -= solution[j][i][m][t];
//						}
//
//						// Altrimenti sposta solo il numero di utenti necessario, ottenuto dividendo il numero di task da eseguire rimasti
//						// per il numero di task che ogni utente può eseguire, e prendendo la parte intera.
//						// Valido solo a condizione (che dovrebbe essere verificata dall'if) che si sappia a priori che il numero di utenti è sufficiente.
//						// Non capisco perché l'approssimazione è per difetto (floor()) e non per eccesso (ceil()):
//						// se ho 4 task rimasti e un certo tipo di utente ne può eseguire 5, non dovrei spostare 1 utente invece che 0?
//						// Non capisco il perché del += se non si ritorna due volte nella stessa casella.
//						else {
//							solution[j][i][m][t] += floor(demand / problem.n[m]);
//							notSatisfied = false;
//						}
//
//						// Il valore della funzione obiettivo viene incrementato del numero di utenti per il loro costo. Nessuna minimizzazione.
//						if (solution[j][i][m][t] != 0)
//							objFun += solution[j][i][m][t] * problem.costs[j][i][m][t];
//
//						// Decrementa il numero di task da eseguire rimasti della cella di destinazione / operazionale i,
//						// sottraendo quelli che possono essere eseguiti al momento dagli utenti della cella di origine j.
//						demand -= problem.n[m] * solution[j][i][m][t];
//						if (!notSatisfied && demand > 0)
//							cout << "SON PRESENTI ATTIVITA' NON ASSEGNATE";
//					}
//				}
//			}
//		}
//	}
//
//	// Dovrebbero essere invertiti per rispettare le specifiche del pdf dell'assignment
//	/*
//	stat.push_back(objFun);
//	stat.push_back((double)(clock() - tStart) / CLOCKS_PER_SEC);
//	*/
//	stat.push_back((double)(clock() - tStart) / CLOCKS_PER_SEC);
//	stat.push_back(objFun);
//
//	hasSolution = true;
//
//	// Stampa il risultato
//	cout << "Start time = " << tStart << "End time = " << clock() << "TimeDiff = " << (((double)clock() - tStart) / CLOCKS_PER_SEC) << "\t" << "Objective Function = " << objFun << "\n";
//}

void Heuristic::writeKPI(string path, string instanceName, vector<double> stat) {
	if (!hasSolution)
		return;

	ofstream fileO(path, ios::app);
	if (!fileO.is_open())
		return;

	fileO << instanceName << ";" << stat[0] << ";" << stat[1];
	for (int i = 2; i<stat.size(); i++)
		fileO << ";" << stat[i];
	fileO << endl;

	fileO.close();

}

void Heuristic::writeSolution(string path) {
	if (!hasSolution)
		return;

	ofstream fileO(path);
	if (!fileO.is_open())
		return;

	fileO << this->nCells << "; " << this->nTimeSteps << "; " << this->nCustomerTypes << endl;
	for (int m = 0; m < this->nCustomerTypes; m++)
		for (int t = 0; t < this->nTimeSteps; t++)
			for (int i = 0; i < this->nCells; i++)
				for (int j = 0; j < this->nCells; j++)
					if (solution[i][j][m][t] > 0)
						fileO << i << ";" << j << ";" << m << ";" << t << ";" << solution[i][j][m][t] << endl;

	fileO.close();
}

eFeasibleState Heuristic::isFeasible(string path) {

	string line;
	string word;
	int nCellsN;
	int nTimeStepsN;
	int nCustomerTypesN;
	int i, j, m, t;


	ifstream iffN(path.c_str());

	if (!iffN.is_open()) {
		cout << "Impossible to open" << path << endl;
		exit(1);
	}

	getline(iffN, line);
	std::replace(line.begin(), line.end(), ';', ' ');
	istringstream iss(line);
	iss >> word; // nCells
	nCellsN = atoi(word.c_str());
	iss >> word; // nTimeSteps
	nTimeStepsN = atoi(word.c_str());
	iss >> word; // nCustomerTypes
	nCustomerTypesN = atoi(word.c_str());

	int**** solutionN = new int***[nCells];
	for (i = 0; i < nCellsN; i++) {
		solutionN[i] = new int**[nCells];
		for (j = 0; j < nCellsN; j++) {
			solutionN[i][j] = new int*[nCustomerTypes];
			for (m = 0; m < nCustomerTypesN; m++) {
				solutionN[i][j][m] = new int[nTimeSteps];
				for (t = 0; t < nTimeStepsN; t++) {
					solutionN[i][j][m][t] = 0;
				}
			}
		}
	}

	while (getline(iffN, line)) {
		std::replace(line.begin(), line.end(), ';', ' ');
		istringstream iss(line);
		iss >> word; // i
		i = atoi(word.c_str());
		iss >> word; // j
		j = atoi(word.c_str());
		iss >> word; // m
		m = atoi(word.c_str());
		iss >> word; // t
		t = atoi(word.c_str());
		iss >> word; // value
		solutionN[i][j][m][t] = atoi(word.c_str());
	}

	// Demand
	bool feasible = true;
	int expr = 0;
	for (int i = 0; i < nCells; i++) {
		for (int j = 0; j < nCells; j++)
			for (int m = 0; m < nCustomerTypes; m++)
				for (int t = 0; t < nTimeSteps; t++)
					expr += problem.n[m] * solutionN[j][i][m][t];
		if (expr < problem.activities[i])
			feasible = false;
	}

	if (!feasible)
		return NOT_FEASIBLE_DEMAND;

	// Max Number of users
	for (int i = 0; i < nCells; i++)
		for (int m = 0; m < nCustomerTypes; m++)
			for (int t = 0; t < nTimeSteps; t++) {
				expr = 0;
				for (int j = 0; j < nCells; j++)
					expr += solutionN[i][j][m][t];
				if (expr > problem.usersCell[i][m][t])
					feasible = false;
			}

	if (!feasible)
		return NOT_FEASIBLE_USERS;

	return FEASIBLE;
}

void Heuristic::getStatSolution(vector<double>& stat) {
	if (!hasSolution)
		return;

	int* tipi = new int[nCustomerTypes];
	for (int m = 0; m < nCustomerTypes; m++)
		tipi[m] = 0;

	for (int i = 0; i < nCells; i++)
		for (int j = 0; j < nCells; j++)
			for (int t = 0; t < nTimeSteps; t++)
				for (int m = 0; m < nCustomerTypes; m++)
					if (solution[i][j][m][t] > 0)
						tipi[m] += solution[i][j][m][t];
	for (int m = 0; m < nCustomerTypes; m++)
		stat.push_back(tipi[m]);

}