#include <stdio.h>
#include "mpi.h"

int main(int argc, char *argv[]) {
	int numProcs, miId, n;
	MPI_Status s;
	MPI_Init(&argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
	MPI_Comm_rank(MPI_COMM_WORLD, &miId);

	if(miId==0){
		printf("Introduce un valor entero:\n");
		scanf("%d", &n);
		printf( "Soy %d y el valor leido es %d", miId, n);
	}
	else{
		printf( "Soy %d y el valor leido es %d", miId, n);
	}
	MPI_Finalize();
	return 0;
}
