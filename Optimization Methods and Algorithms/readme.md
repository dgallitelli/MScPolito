## Optimization Methods and Algorithms

This is the official repository of the project for OMA @ Politecnico di Torino, academic year 2016/17.

#### Working Group
- Tumolo Massimo (Leader)
- Gallitelli Davide
- Filannino Angelo
- Balsamo Enrico
- Montisci Daniele

## Temporary Section

Link to C++ backup copy: [MEGA](https://goo.gl/WRfgue)

#### Notes about the project

Obiettivi:
- Minimizzare i costi per noi
- Minimizzare i tempi

Variabili importanti da usare:
- problem[]
- usersCell[j][m][t]
- demand
- solution

Il nCustomerTypes non ci interessa sapere cosa voglia dire.

Ottimizzare il numero di accessi a memoria per ridurre i tempi. In particolare si parla degli accessi di matrici a più livelli.

#### Current understanding of the project

We're supposed to modify the "_SolveFast_" function in the heuristic.cpp file in order to produce a solution closer to the best possible solution. Said _solveFast_ function is a _void_ function, modifying a statistics array ([0] for _objVal_ and [1] for returing the actual computational time) and with a timeLimit for the computational time (should be under 5 secs).

#### Division of tasks and To-Do

The official board for the project is [here](https://trello.com/b/f24CneSg). Ask the administrator of the board for access.