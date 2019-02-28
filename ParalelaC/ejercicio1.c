#include <stdio.h>
#include "mpi.h"

int main(int argc, char *argv[]) {
	int numProcs, miId;

	MPI_Init(&argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
	MPI_Comm_rank(MPI_COMM_WORLD, &miId);

	if(miId<4)
		printf("Hola, soy el proceso <%d>!", miId);

	MPI_Finalize();
	return 0;
}
